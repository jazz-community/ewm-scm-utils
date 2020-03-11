/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.framework.commands;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.SupportToolsFrameworkConstants;
import com.ibm.js.team.supporttools.framework.framework.AbstractCommand;
import com.ibm.js.team.supporttools.framework.framework.ICommand;

/**
 * 
 * This class represents a minimal sample for a custom command.
 * 
 * To extend the tool with a new command, you must implement {@link ICommand }.
 * 
 * The easiest way to do this is to create a subclass of {@link AbstractCommand}
 * and implement the abstract methods.
 * 
 * To finally enable this command, add this to {@link CommandFactory} in the
 * constructor like below for this sample class.
 * 
 * {@code
 * 
 * private CommandFactory() { super(); put(new ExportConfigurationsCmd());
 * put(new ImportTypeSystemCmd()); put(new DeliverTypeSystemCmd()); put(new
 * SampleCommand()); } }
 * 
 * to finalize the integration.
 * 
 */
public class SampleCommandCmd extends AbstractCommand implements ICommand {

	public static final Logger logger = LoggerFactory.getLogger(SampleCommandCmd.class);

	/**
	 * Constructor, set the command name which will be used as option value for
	 * the command option. The name is used in the UIs and the option parser.
	 */
	public SampleCommandCmd() {
		super(SupportToolsFrameworkConstants.CMD_SAMPLE);
	}

	/**
	 * Method to add the options this command requires.
	 */
	@Override
	public Options addCommandOptions(Options options) {
		// Add Options required for the command
		//
		// Example code
		options.addOption(SupportToolsFrameworkConstants.PARAMETER_SAMPLE_OPTION, true,
				SupportToolsFrameworkConstants.PARAMETER_SAMPLE_OPTION_DESCRIPTION);
		options.addOption(SupportToolsFrameworkConstants.PARAMETER_SAMPLE_OPTION_OPT, true,
				SupportToolsFrameworkConstants.PARAMETER_SAMPLE_OPTION_OPT_DESCRIPTION);
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

		if (!cmd.hasOption(SupportToolsFrameworkConstants.PARAMETER_SAMPLE_OPTION)) {
			isValid = false;
		}
		return isValid;
	}

	/**
	 * Method to print the syntax in case of missing options.
	 */
	@Override
	public void printSyntax() {
		// Print syntax hint for the command
		//
		// Example code
		logger.info("{}", getCommandName());
		logger.info("\n\tA sample command that can be used as template for adding custom commands.");
		logger.info("\n\tSyntax : -{} {} -{} {} [ -{} {} ]", SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				getCommandName(), SupportToolsFrameworkConstants.PARAMETER_SAMPLE_OPTION,
				SupportToolsFrameworkConstants.PARAMETER_SAMPLE_OPTION_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_SAMPLE_OPTION_OPT,
				SupportToolsFrameworkConstants.PARAMETER_SAMPLE_OPTION_OPT_PROTOTYPE);
		logger.info("\tExample: -{} {} -{} {}", SupportToolsFrameworkConstants.PARAMETER_COMMAND, getCommandName(),
				SupportToolsFrameworkConstants.PARAMETER_SAMPLE_OPTION,
				SupportToolsFrameworkConstants.PARAMETER_SAMPLE_OPTION_EXAMPLE);
		logger.info("\tOptional parameter: -{} {}", SupportToolsFrameworkConstants.PARAMETER_SAMPLE_OPTION_OPT,
				SupportToolsFrameworkConstants.PARAMETER_SAMPLE_OPTION_OPT_PROTOTYPE);
		logger.info("\tExample optional parameter: -{} {}", SupportToolsFrameworkConstants.PARAMETER_SAMPLE_OPTION_OPT,
				SupportToolsFrameworkConstants.PARAMETER_SAMPLE_OPTION_OPT_EXAMPLE);
	}

	/**
	 * The main method that executes the behavior of this command.
	 */
	@SuppressWarnings("unused")
	@Override
	public boolean execute() {
		logger.info("Executing Command {}", this.getCommandName());
		boolean result = false;
		// Execute the code
		// Get all the option values
		String mandatoryOption = getCmd().getOptionValue(SupportToolsFrameworkConstants.PARAMETER_SAMPLE_OPTION);
		String optionalOption = getCmd().getOptionValue(SupportToolsFrameworkConstants.PARAMETER_SAMPLE_OPTION_OPT);

		/**
		 * TODO: Your code goes here
		 * 
		 */
		result = true;

		return result;
	}
}
