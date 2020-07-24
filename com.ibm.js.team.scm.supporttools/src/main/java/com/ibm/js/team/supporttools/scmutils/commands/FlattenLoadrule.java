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

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.ibm.js.team.supporttools.framework.SupportToolsFrameworkConstants;
import com.ibm.js.team.supporttools.framework.framework.AbstractCommand;
import com.ibm.js.team.supporttools.framework.framework.ICommand;
import com.ibm.js.team.supporttools.scmutils.ScmSupportToolsConstants;

/**
 * Removes target folder hierarchy from a loadrule.
 */
public class FlattenLoadrule extends AbstractCommand implements ICommand {

	public static final Logger logger = LoggerFactory.getLogger(FlattenLoadrule.class);

	/**
	 * Constructor, set the command name which will be used as option value for
	 * the command option. The name is used in the UIs and the option parser.
	 */
	public FlattenLoadrule() {
		super(ScmSupportToolsConstants.CMD_FLATTEN_LOADRULE);
	}

	/**
	 * Method to add the options this command requires.
	 */
	@Override
	public Options addCommandOptions(Options options) {
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

		if (!(// cmd.hasOption(ScmSupportToolsConstants.PARAMETER_INPUTFOLDER)
		cmd.hasOption(ScmSupportToolsConstants.PARAMETER_SOURCE_LOADRULE_FILE_PATH)
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
		logger.info(ScmSupportToolsConstants.CMD_FLATTEN_LOADRULE_DESCRIPTION);
		// General syntax
		logger.info("\n\tSyntax: -{} {} -{} {} -{} {} ", SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				getCommandName(), ScmSupportToolsConstants.PARAMETER_SOURCE_LOADRULE_FILE_PATH,
				ScmSupportToolsConstants.PARAMETER_SOURCE_LOADRULE_FILE_PATH_PROTOTYPE,
				ScmSupportToolsConstants.PARAMETER_TARGET_LOADRULE_FILE_PATH,
				ScmSupportToolsConstants.PARAMETER_TARGET_LOADRULE_FILE_PATH_PROTOTYPE);
		// Parameter and description
		logger.info("\n\tParameter Description: \n\t -{} \t{} \n\t -{} \t{} \n",
				ScmSupportToolsConstants.PARAMETER_SOURCE_LOADRULE_FILE_PATH,
				ScmSupportToolsConstants.PARAMETER_SOURCE_LOADRULE_FILE_PATH_DESCRIPTION,
				ScmSupportToolsConstants.PARAMETER_TARGET_LOADRULE_FILE_PATH,
				ScmSupportToolsConstants.PARAMETER_TARGET_LOADRULE_FILE_PATH_DESCRIPTION);
		// Examples
		logger.info("\n\tExample: -{} {} -{} {} -{} {}", SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				ScmSupportToolsConstants.CMD_FLATTEN_LOADRULE,
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
		String sourceLoadrulePath = getCmd()
				.getOptionValue(ScmSupportToolsConstants.PARAMETER_SOURCE_LOADRULE_FILE_PATH);
		String targetLoadrulePath = getCmd()
				.getOptionValue(ScmSupportToolsConstants.PARAMETER_TARGET_LOADRULE_FILE_PATH);

		try {
			// 1- Build the doc from the XML file
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new InputSource(sourceLoadrulePath));

			// 2- Locate the node(s) with xpath
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList nodes = (NodeList) xpath.evaluate("//sandboxRelativePath[@pathPrefix]", doc,
					XPathConstants.NODESET);

			// 3- Make the change on the selected nodes
			for (int idx = 0; idx < nodes.getLength(); idx++) {
				Node value = nodes.item(idx).getAttributes().getNamedItem("pathPrefix");
				String val = value.getNodeValue();
				String result1 = val.replaceFirst("/", "#");
				String result2 = result1.replaceAll("/", "_");
				String result3 = result2.replaceFirst("#", "/");
				value.setNodeValue(result3);
			}

			// 4- Save the result to a new XML doc
			Transformer xformer = TransformerFactory.newInstance().newTransformer();
			xformer.transform(new DOMSource(doc), new StreamResult(new File(targetLoadrulePath)));
			result = true;
		} catch (XPathExpressionException e) {
			logger.error("XPathExpressionException: {}", e.getMessage());
		} catch (DOMException e) {
			logger.error("DOMException: {}", e.getMessage());
		} catch (TransformerConfigurationException e) {
			logger.error("IOException: {}", e.getMessage());
		} catch (SAXException e) {
			logger.error("SAXException: {}", e.getMessage());
		} catch (IOException e) {
			logger.error("IOException: {}", e.getMessage());
		} catch (ParserConfigurationException e) {
			logger.error("ParserConfigurationException: {}", e.getMessage());
		} catch (TransformerFactoryConfigurationError e) {
			logger.error("TransformerFactoryConfigurationError: {}", e.getMessage());
		} catch (TransformerException e) {
			logger.error("TransformerException: {}", e.getMessage());
		}

		return result;
	}
}
