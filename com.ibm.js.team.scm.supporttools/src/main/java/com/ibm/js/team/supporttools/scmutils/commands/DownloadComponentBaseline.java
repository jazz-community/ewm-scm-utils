/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scmutils.commands;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.SupportToolsFrameworkConstants;
import com.ibm.js.team.supporttools.framework.framework.AbstractTeamrepositoryCommand;
import com.ibm.js.team.supporttools.framework.framework.ICommand;
import com.ibm.js.team.supporttools.framework.framework.IProgress;
import com.ibm.js.team.supporttools.framework.util.TimeStampUtil;
import com.ibm.js.team.supporttools.scmutils.ScmSupportToolsConstants;
import com.ibm.js.team.supporttools.scmutils.utils.ComponentUtil;
import com.ibm.js.team.supporttools.scmutils.utils.ConnectionUtil;
import com.ibm.js.team.supporttools.scmutils.utils.ComponentConfigurationDownloadUtil;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.client.IBaselineConnection;
import com.ibm.team.scm.client.IWorkspaceConnection;
import com.ibm.team.scm.client.IWorkspaceManager;
import com.ibm.team.scm.client.SCMPlatform;
import com.ibm.team.scm.common.IBaselineHandle;
import com.ibm.team.scm.common.IComponentHandle;
import com.ibm.team.scm.common.dto.IBaselineSearchCriteria;

/**
 * Command to download a component baseline into a local folder on disk.
 * 
 */
public class DownloadComponentBaseline extends AbstractTeamrepositoryCommand implements ICommand, IProgress {

	public static final Logger logger = LoggerFactory.getLogger(DownloadComponentBaseline.class);
	private String fRootFolder = null;

	/**
	 * Constructor, set the command name which will be used as option value for
	 * the command option. The name is used in the UIs and the option parser.
	 */
	public DownloadComponentBaseline() {
		super(ScmSupportToolsConstants.CMD_DOWNLOAD_COMPONENT_BASELINE);
	}

	/**
	 * Method to add the options this command requires.
	 */
	@Override
	public Options addTeamRepositoryCommandOptions(Options options) {
		options.addOption(ScmSupportToolsConstants.PARAMETER_COMPONENTNAME, true,
				ScmSupportToolsConstants.PARAMETER_COMPONENTNAME_DESCRIPTION);
		options.addOption(ScmSupportToolsConstants.PARAMETER_BASELINENAME, true,
				ScmSupportToolsConstants.PARAMETER_BASELINENAME_DESCRIPTION);		
		options.addOption(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER, true,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_DESCRIPTION);
		return options;
	}

	/**
	 * Method to check if the required options/parameters required to perform
	 * the command are available.
	 */
	@Override
	public boolean checkTeamreposiroyCommandParameters(CommandLine cmd) {
		// Check for required options
		boolean isValid = true;

		if (!(cmd.hasOption(ScmSupportToolsConstants.PARAMETER_COMPONENTNAME)
				&& cmd.hasOption(ScmSupportToolsConstants.PARAMETER_BASELINENAME)
				&& cmd.hasOption(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER))) {
			isValid = false;
		}
		return isValid;
	}

	@Override
	public String getScenarioName() {
		return ScmSupportToolsConstants.EXPENSIVESCENARIO_SCMTOOLS + getCommandName();
	}

	/**
	 * Method to print the syntax in case of missing options.
	 */
	@Override
	public void printSyntax() {
		// Command name and description
		logger.info("{}", getCommandName());
		// General syntax
		logger.info("\n\tSyntax : -{} {} -{} {} -{} {} -{} {} -{} {} -{} {} -{} {}",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND, getCommandName(),
				SupportToolsFrameworkConstants.PARAMETER_URL, SupportToolsFrameworkConstants.PARAMETER_URL_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_USER,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_PROTOTYPE,
				ScmSupportToolsConstants.PARAMETER_COMPONENTNAME,
				ScmSupportToolsConstants.PARAMETER_COMPONENTNAME_PROTOTYPE,
				ScmSupportToolsConstants.PARAMETER_BASELINENAME,
				ScmSupportToolsConstants.PARAMETER_BASELINENAME_PROTOTYPE, 
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_PROTOTYPE);
		// Parameter and description
		logger.info(
				"\n\tParameter description: \n\t -{} \t {} \n\t -{} \t{} \n\t -{} \t {} \n\t -{} \t {} \n\t -{} \t {} \n\t -{} \t {} \n\t -{} \t {}",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				SupportToolsFrameworkConstants.PARAMETER_COMMAND_DESCRIPTION,
				SupportToolsFrameworkConstants.PARAMETER_URL, SupportToolsFrameworkConstants.PARAMETER_URL_DESCRIPTION,
				SupportToolsFrameworkConstants.PARAMETER_USER,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_DESCRIPTION,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_DESCRIPTION,
				ScmSupportToolsConstants.PARAMETER_COMPONENTNAME,
				ScmSupportToolsConstants.PARAMETER_COMPONENTNAME_PROTOTYPE,				
				ScmSupportToolsConstants.PARAMETER_BASELINENAME,
				ScmSupportToolsConstants.PARAMETER_BASELINENAME_DESCRIPTION,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_DESCRIPTION);
		// Optional parameters
//		logger.info("\n\tOptional parameter syntax: -{} {} -{} -{}",
//				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER,
//				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER_PROTOTYPE,
//				ScmSupportToolsConstants.PARAMETER_REUSE_EXISTING_WORKSPACE_FLAG,
//				ScmSupportToolsConstants.PARAMETER_SKIP_UPLOADING_EXISTING_COMPONENT_FLAG);
		// Optional parameters description
//		logger.info("\n\tOptional parameter description: \n\t -{} \t{} \n\t -{} \t {}\n\t -{} \t {}",
//				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER,
//				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER_DESCRIPTION,
//				ScmSupportToolsConstants.PARAMETER_REUSE_EXISTING_WORKSPACE_FLAG,
//				ScmSupportToolsConstants.PARAMETER_REUSE_EXISTING_WORKSPACE_FLAG_DESCRIPTION,
//				ScmSupportToolsConstants.PARAMETER_SKIP_UPLOADING_EXISTING_COMPONENT_FLAG,
//				ScmSupportToolsConstants.PARAMETER_SKIP_UPLOADING_EXISTING_COMPONENT_FLAG_DESCRIPTION);
		// Examples
		logger.info("\n\tExample: -{} {} -{} {} -{} {} -{} {} -{} {} -{} {} -{} {}",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND, getCommandName(),
				SupportToolsFrameworkConstants.PARAMETER_URL, SupportToolsFrameworkConstants.PARAMETER_URL_EXAMPLE,
				SupportToolsFrameworkConstants.PARAMETER_USER, SupportToolsFrameworkConstants.PARAMETER_USER_ID_EXAMPLE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_EXAMPLE,
				ScmSupportToolsConstants.PARAMETER_COMPONENTNAME,
				ScmSupportToolsConstants.PARAMETER_COMPONENTNAME_EXAMPLE,
				ScmSupportToolsConstants.PARAMETER_BASELINENAME,
				ScmSupportToolsConstants.PARAMETER_BASELINENAME_EXAMPLE, 
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_EXAMPLE);
		// Optional parameter examples
//		logger.info("\n\tExample optional parameter: -{} {} -{} -{}",
//				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER,
//				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER_EXAMPLE,
//				ScmSupportToolsConstants.PARAMETER_REUSE_EXISTING_WORKSPACE_FLAG,
//				ScmSupportToolsConstants.PARAMETER_SKIP_UPLOADING_EXISTING_COMPONENT_FLAG);
	}

	/**
	 * The main method that executes the behavior of this command.
	 */
	@Override
	public boolean executeTeamRepositoryCommand() throws TeamRepositoryException {
		boolean result = false;
		// Execute the code
		// Get all the option values
		final String componentName = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_COMPONENTNAME);
		final String baselineName = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_BASELINENAME);
		final String basePath = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER);
		try {
			result = downloadComponentBaseline(getTeamRepository(), componentName, baselineName, basePath,  getMonitor());
		} catch (IOException e) {
			logger.error("IOException: {}", e.getMessage());
		}
		return result;
	}

	/**
	 * Import the repository workspace information.
	 * 
	 * @param teamRepository
	 * @param projectAreaName
	 * @param scmConnection
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	private boolean downloadComponentBaseline(ITeamRepository teamRepository, String componentName, String baselineName, String basePath,
			IProgressMonitor monitor)
			throws TeamRepositoryException, UnsupportedEncodingException, FileNotFoundException, IOException {
		boolean result= false;
		IWorkspaceConnection tempWorkspace = null;
		IWorkspaceManager wm = SCMPlatform.getWorkspaceManager(teamRepository);
		try{
		
		Date now = new Date();
		String userID= teamRepository.loggedInContributor().getUserId();
		String timeStampNow = TimeStampUtil.getDate(now, null);
		String tempWorkspaceName = "Download Workspace " + userID + " " + timeStampNow;
		
		tempWorkspace = wm.createWorkspace(teamRepository.loggedInContributor(), tempWorkspaceName,
				"Temporary workspace to download a component baseline." + tempWorkspaceName, monitor);
		
		fRootFolder = basePath;
		logger.info("Find component...");
		IComponentHandle componentHandle = ComponentUtil.findComponentByName(wm, componentName, monitor); // Find the component
		logger.info("Find component baseline...");
        IBaselineSearchCriteria criteria = IBaselineSearchCriteria.FACTORY.newInstance().setComponentRequired(componentHandle);
        criteria.setExactName(baselineName);

		List<IBaselineHandle> baselines = wm.findBaselines(criteria, 4, monitor);
		if(baselines.size()==0){
			logger.info("Baseline '{}' not found...", baselineName);
			return false;		
		}
		if(baselines.size()>1){
			logger.info("Baseline name '{}' not unique...", baselineName);
			return false;		
		}
		
		IBaselineHandle baselineHandle = baselines.get(0);
		IBaselineConnection baselineConnection = wm.getBaselineConnection(baselineHandle, monitor);
		if(!ConnectionUtil.isComponentInWorkspace(tempWorkspace,componentHandle)){
			ConnectionUtil.addComponentToWorkspaceConnection(tempWorkspace, componentHandle, monitor);
		}

		logger.info("Set Active Baseline...");
		tempWorkspace.applyComponentOperations(Collections.singletonList(tempWorkspace
				.componentOpFactory().replaceComponent(componentHandle,
						baselineConnection, false)), monitor);
		logger.info("Set Active Baseline successful...");


		logger.info("Downloading Component Baseline...");
		ComponentConfigurationDownloadUtil downloader = new ComponentConfigurationDownloadUtil(ComponentConfigurationDownloadUtil.ExportMode.PRESERVE, this);
		downloader.download(teamRepository, tempWorkspace, componentHandle, fRootFolder, monitor);
		logger.info("Downloading Component Baseline successful...");
		result=true;
		return result;
		} finally {
			if(tempWorkspace!=null){
				wm.deleteWorkspace(tempWorkspace.getResolvedWorkspace(), monitor);				
			}
		}
	}
}
