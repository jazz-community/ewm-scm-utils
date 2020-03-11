/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.SupportToolsFrameworkConstants;
import com.ibm.js.team.supporttools.framework.framework.AbstractCommand;
import com.ibm.js.team.supporttools.framework.framework.ICommand;
import com.ibm.js.team.supporttools.scm.ScmSupportToolsConstants;
import com.ibm.js.team.supporttools.scm.changegeneration.ExternalChangeGenerator;

/**
 * Experimental command to generate artificial I/O load on sandboxes that are tracked in RTC, especially 
 * when external file changes are tracked.
 * 
 */
public class GenerateExternalChanges extends AbstractCommand implements ICommand {

	public static final Logger logger = LoggerFactory.getLogger(GenerateExternalChanges.class);

	/**
	 * Constructor, set the command name which will be used as option value for
	 * the command option. The name is used in the UIs and the option parser.
	 */
	public GenerateExternalChanges() {
		super(ScmSupportToolsConstants.CMD_GENERATE_EXTERNAL_CHANGES);
	}

	/**
	 * Method to add the options this command requires.
	 */
	@Override
	public Options addCommandOptions(Options options) {
		options.addOption(ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER, true,
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER_DESCRIPTION);
		return options;
	}

	/**
	 * Method to check if the required options/parameters required to perform
	 * the command are available.
	 */
	@Override
	public boolean checkParameters(CommandLine cmd) {
		// Check for required options
		boolean isValid = true;

		if (!(cmd.hasOption(ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER))) {
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
		logger.info(ScmSupportToolsConstants.CMD_GENERATE_EXTERNAL_CHANGES_DESCRIPTION);
		// General syntax
		logger.info("\n\tSyntax: -{} {} -{} {}", SupportToolsFrameworkConstants.PARAMETER_COMMAND, getCommandName(),
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER,
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER_PROTOTYPE);
		// Parameter and description
		logger.info("\n\tParameter description: \n\t -{} \t {} \n\t -{} \t{}",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				SupportToolsFrameworkConstants.PARAMETER_COMMAND_DESCRIPTION,
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER,
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER_DESCRIPTION);
		// Examples
		logger.info("\n\tExample: -{} {} -{} {}", SupportToolsFrameworkConstants.PARAMETER_COMMAND, getCommandName(),
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER,
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER_EXAMPLE);
	}

	/**
	 * The main method that executes the behavior of this command.
	 */
	@Override
	public boolean execute() {
		logger.info("Executing Command {}", this.getCommandName());
		boolean result = false;
		// Execute the code
		// Get all the option values
		String sandboxFolderPath = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER);
		ExternalChangeGenerator changeGen = new ExternalChangeGenerator(sandboxFolderPath);
		result = changeGen.generateLoad();
		return result;
	}

}
