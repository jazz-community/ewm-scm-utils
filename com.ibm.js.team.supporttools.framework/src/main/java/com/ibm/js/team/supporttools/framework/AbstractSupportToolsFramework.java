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
package com.ibm.js.team.supporttools.framework;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.commands.ICommandFactory;
import com.ibm.js.team.supporttools.framework.framework.ICommand;

/**
 * Main class that is called and controls the execution.
 * 
 * @see https://jazz.net/wiki/bin/view/Main/DNGServerAPI
 *
 */
public abstract class AbstractSupportToolsFramework {

	public static final Logger logger = LoggerFactory.getLogger(AbstractSupportToolsFramework.class);
	
	ICommandFactory commandFactory = null;

public AbstractSupportToolsFramework(ICommandFactory commandFactory) {
		super();
		this.commandFactory = commandFactory;
	}

	/**
	 * Execute the command
	 * 
	 * @param args
	 * @throws ParseException
	 */
	public boolean execute(final String[] args) throws ParseException {
		boolean result = false;
		CommandLine cmd = null;
		Options options = new Options();
		options.addOption(SupportToolsFrameworkConstants.PARAMETER_COMMAND, true,
				SupportToolsFrameworkConstants.PARAMETER_COMMAND_EXAMPLE);
		try {

			// Parse the command line
			CommandLineParser cliParser = new GnuParser();
			// ignore unrecognized options, we only care for other issues,
			cmd = cliParser.parse(options, args, true);
		} catch (ParseException e) {
			if (e instanceof MissingArgumentException) {
				logger.error("Missing command \n" + "Syntax: -command commandName {[-parameter] [parameterValue]}");
				printSupportedCommands();
				return result;
			}
			if (!(e instanceof UnrecognizedOptionException)) {
				logger.info("Failed.");
				throw (e);
			}
		}

		// Get the command name provided as option
		String command = cmd.getOptionValue(SupportToolsFrameworkConstants.PARAMETER_COMMAND);
		if (command == null) {
			logger.error("Missing command \n\nSyntax: -command commandName {[-parameter] [parameterValue]}");
			printSupportedCommands();
			return result;
		}

		// get the class to execute
		ICommand execCommand = getCommandFactory().getCommandMap().get(command);
		if (execCommand == null) {
			logger.error("Unsupported command name '{}' \n", command);
			printSupportedCommands();
			return result;
		}

		// run the command
		result = execCommand.run(options, args);
		return result;
	}

	/**
	 * Print the syntax.
	 */
	public void printSupportedCommands() {
		logger.error("Available commands: \n");
		getCommandFactory().printCommandSyntax();
	}
	
	public ICommandFactory getCommandFactory() {
		return commandFactory;
	}
}
