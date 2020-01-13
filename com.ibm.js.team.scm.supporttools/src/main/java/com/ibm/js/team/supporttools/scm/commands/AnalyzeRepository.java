/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.commands;

import java.net.URI;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.SupportToolsFrameworkConstants;
import com.ibm.js.team.supporttools.framework.framework.AbstractTeamrepositoryCommand;
import com.ibm.js.team.supporttools.framework.framework.ICommand;
import com.ibm.js.team.supporttools.scm.ScmSupportToolsConstants;
import com.ibm.js.team.supporttools.scm.statistics.RepositoryAnalyzer;
import com.ibm.js.team.supporttools.scm.utils.ConnectionUtil;
import com.ibm.team.process.client.IProcessItemService;
import com.ibm.team.process.common.IProcessArea;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.client.IWorkspaceConnection;
import com.ibm.team.scm.client.IWorkspaceManager;
import com.ibm.team.scm.client.SCMPlatform;
import com.ibm.team.scm.common.IWorkspaceHandle;
import com.ibm.team.scm.common.dto.IWorkspaceSearchCriteria;

/**
 * Allows to analyze a repository workspace, all its components and the current
 * SCM data of a workspace connection.
 * 
 */
public class AnalyzeRepository extends AbstractTeamrepositoryCommand implements ICommand {

	public static final Logger logger = LoggerFactory.getLogger(AnalyzeRepository.class);
	private int fProgress = 0;
	private String fOutputFolder = null;

	/**
	 * Constructor, set the command name which will be used as option value for
	 * the command option. The name is used in the UIs and the option parser.
	 */
	public AnalyzeRepository() {
		super(ScmSupportToolsConstants.CMD_ANYLYZE_REPOSITORY);
	}

	@Override
	public String getScenarioName() {
		return ScmSupportToolsConstants.EXPENSIVESCENARIO_SCMTOOLS + getCommandName();
	}

	/**
	 * Method to add the additional options this command requires.
	 */
	@Override
	public Options addTeamRepositoryCommandOptions(Options options) {
		options.addOption(ScmSupportToolsConstants.PARAMETER_SCM_SCOPE, true,
				ScmSupportToolsConstants.PARAMETER_SCM_SCOPE_DESCRIPTION);
		options.addOption(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER, true,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_DESCRIPTION);
		return options;
	}

	/**
	 * Method to check if the additional required options/parameters required to
	 * perform the command are available.
	 */
	@Override
	public boolean checkTeamreposiroyCommandParameters(CommandLine cmd) {
		// Check for required options
		boolean isValid = true;
		// Check for required parameters
		return isValid;
	}

	/**
	 * Method to print the syntax in case of missing options.
	 */
	@Override
	public void printSyntax() {
		// Command name and description
		logger.info("{}", getCommandName());
		logger.info(ScmSupportToolsConstants.CMD_ANYLYZE_REPOSITORY_DESCRIPTION);
		// General syntax
		logger.info("\n\tSyntax: -{} {} -{} {} -{} {} -{} {} -{} {} -{} {}", SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				getCommandName(), SupportToolsFrameworkConstants.PARAMETER_URL,
				SupportToolsFrameworkConstants.PARAMETER_URL_PROTOTYPE, SupportToolsFrameworkConstants.PARAMETER_USER,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_PROTOTYPE,
				ScmSupportToolsConstants.PARAMETER_SCM_SCOPE,
				ScmSupportToolsConstants.PARAMETER_SCM_SCOPE_PROTOTYPE,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_PROTOTYPE);
		
		// Parameter and description
		logger.info(
				"\n\tParameter description: \n\t -{} \t {} \n\t -{} \t{} \n\t -{} \t {} \n\t -{} \t {} \n\t -{} \t {} \n\t -{} \t {}",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				SupportToolsFrameworkConstants.PARAMETER_COMMAND_DESCRIPTION,
				SupportToolsFrameworkConstants.PARAMETER_URL, SupportToolsFrameworkConstants.PARAMETER_URL_DESCRIPTION,
				SupportToolsFrameworkConstants.PARAMETER_USER,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_DESCRIPTION,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_DESCRIPTION,
				ScmSupportToolsConstants.PARAMETER_SCM_SCOPE,
				ScmSupportToolsConstants.PARAMETER_SCM_SCOPE_DESCRIPTION,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_DESCRIPTION);
		// Examples
		logger.info("\n\tExample: -{} {} -{} {} -{} {} -{} {} -{} {} -{} {}", SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				getCommandName(), SupportToolsFrameworkConstants.PARAMETER_URL,
				SupportToolsFrameworkConstants.PARAMETER_URL_EXAMPLE, SupportToolsFrameworkConstants.PARAMETER_USER,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_EXAMPLE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_EXAMPLE,
				ScmSupportToolsConstants.PARAMETER_SCM_SCOPE,
				ScmSupportToolsConstants.PARAMETER_SCM_SCOPE_EXAMPLE,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_EXAMPLE);
	}

	/**
	 * The main method that executes the behavior of this command.
	 * 
	 * @throws TeamRepositoryException
	 */
	@Override
	public boolean executeTeamRepositoryCommand() throws TeamRepositoryException {
		boolean result = false;

		this.fOutputFolder = null;
		if (getCmd().hasOption(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER)) {
			fOutputFolder = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER);
		}

		result = analyzeRepository();
		return result;
	}

	private boolean analyzeRepository() throws TeamRepositoryException {
		boolean result = false;
		IWorkspaceSearchCriteria criteria = IWorkspaceSearchCriteria.FACTORY.newInstance();
		criteria.setKind(IWorkspaceSearchCriteria.STREAMS);

		String connectionOwnerScope = null;
		if (getCmd().hasOption(ScmSupportToolsConstants.PARAMETER_SCM_SCOPE)) {
			connectionOwnerScope = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_SCM_SCOPE);
		}
		if (connectionOwnerScope != null) {
			boolean scope = true;
			String[] processAreas = connectionOwnerScope.split("&");
			for (int i = 0; i < processAreas.length; i++) {
				String processArea = processAreas[i];
				IProcessArea area = findProcessArea(processArea);
				if (null == area) {
					logger.error("SCM Connection Owner Scope: unable to find process area '{}'", processArea);
					scope = false;
				} else {
					criteria.getFilterByOwnerOptional().add(area);
					// criteria.setExactOwnerName(arg0)
				}
			}
			if (!scope) {
				logger.error("Check SCM Connection Owner Scope");
				return false;
			}
		}
		List<IWorkspaceHandle> connections = findConnections(criteria);
		List<? extends IWorkspaceConnection> connection = ConnectionUtil.getWorkspaceConnections(getTeamRepository(),
				connections, getMonitor());
		RepositoryAnalyzer repoAnalyzer = new RepositoryAnalyzer(getTeamRepository(), this.fOutputFolder, getMonitor());
		result = repoAnalyzer.analyze(connection);
		return result;
	}

	/**
	 */
	private List<IWorkspaceHandle> findConnections(IWorkspaceSearchCriteria criteria) throws TeamRepositoryException {
		IWorkspaceManager wm = SCMPlatform.getWorkspaceManager(getTeamRepository());
		List<IWorkspaceHandle> connections = wm.findWorkspaces(criteria, Integer.MAX_VALUE, getMonitor());
		return connections;
	}

	public IProcessArea findProcessArea(String name) throws TeamRepositoryException {
		IProcessItemService service = (IProcessItemService) getTeamRepository()
				.getClientLibrary(IProcessItemService.class);
		URI uri = URI.create(name.replaceAll(" ", "%20"));
		IProcessArea area = service.findProcessArea(uri, IProcessItemService.ALL_PROPERTIES, getMonitor());
		return area;
	}

	/**
	 * This prints one '.' for every for 10 times it is called to show some
	 * progress. Can be used to show more fine grained progress.
	 */
	@SuppressWarnings("unused")
	private void showProgress() {
		fProgress++;
		if (fProgress > 8) {
			System.out.print(".");
			fProgress = 0;
		}
	}
}
