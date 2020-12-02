/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scmutils.utils;

import java.io.File;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 */
public class AppLauncherUtil {
	
	public static final Logger logger = LoggerFactory.getLogger(AppLauncherUtil.class);
	
	public static void executeBatch(File executable, String parameter,
			String[] environment, File workingDir, boolean synchronous) {
		String[] parameters = parameter.split(" ");
		executeBatch(executable, parameters, environment, workingDir, synchronous);
	}

	public static int executeBatch(File executable, String[] args,
			String[] environment, File workingDir, boolean synchronous) {
		if(synchronous){
			return executeBatchSynchronous(executable, args, environment, workingDir);			
		} else {
			return -1;
			// TODO
		}
	}

	
	public static int  executeBatchSynchronous(File executable, String[] args,
			String[] environment, File workingDir) {
		int result = -1;
		try {

			ProcessBuilder builder = new ProcessBuilder();
			builder.directory(workingDir);

			ArrayList<String> command = new ArrayList<String>();

			command.add("cmd.exe");
			command.add("/C");
			command.add("start");
			command.add("/wait");
			/**
			 * Add the Executable
			 */
			String commandFile = executable.getAbsolutePath().toString();

			/**
			 * Add the additional parameters to the command. Split by
			 * whitespace.
			 * 
			 * TODO: support blanks in paths and parameters using quotes on
			 * windows
			 */
			command.add(commandFile);
			String output = "";
			for (int i = 0; i < args.length; i++) {
				command.add(args[i]);
				output += (String) args[i] + " ";
			}
			logger.info("Launching: " + commandFile);
			logger.info("Using: " + output);

			/**
			 * Launch the command
			 */
			builder.command(command);
			builder.directory(workingDir);

			/**
			 * Use the builder to run the process.
			 * 
			 * @see com.ibm.team.filesystem.client.restproxy.Discovery2
			 */
			Process proc = null;
			try {
				builder.redirectErrorStream(true);
				proc = builder.start();
				result = proc.waitFor();
				return result;
			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getLocalizedMessage());
			}
			//proc.destroyForcibly();

		} catch (Exception e) {
			logger.error("Exception Executing:", e);
		}
		return result;
	}

}
