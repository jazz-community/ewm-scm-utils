/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scmutils.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.SupportToolsFrameworkConstants;
import com.ibm.js.team.supporttools.framework.framework.AbstractCommand;
import com.ibm.js.team.supporttools.framework.framework.ICommand;
import com.ibm.js.team.supporttools.scmutils.ScmSupportToolsConstants;
import com.ibm.team.repository.common.json.JSONArray;
import com.ibm.team.repository.common.json.JSONObject;

/**
 * Convert a load rule to match the component ID's for components after an import. This allows to use a load rule that uses component Id's with the imported components.
 * 
 */
public class ConvertLoadrule extends AbstractCommand implements ICommand {

	public static final Logger logger = LoggerFactory.getLogger(ConvertLoadrule.class);

	/**
	 * Constructor, set the command name which will be used as option value for
	 * the command option. The name is used in the UIs and the option parser.
	 */
	public ConvertLoadrule() {
		super(ScmSupportToolsConstants.CMD_CONVERT_LOADRULE);
	}

	/**
	 * Method to add the options this command requires.
	 */
	@Override
	public Options addCommandOptions(Options options) {
		options.addOption(ScmSupportToolsConstants.PARAMETER_INPUTFOLDER, true,
				ScmSupportToolsConstants.PARAMETER_INPUTFOLDER_DESCRIPTION);
		options.addOption(ScmSupportToolsConstants.PARAMETER_SOURCE_LOADRULE_FILE_PATH, true,
				ScmSupportToolsConstants.PARAMETER_SOURCE_LOADRULE_FILE_PATH_DESCRIPTION);
		options.addOption(ScmSupportToolsConstants.PARAMETER_TARGET_LOADRULE_FILE_PATH, true,
				ScmSupportToolsConstants.PARAMETER_TARGET_LOADRULE_FILE_PATH_DESCRIPTION);
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

		if (!(cmd.hasOption(ScmSupportToolsConstants.PARAMETER_INPUTFOLDER)
				&& cmd.hasOption(ScmSupportToolsConstants.PARAMETER_SOURCE_LOADRULE_FILE_PATH)
				&& cmd.hasOption(ScmSupportToolsConstants.PARAMETER_TARGET_LOADRULE_FILE_PATH))) {
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
		logger.info(ScmSupportToolsConstants.CMD_CONVERT_LOADRULE_DESCRIPTION);
		// General syntax
		logger.info("\n\tSyntax: -{} {} -{} {} -{} {} -{} {} ", SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				getCommandName(), ScmSupportToolsConstants.PARAMETER_INPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_INPUTFOLDER_PROTOTYPE,
				ScmSupportToolsConstants.PARAMETER_SOURCE_LOADRULE_FILE_PATH,
				ScmSupportToolsConstants.PARAMETER_SOURCE_LOADRULE_FILE_PATH_PROTOTYPE,
				ScmSupportToolsConstants.PARAMETER_TARGET_LOADRULE_FILE_PATH,
				ScmSupportToolsConstants.PARAMETER_TARGET_LOADRULE_FILE_PATH_PROTOTYPE);
		// Parameter and description
		logger.info("\n\tParameter Description: \n\t -{} \t {} \n\t -{} \t{} \n\t -{} \t{}",
				ScmSupportToolsConstants.PARAMETER_INPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_INPUTFOLDER_DESCRIPTION
						+ ScmSupportToolsConstants.PARAMETER_INPUTFOLDER_CONVERT_DESCRIPTION,
				ScmSupportToolsConstants.PARAMETER_SOURCE_LOADRULE_FILE_PATH,
				ScmSupportToolsConstants.PARAMETER_SOURCE_LOADRULE_FILE_PATH_DESCRIPTION,
				ScmSupportToolsConstants.PARAMETER_TARGET_LOADRULE_FILE_PATH,
				ScmSupportToolsConstants.PARAMETER_TARGET_LOADRULE_FILE_PATH_DESCRIPTION);
		// Examples
		logger.info("\n\tExample: -{} {} -{} {} -{} {} -{} {}", SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				getCommandName(), ScmSupportToolsConstants.PARAMETER_INPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_INPUTFOLDER_EXAMPLE,
				ScmSupportToolsConstants.PARAMETER_SOURCE_LOADRULE_FILE_PATH,
				ScmSupportToolsConstants.PARAMETER_SOURCE_LOADRULE_FILE_PATH_EXAMPLE,
				ScmSupportToolsConstants.PARAMETER_TARGET_LOADRULE_FILE_PATH,
				ScmSupportToolsConstants.PARAMETER_TARGET_LOADRULE_FILE_PATH_EXAMPLE);
		// Optional parameter examples
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
		String inputFolderPath = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_INPUTFOLDER);
		String sourceLoadrulePath = getCmd()
				.getOptionValue(ScmSupportToolsConstants.PARAMETER_SOURCE_LOADRULE_FILE_PATH);
		String targetLoadrulePath = getCmd()
				.getOptionValue(ScmSupportToolsConstants.PARAMETER_TARGET_LOADRULE_FILE_PATH);

		result = convertLoadrule(inputFolderPath, sourceLoadrulePath, targetLoadrulePath);
		return result;
	}

	/**
	 * @param inputFolderPath
	 * @param sourceLoadrulePath
	 * @param targetLoadrulePath
	 * @return
	 */
	private boolean convertLoadrule(String inputFolderPath, String sourceLoadrulePath, String targetLoadrulePath) {
		boolean result = false;
		try {
			HashMap<String, String> source2TargetUUIDMap = new HashMap<String, String>(3000);
			File jsonInputFile = new File(inputFolderPath, ScmSupportToolsConstants.COMPONENT_MAPPING_JSON_FILE);
			Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(jsonInputFile), "UTF-8")); //$NON-NLS-1$
			logger.info("Reading component UUID mapping from file '{}'...", jsonInputFile.getAbsolutePath());
			JSONArray maps = JSONArray.parse(reader);
			for (Object map : maps) {
				if (map instanceof JSONObject) {
					JSONObject jsonMap = (JSONObject) map;
					String sourceUUID = (String) jsonMap.get(ScmSupportToolsConstants.JSON_SOURCE_COMPONENT_UUID);
					String targetUUID = (String) jsonMap.get(ScmSupportToolsConstants.JSON_TARGET_COMPONENT_UUID);
					source2TargetUUIDMap.put(sourceUUID, targetUUID);
				}
			}
			Path sourcePath = Paths.get(sourceLoadrulePath);
			Path targetPath = Paths.get(targetLoadrulePath);
			Charset charset = StandardCharsets.UTF_8;
			String content;
			content = new String(Files.readAllBytes(sourcePath), charset);
			Set<Entry<String, String>> uuidMapping = source2TargetUUIDMap.entrySet();
			for (Entry<String, String> entry : uuidMapping) {
				content = content.replaceAll(entry.getKey(), entry.getValue());
			}
			Files.write(targetPath, content.getBytes(charset));
			result = true;
		} catch (IOException e) {
			logger.error("IOException: {}", e.getMessage());
		}
		return result;
	}
}
