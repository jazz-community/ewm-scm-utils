package com.ibm.js.team.supporttools.scm;

import java.net.URISyntaxException;

import org.apache.commons.cli.ParseException;

import com.ibm.js.team.supporttools.framework.AbstractSupportToolsFramework;
import com.ibm.js.team.supporttools.framework.commands.ICommandFactory;

public class SCMSupportTools extends AbstractSupportToolsFramework {

	public SCMSupportTools(ICommandFactory commandFactory) {
		super(commandFactory);
	}
	
	
	/**
	 * Main entry point for the application. Gets and performs the command.
	 * 
	 * @param args
	 * @throws ParseException
	 * @throws URISyntaxException
	 */
	public static void main(String[] args) throws URISyntaxException, ParseException {

		logger.info("\nSCMSupportTools Version: {}", "1.0");
		boolean result=false;
		AbstractSupportToolsFramework scmTools= new SCMSupportTools(new SCMSupportToolsCommandFactory());
		result = scmTools.execute(args);
		if (result) {
			logger.info("Success.");
		} else {
			logger.info("Failed.");
			System.exit(1);
		}
	}


}
