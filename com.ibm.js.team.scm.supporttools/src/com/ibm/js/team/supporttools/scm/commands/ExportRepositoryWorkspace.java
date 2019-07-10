/*******************************************************************************
 * Copyright (c) 2012 - 2013, 2018 IBM Corporation.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *
 *    Ralph Schoon - Initial implementation
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.commands;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.jena.sparql.function.library.leviathan.log;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.SupportToolsFrameworkConstants;
import com.ibm.js.team.supporttools.framework.framework.AbstractCommand;
import com.ibm.js.team.supporttools.framework.framework.ICommand;
import com.ibm.js.team.supporttools.scm.ScmSupportToolsConstants;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.common.TeamRepositoryException;

/**
 */
public class ExportRepositoryWorkspace extends AbstractCommand implements ICommand {

	public static final Logger logger = LoggerFactory.getLogger(ExportRepositoryWorkspace.class);

	/**
	 * Constructor, set the command name which will be used as option value for the
	 * command option. The name is used in the UIs and the option parser.
	 */
	public ExportRepositoryWorkspace() {
		super(ScmSupportToolsConstants.CMD_EXPORTWORKSPACE);
	}

	/**
	 * Method to add the options this command requires.
	 */
	@Override
	public Options addCommandOptions(Options options) {
		options.addOption(SupportToolsFrameworkConstants.PARAMETER_URL, true,
				SupportToolsFrameworkConstants.PARAMETER_URL_DESCRIPTION);
		options.addOption(SupportToolsFrameworkConstants.PARAMETER_USER, true,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_DESCRIPTION);
		options.addOption(SupportToolsFrameworkConstants.PARAMETER_PASSWORD, true,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_DESCRIPTION);
		options.addOption(ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID, true,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_DESCRIPTION);
		options.addOption(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER, true,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_DESCRIPTION);
		return options;
	}

	/**
	 * Method to check if the required options/parameters required to perform the
	 * command are available.
	 */
	@Override
	public boolean checkParameters(CommandLine cmd) {
		// Check for required options
		boolean isValid = true;

		if (!(cmd.hasOption(SupportToolsFrameworkConstants.PARAMETER_URL)
				&& cmd.hasOption(SupportToolsFrameworkConstants.PARAMETER_USER)
				&& cmd.hasOption(SupportToolsFrameworkConstants.PARAMETER_PASSWORD)
				&& cmd.hasOption(ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID)
				&& cmd.hasOption(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER)
				)) {
			isValid = false;
		}
		return isValid;
	}

	/**
	 * Method to print the syntax in case of missing options.
	 */
	@Override
	public void printSyntax() {
		logger.info("{}", getCommandName());
		logger.info(
				"\n\tExports the contents of a repository workspace into a set of zip files.");
		logger.info("\n\tSyntax : -{} {} -{} {} -{} {} -{} {} -{} {} [ -{} {} ]",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND, getCommandName(),
				SupportToolsFrameworkConstants.PARAMETER_URL,
				SupportToolsFrameworkConstants.PARAMETER_URL_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_USER,
				SupportToolsFrameworkConstants.PARAMETER_USER_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_PROTOTYPE,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_PROTOTYPE,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_PROTOTYPE
//				,
//				SupportToolsFrameworkConstants.PARAMETER_CSV_FILE_PATH,
//				SupportToolsFrameworkConstants.PARAMETER_CSV_FILE_PATH_PROTOTYPE,
//				SupportToolsFrameworkConstants.PARAMETER_CSV_DELIMITER,
//				SupportToolsFrameworkConstants.PARAMETER_CSV_DELIMITER_PROTOTYPE
				);
		logger.info("\tExample: -{} {} -{} {} -{} {} -{} {} -{} {}", SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				getCommandName(), SupportToolsFrameworkConstants.PARAMETER_URL,
				SupportToolsFrameworkConstants.PARAMETER_URL_EXAMPLE, SupportToolsFrameworkConstants.PARAMETER_USER,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_EXAMPLE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_EXAMPLE,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_EXAMPLE,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_EXAMPLE
//				,
//				SupportToolsFrameworkConstants.PARAMETER_CSV_FILE_PATH,
//				SupportToolsFrameworkConstants.PARAMETER_CSV_FILE_PATH_EXAMPLE
				);

		logger.info("\tOptional parameter: -{} {}"
//				, 
//				SupportToolsFrameworkConstants.PARAMETER_CSV_DELIMITER,
//				SupportToolsFrameworkConstants.PARAMETER_CSV_DELIMITER_PROTOTYPE
				);
		logger.info("\tExample optional parameter: -{} {}"
//				, 
//				SupportToolsFrameworkConstants.PARAMETER_CSV_DELIMITER,
//				SupportToolsFrameworkConstants.PARAMETER_CSV_DELIMITER_EXAMPLE
				);
	}

	/**
	 * The main method that executes the behavior of this command.
	 */
	@SuppressWarnings("unused")
	@Override
	public boolean execute() {
		logger.info("Executing Command {}" , this.getCommandName());
		boolean result = false;
		// Execute the code
		// Get all the option values
		String repositoryURI = getCmd().getOptionValue(SupportToolsFrameworkConstants.PARAMETER_URL);
		String userId = getCmd().getOptionValue(SupportToolsFrameworkConstants.PARAMETER_USER);
		String userPassword = getCmd().getOptionValue(SupportToolsFrameworkConstants.PARAMETER_PASSWORD);
		String scmWorkspace = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID);
		String outputFolderPath = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER);

		TeamPlatform.startup();
		try {
			IProgressMonitor monitor = new NullProgressMonitor();
			ITeamRepository teamRepository = TeamPlatform
					.getTeamRepositoryService().getTeamRepository(repositoryURI);
			teamRepository.registerLoginHandler(new ITeamRepository.ILoginHandler() {
				public ILoginInfo challenge(ITeamRepository repository) {
					return new ILoginInfo() {
						public String getUserId() {
							return userId;
						}

						public String getPassword() {
							return userPassword;
						}
					};
				}
			});
			teamRepository.login(monitor);
			File outputfolder = new File(outputFolderPath);
			if(!outputfolder.exists()) {
				logger.error("Error: Outputfolder '{}' does not exist.", outputFolderPath);
				return result;
			}
			if(!outputfolder.isDirectory()) {
				logger.error("Error: Outputfolder '{}' is not a directory.", outputFolderPath);
				return result;
			}
			result = exportWorkspace(teamRepository,outputfolder,scmWorkspace,monitor);
		} catch (TeamRepositoryException e) {
			System.out.println("TeamRepositoryException: " + e.getMessage());
			e.printStackTrace();
		} finally {
			TeamPlatform.shutdown();
		}
		
		return result;
	}

	private boolean exportWorkspace(ITeamRepository teamRepository, File outputfolder, String scmWorkspace, IProgressMonitor monitor) throws TeamRepositoryException{
		boolean result = false;
		
		result=true;
		return result;
	}
}
