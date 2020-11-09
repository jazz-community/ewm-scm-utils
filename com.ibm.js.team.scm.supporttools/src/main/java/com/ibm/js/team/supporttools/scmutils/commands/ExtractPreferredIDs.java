/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scmutils.commands;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.SupportToolsFrameworkConstants;
import com.ibm.js.team.supporttools.framework.framework.AbstractCommand;
import com.ibm.js.team.supporttools.framework.framework.ICommand;
import com.ibm.js.team.supporttools.scmutils.ScmSupportToolsConstants;
import com.ibm.js.team.supporttools.scmutils.statistics.PrefIDAnalyzer;
import com.ibm.js.team.supporttools.scmutils.statistics.SandboxAnalyzer;
import com.ibm.js.team.supporttools.scmutils.utils.SheetUtils;

/**
 * Allows to analyze a sandbox or local file system folder. Ignores folders with
 * names ".git", ".jazz5", ".metadata".
 * 
 */
public class ExtractPreferredIDs extends AbstractCommand implements ICommand {

	public static final Logger logger = LoggerFactory.getLogger(ExtractPreferredIDs.class);
	/**
	 * Constructor, set the command name which will be used as option value for
	 * the command option. The name is used in the UIs and the option parser.
	 */
	public ExtractPreferredIDs() {
		super(ScmSupportToolsConstants.EXTRACT_PREFERRED_IDS);
	}

	/**
	 * Method to add the options this command requires.
	 */
	@Override
	public Options addCommandOptions(Options options) {
		options.addOption(ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER, true,
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER_DESCRIPTION);
		options.addOption(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER, true,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_DESCRIPTION);
		options.addOption(ScmSupportToolsConstants.PARAMETER_LATESTONLY_FLAG, false,
				ScmSupportToolsConstants.PARAMETER_LATESTONLY_FLAG_DESCRIPTION);
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
		logger.info(ScmSupportToolsConstants.CMD_ANALYZE_SANDBOX_DESCRIPTION);
		// General syntax
		logger.info("\n\tSyntax: -{} {} -{} {}", SupportToolsFrameworkConstants.PARAMETER_COMMAND, getCommandName(),
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER,
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER_PROTOTYPE,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_PROTOTYPE);
		// Parameter and description
		logger.info("\n\tParameter description: \n\t -{} \t {} \n\t -{} \t{}",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				SupportToolsFrameworkConstants.PARAMETER_COMMAND_DESCRIPTION,
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER,
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER_DESCRIPTION,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_DESCRIPTION);
		// Optional parameters
		logger.info("\n\tOptional parameter syntax: -{}",
				ScmSupportToolsConstants.PARAMETER_LATESTONLY_FLAG);
		// Optional parameters description
		logger.info("\n\tOptional parameter description: \n\t -{} \t{}",
				ScmSupportToolsConstants.PARAMETER_LATESTONLY_FLAG,
				ScmSupportToolsConstants.PARAMETER_LATESTONLY_FLAG_DESCRIPTION);
		
		// Examples
		logger.info("\n\tExample: -{} {} -{} {}", SupportToolsFrameworkConstants.PARAMETER_COMMAND, getCommandName(),
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER,
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER_EXAMPLE,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_EXAMPLE);
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
		try {
			result = analyzeSandbox(sandboxFolderPath);
		} catch (IOException e) {
			logger.error("IO Exception");
			e.printStackTrace();
		} finally {
		}
		return result;
	}

	/**
	 * Analyze a sandbox.
	 * 
	 * @param sandboxFolderPath
	 * @return
	 * @throws IOException
	 */
	private boolean analyzeSandbox(String sandboxFolderPath) throws IOException {
		boolean result = false;
		logger.info("Analyze sandbox '{}'...", sandboxFolderPath);
		File sandboxFolder = new File(sandboxFolderPath);
		if (!sandboxFolder.exists()) {
			logger.error("Error: Sandboxfolder '{}' does not exist.", sandboxFolderPath);
			return result;
		}
		if (!sandboxFolder.isDirectory()) {
			logger.error("Error: Sandboxfolder '{}' is not a directory.", sandboxFolderPath);
			return result;
		}
		
		String fOutputFolder = null;
		if (getCmd().hasOption(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER)) {
			fOutputFolder = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER);
		}
		
		boolean latestOnly=getCmd().hasOption(ScmSupportToolsConstants.PARAMETER_LATESTONLY_FLAG);
		PrefIDAnalyzer sandboxAnalyzer = new PrefIDAnalyzer(sandboxFolderPath,latestOnly);
	
		sandboxAnalyzer.analyze(sandboxFolder);
//		String workbookName = sandboxFolder.getName() + ".xls";
//		Workbook workBook = SheetUtils.createWorkBook();
//		sandboxAnalyzer.updateWorkBook(workBook);
//		SheetUtils.writeWorkBook(workBook, fOutputFolder, workbookName);
//		logger.info("\n\nShow results...");
//		logger.info(sandboxAnalyzer.getResultAsString());
		return true;
	}

}
