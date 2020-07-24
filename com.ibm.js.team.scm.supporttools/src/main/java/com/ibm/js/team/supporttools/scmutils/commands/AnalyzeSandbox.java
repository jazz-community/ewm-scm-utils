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
import java.util.HashSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.SupportToolsFrameworkConstants;
import com.ibm.js.team.supporttools.framework.framework.AbstractCommand;
import com.ibm.js.team.supporttools.framework.framework.ICommand;
import com.ibm.js.team.supporttools.scmutils.ScmSupportToolsConstants;
import com.ibm.js.team.supporttools.scmutils.statistics.ComponentStat;
import com.ibm.js.team.supporttools.scmutils.statistics.FileInfo;
import com.ibm.js.team.supporttools.scmutils.statistics.SandboxAnalyzer;
import com.ibm.js.team.supporttools.scmutils.statistics.sizerange.RangeStats;
import com.ibm.js.team.supporttools.scmutils.utils.SheetUtils;

/**
 * Allows to analyze a sandbox or local file system folder. Ignores folders with
 * names ".git", ".jazz5", ".metadata".
 * 
 */
public class AnalyzeSandbox extends AbstractCommand implements ICommand {

	public static final Logger logger = LoggerFactory.getLogger(AnalyzeSandbox.class);

	/**
	 * Constructor, set the command name which will be used as option value for
	 * the command option. The name is used in the UIs and the option parser.
	 */
	public AnalyzeSandbox() {
		super(ScmSupportToolsConstants.CMD_ANYLYZE_SANDBOX);
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

		SandboxAnalyzer sandboxAnalyzer = new SandboxAnalyzer(sandboxFolderPath);
	
		sandboxAnalyzer.addIgnoreDirectory(".metadata");
		sandboxAnalyzer.addIgnoreDirectory(".jazz5");
		sandboxAnalyzer.addIgnoreDirectory(".git");
		
		sandboxAnalyzer.analyze(sandboxFolder);
		String workbookName = sandboxFolder.getName() + ".xls";
		Workbook workBook = SheetUtils.createWorkBook();
		sandboxAnalyzer.updateWorkBook(workBook);
		SheetUtils.writeWorkBook(workBook, fOutputFolder, workbookName);
		logger.info("\n\nShow results...");
		logger.info(sandboxAnalyzer.getResultAsString());
		return true;
	}

//	/**
//	 * @param sandboxFolder
//	 * @param path
//	 * @param compStat
//	 * @param depth
//	 */
//	private void analyzeFolder(File sandboxFolder, String path, ComponentStat compStat, int depth) {
//		File[] contents = sandboxFolder.listFiles();
//		long folders = 0;
//		long files = 0;
//		for (File file : contents) {
//			if (file.isDirectory()) {
//				if (!isIgnoredDirectory(file)) {
//					folders++;
//					compStat.addFolderStat(file, depth);
//					analyzeFolder(file, file.getAbsolutePath(), compStat, depth + 1);
//				} else {
//					logger.info("\nIgnoring folder '{}'", file.getAbsolutePath());
//				}
//			} else {
//				if (!isIgnoredFile(file)) {
//					files++;
//					FileInfo fInfo = FileInfo.getFileInfo(file);
//					compStat.addFileStat(fInfo, depth);
//					rangeStats.analyze(fInfo);
//				} else {
//					logger.info("\nIgnoring file '{}'", file.getAbsolutePath());
//				}
//			}
//		}
//		compStat.addFolderStats(folders, files, depth);
//		showProgress();
//	}
//
//	/**
//	 * This prints one '.' for every for 10 times it is called to show some
//	 * progress. Can be used to show more fine grained progress.
//	 */
//	private void showProgress() {
//		fProgress++;
//		if (fProgress % 10 == 9) {
//			System.out.print(".");
//		}
//	}

//	public void addIgnoreDirectory(String name) {
//		ignoreFolderSet.add(name);
//	}
//
//	public void addIgnoreFile(String name) {
//		ignoreFileSet.add(name);
//	}
//
//	private boolean isIgnoredDirectory(File file) {
//		if (file == null) {
//			return false;
//		}
//		return ignoreFolderSet.contains(file.getName());
//	}
//
//	private boolean isIgnoredFile(File file) {
//		if (file == null) {
//			return false;
//		}
//		return ignoreFolderSet.contains(file.getName());
//	}
}
