/*******************************************************************************
 * Copyright (c) 2015-2019 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.commands;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.SupportToolsFrameworkConstants;
import com.ibm.js.team.supporttools.framework.framework.AbstractTeamrepositoryCommand;
import com.ibm.js.team.supporttools.framework.framework.ICommand;
import com.ibm.js.team.supporttools.scm.ScmSupportToolsConstants;
import com.ibm.js.team.supporttools.scm.statistics.AnalyzeConnectionStatistics;
import com.ibm.team.repository.common.TeamRepositoryException;

/**
 * Allows to analyze a repository workspace, all its components and the current
 * SCM data.
 * 
 */
public class AnalyzeWorkspace extends AbstractTeamrepositoryCommand implements ICommand {

	public static final Logger logger = LoggerFactory.getLogger(AnalyzeWorkspace.class);
	private int fProgress = 0;

	/**
	 * Constructor, set the command name which will be used as option value for
	 * the command option. The name is used in the UIs and the option parser.
	 */
	public AnalyzeWorkspace() {
		super(ScmSupportToolsConstants.CMD_ANYLYZEWORKSPACECONNECTION);
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
		options.addOption(ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID, true,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_DESCRIPTION);
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

		if (!(cmd.hasOption(ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID))) {
			isValid = false;
		}
		return isValid;
	}

	/**
	 * Method to print the syntax in case of missing options.
	 */
	@Override
	public void printSyntax() {
		// Command name and description
		logger.info("{}", getCommandName());
		logger.info(ScmSupportToolsConstants.CMD_ANALYSE_WORKSPACECONNECTION_DESCRIPTION);
		// General syntax
		logger.info("\n\tSyntax: -{} {} -{} {} -{} {} -{} {} -{} {} ", SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				getCommandName(), SupportToolsFrameworkConstants.PARAMETER_URL,
				SupportToolsFrameworkConstants.PARAMETER_URL_PROTOTYPE, SupportToolsFrameworkConstants.PARAMETER_USER,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_PROTOTYPE,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_PROTOTYPE);
		// Parameter and description
		logger.info(
				"\n\tParameter description: \n\t -{} \t {} \n\t -{} \t{} \n\t -{} \t {} \n\t -{} \t {} \n\t -{} \t {}",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				SupportToolsFrameworkConstants.PARAMETER_COMMAND_DESCRIPTION,
				SupportToolsFrameworkConstants.PARAMETER_URL, SupportToolsFrameworkConstants.PARAMETER_URL_DESCRIPTION,
				SupportToolsFrameworkConstants.PARAMETER_USER,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_DESCRIPTION,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_DESCRIPTION,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_DESCRIPTION);
		// Examples
		logger.info("\n\tExample: -{} {} -{} {} -{} {} -{} {} -{} {}", SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				getCommandName(), SupportToolsFrameworkConstants.PARAMETER_URL,
				SupportToolsFrameworkConstants.PARAMETER_URL_EXAMPLE, SupportToolsFrameworkConstants.PARAMETER_USER,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_EXAMPLE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_EXAMPLE,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_EXAMPLE);
	}

	/**
	 * The main method that executes the behavior of this command.
	 * 
	 * @throws TeamRepositoryException
	 */
	@Override
	public boolean executeTeamRepositoryCommand() throws TeamRepositoryException {
		boolean result = false;
		String scmWorkspace = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID);
		try {
			AnalyzeConnectionStatistics stats = new AnalyzeConnectionStatistics(getTeamRepository(), getMonitor());

			result = stats.analyzeWorkspace(scmWorkspace);
			if (result) {
				logger.info("Show results...");
				stats.getConnectionStats().printConnectionStatistics();
				stats.getConnectionRangeStats().generateWorkBook(scmWorkspace+".xls");
			}
		} catch (IOException e) {
			logger.error("IOException: {}", e.getMessage());
		}
		return result;
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
