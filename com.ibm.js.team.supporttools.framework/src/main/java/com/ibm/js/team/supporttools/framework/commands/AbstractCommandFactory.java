/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.framework.commands;

import java.util.Collection;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.framework.ICommand;

/**
 * Factory class dealing with commands. A new command has to be added to the
 * constructor.
 *
 */
public abstract class AbstractCommandFactory implements ICommandFactory {

	public static final Logger logger = LoggerFactory.getLogger(AbstractCommandFactory.class);
	private HashMap<String, ICommand> commandMap = new HashMap<String, ICommand>();

	/**
	 * Creates the list of supported commands
	 */
	public AbstractCommandFactory() {
		super();
		setAvailableCommands();
//		put(new ExportConfigurationsCmd());
//		put(new ExportConfigurationsByDescriptionCmd());
//		put(new ExportAllConfigurationsByDescriptionCmd());
//		put(new ImportTypeSystemCmd());
//		put(new DeliverTypeSystemCmd());
//		put(new ImportTypeSystemByDescriptionCmd());
//		put(new DeliverTypeSystemByDescriptionCmd());
		// Enable sample command
//		put(new SampleCommandCmd());
	}

	public abstract void setAvailableCommands();

	/**
	 * Add a command to the list of supported commands
	 * 
	 * @param cmd
	 */
	public void put(final ICommand cmd) {
		commandMap.put(cmd.getCommandName(), cmd);
	}

	/**
	 * Print the syntax for all supported commands
	 */
	public void printCommandSyntax() {
		Collection<ICommand> commands = commandMap.values();
		for (ICommand iCommand : commands) {
			iCommand.printSyntax();
			logger.info("\n");
		}
	}

	/**
	 * @return the commandMap, never null
	 */
	public HashMap<String, ICommand> getCommandMap() {
		return commandMap;
	}

	/**
	 * Get a command by its name
	 * 
	 * @param commandName
	 * @return command or null
	 */
	public ICommand getCommand(final String commandName) {
		return commandMap.get(commandName);
	}

}
