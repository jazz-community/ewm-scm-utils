/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm;

import java.net.URISyntaxException;

import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.AbstractSupportToolsFramework;
import com.ibm.js.team.supporttools.framework.commands.ICommandFactory;

/**
 * Main class to run the ScmSupportTools
 *
 */
public class ScmSupportTools extends AbstractSupportToolsFramework {

	public static final Logger logger = LoggerFactory.getLogger(ScmSupportTools.class);

	public ScmSupportTools(ICommandFactory commandFactory) {
		super(commandFactory);
	}

	/**
	 * Main entry point for the application. Gets and performs the command.
	 * 
	 * @param args
	 * @throws ParseException
	 * @throws URISyntaxException
	 */
	public static void main(String[] args) throws ParseException {

		logger.info("\n{} Version: {}", ScmSupportToolsConstants.SCMTOOLS, ScmSupportToolsConstants.SCMTOOLS_VERSION);
		boolean result = false;
		AbstractSupportToolsFramework scmTools = new ScmSupportTools(new ScmSupportToolsCommandFactory());
		result = scmTools.execute(args);
		if (result) {
			logger.info("Success.");
		} else {
			logger.info("Failed.");
			System.exit(1);
		}
	}
}
