/*******************************************************************************
 * Copyright (c) 2015-2019 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.framework.framework;

import org.apache.commons.cli.Options;

/**
 * Interface that commands must implement.
 *
 */
public interface ICommand {

	/**
	 * @return the command name of the current command
	 */
	public String getCommandName();

	/**
	 * The command is supposed to log info its syntax.
	 */
	public void printSyntax();

	/**
	 * Method to run the command
	 * 
	 * @param options Options to add the specific options for a command
	 * @param args    the arguments of this call
	 * @return true if command was successful, false otherwise.
	 */
	public boolean run(Options options, final String[] args);

}
