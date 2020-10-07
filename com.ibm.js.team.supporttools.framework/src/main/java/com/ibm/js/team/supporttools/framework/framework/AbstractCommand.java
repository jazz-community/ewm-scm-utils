/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.framework.framework;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract class representing the basic workflow of a command.
 */
public abstract class AbstractCommand implements ICommand {

	public static final Logger logger = LoggerFactory.getLogger(AbstractCommand.class);
	private CommandLine cmd = null;
	private String commandName;
	private int fProgress = 0;

	/**
	 * @param commandName
	 *            name of the command
	 */
	public AbstractCommand(final String commandName) {
		super();
		this.commandName = commandName;
	}

	/**
	 * @return the parsed commandLine
	 */
	public CommandLine getCmd() {
		return cmd;
	}

	/**
	 * @return the name of the command. This is used to find the command that
	 *         needs to be called.
	 */
	public String getCommandName() {
		return this.commandName;
	}

	/**
	 * Execute the workflow
	 * 
	 * @return true if the the execution succeeded.
	 */
	public boolean run(Options options, final String[] args) {
		try {
			options = addCommandOptions(options);
			// Parse the command line
			CommandLineParser cliParser = new GnuParser();
			this.cmd = cliParser.parse(options, args);
			if (!checkParameters(cmd)) {
				logger.error("Missing required parameters.");
				printSyntax();
				return false;
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		// System.exit is done outside.
		return execute();
	}

	/**
	 * Add the parser options for the subclass. Method to be implemented in
	 * subclasses.
	 * 
	 * @param options
	 * @return
	 */
	public abstract Options addCommandOptions(Options options);

	/**
	 * Validate the parameters for the command. Method to be implemented in
	 * subclasses.
	 * 
	 * @param cmd
	 * @return true if the required parameters are available.
	 */
	public abstract boolean checkParameters(final CommandLine cmd);

	/**
	 * Used to print the syntax of a command. Method to be implemented in
	 * subclasses.
	 */
	public abstract void printSyntax();

	/**
	 * Execute the command. Implement this method to create the desired
	 * behavior. Method to be implemented in subclasses.
	 * 
	 * @return
	 */
	public abstract boolean execute();
	
	/**
	 * This prints one '.' for every for 10 times it is called to show some
	 * progress. Can be used to show more fine grained progress.
	 */
	public void showProgress() {
		fProgress++;
		if (fProgress % 10 == 9) {
			System.out.print(".");
		}
	}


}
