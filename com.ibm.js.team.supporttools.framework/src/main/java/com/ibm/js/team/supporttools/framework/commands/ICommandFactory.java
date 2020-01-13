/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.framework.commands;

import java.util.HashMap;

import com.ibm.js.team.supporttools.framework.framework.ICommand;

public interface ICommandFactory {
	/**
	 * Add a command to the list of supported commands
	 * 
	 * @param cmd
	 */
	public void put(final ICommand cmd);

	/**
	 * Print the syntax for all supported commands
	 */
	public void printCommandSyntax();

	/**
	 * @return the commandMap, never null
	 */
	public HashMap<String, ICommand> getCommandMap();

	/**
	 * Get a command by its name
	 * 
	 * @param commandName
	 * @return command or null
	 */
	public ICommand getCommand(String commandName);
}