/*******************************************************************************
 * Copyright (c) 2015-2019 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.commands;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.SupportToolsFrameworkConstants;
import com.ibm.js.team.supporttools.framework.framework.AbstractCommand;
import com.ibm.js.team.supporttools.framework.framework.ICommand;
import com.ibm.js.team.supporttools.scm.ScmSupportToolsConstants;
import com.ibm.js.team.supporttools.scm.statistics.ComponentStat;
import com.ibm.js.team.supporttools.scm.statistics.sizerange.RangeStats;
import com.ibm.js.team.supporttools.scm.utils.FileInfo;

/**
 * Allows to analyze a sandbox or local file system folder.
 * 
 */
public class AnalyzeSandbox extends AbstractCommand implements ICommand {

	public static final Logger logger = LoggerFactory.getLogger(AnalyzeSandbox.class);
	private int fProgress = 0;
	private RangeStats rangeStats = new RangeStats();

	/**
	 * Constructor, set the command name which will be used as option value for
	 * the command option. The name is used in the UIs and the option parser.
	 */
	public AnalyzeSandbox() {
		super(ScmSupportToolsConstants.CMD_ANYLYZESANDBOX);
	}

	/**
	 * Method to add the options this command requires.
	 */
	@Override
	public Options addCommandOptions(Options options) {
		options.addOption(ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER, true,
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER_DESCRIPTION);
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
		logger.info(ScmSupportToolsConstants.CMD_ANALYZESANDBOX_DESCRIPTION);
		// General syntax
		logger.info("\n\tSyntax: -{} {} -{} {}", SupportToolsFrameworkConstants.PARAMETER_COMMAND, getCommandName(),
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER,
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER_PROTOTYPE);
		// Parameter and description
		logger.info("\n\tParameter description: \n\t -{} \t {} \n\t -{} \t{}",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				SupportToolsFrameworkConstants.PARAMETER_COMMAND_DESCRIPTION,
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER,
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER_DESCRIPTION);
		// Examples
		logger.info("\n\tExample: -{} {} -{} {}", SupportToolsFrameworkConstants.PARAMETER_COMMAND, getCommandName(),
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER,
				ScmSupportToolsConstants.PARAMETER_SANDBOXFOLDER_EXAMPLE);
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		}
		return result;
	}

	/**
	 * Export a repository workspace, all its components and the current SCM
	 * data into a persistent format. The persistent format can later be used to
	 * import and recreate a repository workspace and the component and the
	 * latest content.
	 * 
	 * @param sandboxFolderPath
	 * @return
	 * 
	 * 		scm show sandbox-structure
	 * @throws IOException
	 */
	private boolean analyzeSandbox(String sandboxFolderPath) throws IOException {
		boolean result = false;
		logger.info("Analyze sandbox '{}'...", sandboxFolderPath);
		File sandboxFolder = new File(sandboxFolderPath);
		if (!sandboxFolder.exists()) {
			logger.error("Error: Sandboxfolder '{}' could not be created.", sandboxFolderPath);
			return result;
		}
		if (!sandboxFolder.isDirectory()) {
			logger.error("Error: Sandboxfolder '{}' is not a directory.", sandboxFolderPath);
			return result;
		}
		ComponentStat compStat = new ComponentStat(sandboxFolderPath);
		analyzeFolder(sandboxFolder, "", compStat, 0);

		logger.info("\n\nShow results...");
		logger.info(compStat.toString());
		rangeStats.createWorkBook();
		return true;
	}

	/**
	 * @param sandboxFolder
	 * @param path
	 * @param compStat
	 * @param depth
	 */
	private void analyzeFolder(File sandboxFolder, String path, ComponentStat compStat, int depth) {
		File[] contents = sandboxFolder.listFiles();
		long folders = 0;
		long files = 0;
		for (File file : contents) {
			if (file.isDirectory()) {
				folders++;
				compStat.addFolderStat(file, depth);
				analyzeFolder(file, file.getAbsolutePath(), compStat, depth + 1);
			} else {
				files++;
				FileInfo fInfo = FileInfo.getFileInfo(file);
				compStat.addFileStat(fInfo, depth);
				rangeStats.analyze(fInfo);
			}
		}
		compStat.addFolderStats(folders, files, depth);
		showProgress();
	}

	/**
	 * This prints one '.' for every for 10 times it is called to show some
	 * progress. Can be used to show more fine grained progress.
	 */
	private void showProgress() {
		fProgress++;
		if (fProgress % 10 == 9) {
			System.out.print(".");
		}
	}
}
