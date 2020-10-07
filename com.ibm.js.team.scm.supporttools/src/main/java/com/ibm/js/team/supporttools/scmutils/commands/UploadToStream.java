/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scmutils.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.SupportToolsFrameworkConstants;
import com.ibm.js.team.supporttools.framework.framework.AbstractTeamrepositoryCommand;
import com.ibm.js.team.supporttools.framework.framework.ICommand;
import com.ibm.js.team.supporttools.framework.util.TimeStampUtil;
import com.ibm.js.team.supporttools.scmutils.ScmSupportToolsConstants;
import com.ibm.js.team.supporttools.scmutils.utils.ComponentUtil;
import com.ibm.js.team.supporttools.scmutils.utils.ConnectionUtil;
import com.ibm.js.team.supporttools.scmutils.utils.FileSystemToSCMUploader;
import com.ibm.js.team.supporttools.scmutils.utils.ProjectAreaUtil;
import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.IAuditableHandle;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.client.IBaselineConnection;
import com.ibm.team.scm.client.IWorkspaceConnection;
import com.ibm.team.scm.client.IWorkspaceManager;
import com.ibm.team.scm.client.SCMPlatform;
import com.ibm.team.scm.common.IComponentHandle;
import com.ibm.team.scm.common.IFlowEntry;
import com.ibm.team.scm.common.IFlowTable;
import com.ibm.team.scm.common.IWorkspaceHandle;
import com.ibm.team.scm.common.WorkspaceComparisonFlags;
import com.ibm.team.scm.common.dto.IChangeHistorySyncReport;
import com.ibm.team.scm.common.dto.IReadScope;
import com.ibm.team.scm.common.dto.IWorkspaceSearchCriteria;

/**
 * Command to 
 * 
 */
public class UploadToStream extends AbstractTeamrepositoryCommand implements ICommand {

	public static final Logger logger = LoggerFactory.getLogger(UploadToStream.class);
	private File fInputFolder = null;

	/**
	 * Constructor, set the command name which will be used as option value for
	 * the command option. The name is used in the UIs and the option parser.
	 */
	public UploadToStream() {
		super(ScmSupportToolsConstants.CMD_UPLOAD_TO_STREAM);
	}

	/**
	 * Method to add the options this command requires.
	 */
	@Override
	public Options addTeamRepositoryCommandOptions(Options options) {
		options.addOption(SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA, true,
				SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA_DESCRIPTION);
		options.addOption(ScmSupportToolsConstants.PARAMETER_STREAM_NAME, true,
				ScmSupportToolsConstants.PARAMETER_STREAM_NAME_DESCRIPTION);
		options.addOption(ScmSupportToolsConstants.PARAMETER_INPUTFOLDER, true,
				ScmSupportToolsConstants.PARAMETER_INPUTFOLDER_DESCRIPTION);
		return options;
	}

	/**
	 * Method to check if the required options/parameters required to perform
	 * the command are available.
	 */
	@Override
	public boolean checkTeamreposiroyCommandParameters(CommandLine cmd) {
		// Check for required options
		boolean isValid = true;

		if (!(cmd.hasOption(SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA)
				&& cmd.hasOption(ScmSupportToolsConstants.PARAMETER_STREAM_NAME)
				&& cmd.hasOption(ScmSupportToolsConstants.PARAMETER_INPUTFOLDER))) {
			isValid = false;
		}
		return isValid;
	}

	@Override
	public String getScenarioName() {
		return ScmSupportToolsConstants.EXPENSIVESCENARIO_SCMTOOLS + getCommandName();
	}

	/**
	 * Method to print the syntax in case of missing options.
	 */
	@Override
	public void printSyntax() {
		// Command name and description
		logger.info("{}", getCommandName());
		logger.info(ScmSupportToolsConstants.CMD_IMPORT_WORKSPACE_DESCRIPTION);
		// General syntax
		logger.info("\n\tSyntax : -{} {} -{} {} -{} {} -{} {} -{} {} -{} {} -{} {}",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND, getCommandName(),
				SupportToolsFrameworkConstants.PARAMETER_URL, SupportToolsFrameworkConstants.PARAMETER_URL_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_USER,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA,
				SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA_PROTOTYPE,
				ScmSupportToolsConstants.PARAMETER_STREAM_NAME,
				ScmSupportToolsConstants.PARAMETER_STREAM_NAME_PROTOTYPE, 
				ScmSupportToolsConstants.PARAMETER_INPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_INPUTFOLDER_PROTOTYPE);
		// Parameter and description
		logger.info(
				"\n\tParameter description: \n\t -{} \t {} \n\t -{} \t{} \n\t -{} \t {} \n\t -{} \t {} \n\t -{} \t {} \n\t -{} \t {} \n\t -{} \t {}",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				SupportToolsFrameworkConstants.PARAMETER_COMMAND_DESCRIPTION,
				SupportToolsFrameworkConstants.PARAMETER_URL, SupportToolsFrameworkConstants.PARAMETER_URL_DESCRIPTION,
				SupportToolsFrameworkConstants.PARAMETER_USER,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_DESCRIPTION,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_DESCRIPTION,
				SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA,
				SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA_PROTOTYPE,				
				ScmSupportToolsConstants.PARAMETER_STREAM_NAME,
				ScmSupportToolsConstants.PARAMETER_STREAM_NAME_DESCRIPTION,
				ScmSupportToolsConstants.PARAMETER_INPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_INPUTFOLDER_DESCRIPTION);
		// Optional parameters
//		logger.info("\n\tOptional parameter syntax: -{} {} -{} -{}",
//				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER,
//				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER_PROTOTYPE,
//				ScmSupportToolsConstants.PARAMETER_REUSE_EXISTING_WORKSPACE_FLAG,
//				ScmSupportToolsConstants.PARAMETER_SKIP_UPLOADING_EXISTING_COMPONENT_FLAG);
		// Optional parameters description
//		logger.info("\n\tOptional parameter description: \n\t -{} \t{} \n\t -{} \t {}\n\t -{} \t {}",
//				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER,
//				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER_DESCRIPTION,
//				ScmSupportToolsConstants.PARAMETER_REUSE_EXISTING_WORKSPACE_FLAG,
//				ScmSupportToolsConstants.PARAMETER_REUSE_EXISTING_WORKSPACE_FLAG_DESCRIPTION,
//				ScmSupportToolsConstants.PARAMETER_SKIP_UPLOADING_EXISTING_COMPONENT_FLAG,
//				ScmSupportToolsConstants.PARAMETER_SKIP_UPLOADING_EXISTING_COMPONENT_FLAG_DESCRIPTION);
		// Examples
		logger.info("\n\tExample: -{} {} -{} {} -{} {} -{} {} -{} {} -{} {} -{} {}",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND, getCommandName(),
				SupportToolsFrameworkConstants.PARAMETER_URL, SupportToolsFrameworkConstants.PARAMETER_URL_EXAMPLE,
				SupportToolsFrameworkConstants.PARAMETER_USER, SupportToolsFrameworkConstants.PARAMETER_USER_ID_EXAMPLE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_EXAMPLE,
				SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA,
				SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA_EXAMPLE,
				ScmSupportToolsConstants.PARAMETER_STREAM_NAME,
				ScmSupportToolsConstants.PARAMETER_STREAM_NAME_EXAMPLE, 
				ScmSupportToolsConstants.PARAMETER_INPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_INPUTFOLDER_EXAMPLE);
		// Optional parameter examples
//		logger.info("\n\tExample optional parameter: -{} {} -{} -{}",
//				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER,
//				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER_EXAMPLE,
//				ScmSupportToolsConstants.PARAMETER_REUSE_EXISTING_WORKSPACE_FLAG,
//				ScmSupportToolsConstants.PARAMETER_SKIP_UPLOADING_EXISTING_COMPONENT_FLAG);
	}

	/**
	 * The main method that executes the behavior of this command.
	 */
	@Override
	public boolean executeTeamRepositoryCommand() throws TeamRepositoryException {
		boolean result = false;
		// Execute the code
		// Get all the option values
		final String projectAreaName = getCmd().getOptionValue(SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA);
		final String scmStream = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_STREAM_NAME);
		final String inputFolderPath = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_INPUTFOLDER);
		
		try {
			File inputfolder = new File(inputFolderPath);
			if (!inputfolder.exists()) {
				logger.error("Error: Folder '{}' does not exist.", inputFolderPath);
				return result;
			}
			if (!inputfolder.isDirectory()) {
				logger.error("Error: Folder '{}' is not a directory.", inputFolderPath);
				return result;
			}
			fInputFolder = inputfolder;
			result = uploadFilesToComponent(getTeamRepository(), projectAreaName, scmStream, getMonitor());
		} catch (IOException e) {
			logger.error("IOException: {}", e.getMessage());
		}
		return result;
	}

	/**
	 * Import the repository workspace information.
	 * 
	 * @param teamRepository
	 * @param projectAreaName
	 * @param scmConnection
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	private boolean uploadFilesToComponent(ITeamRepository teamRepository, String projectAreaName, String scmConnection,
			IProgressMonitor monitor)
			throws TeamRepositoryException, UnsupportedEncodingException, FileNotFoundException, IOException {
		boolean result= false;
		IWorkspaceConnection tempWorkspace = null;
		IWorkspaceManager wm = SCMPlatform.getWorkspaceManager(teamRepository);
		try{
		// Find Stream
		logger.info("Find stream '{}'...", scmConnection);
		IWorkspaceConnection targetStream = null;
		List<IWorkspaceHandle> streams = ConnectionUtil.findWorkspacesByName(teamRepository, scmConnection,
				IWorkspaceSearchCriteria.STREAMS, monitor);
		if (streams.size() == 0) {
			logger.error("The stream '{}' does not exist.", scmConnection);
			return result;
		}
		if (streams.size() >1 ) {
			logger.error("Ambiguous stream name '{}'.", scmConnection);
			return result;
		}
		
		targetStream=wm.getWorkspaceConnection(streams.get(0), monitor);
		
		String userID= teamRepository.loggedInContributor().getUserId();
		String timeStampNow = TimeStampUtil.getDate(new Date(), null);
		String tempWorkspaceName = "Upload Workspace " + userID + " " + timeStampNow;
		
		tempWorkspace = wm.createWorkspace(teamRepository.loggedInContributor(), tempWorkspaceName,
				"Temporary workspace to share and upload " + tempWorkspaceName, monitor);
		
		// Find Project Area
		IProcessClientService processClient = (IProcessClientService) teamRepository
				.getClientLibrary(IProcessClientService.class);
		IAuditableHandle owner = ProjectAreaUtil.findProjectAreaByFQN(projectAreaName, processClient, monitor);
		

		File inputFile = fInputFolder;
		String componentName=inputFile.getName();
		logger.info("Find or create component...");
		IComponentHandle componentHandle = ComponentUtil.findComponentByName(wm, componentName, monitor); // Find the component
		if(null==componentHandle){
			componentHandle=ComponentUtil.createComponent(teamRepository, wm, componentName, owner, monitor);
			ComponentUtil.setComponentOwnerAndVisibility(wm, componentHandle,owner , IReadScope.FACTORY.createProcessAreaScope(), monitor);
		}
		// add component to workspace and stream
		if(!ConnectionUtil.isComponentInWorkspace(targetStream,componentHandle)){
			ConnectionUtil.addComponentToWorkspaceConnection(targetStream, componentHandle, monitor);
		}
		if(!ConnectionUtil.isComponentInWorkspace(tempWorkspace,componentHandle)){
			ConnectionUtil.addComponentFromSeedToWorkspaceConnection(tempWorkspace, componentHandle,targetStream, monitor);
		}
		Collection<IComponentHandle> components = new ArrayList<IComponentHandle>(1); 
		components.add(componentHandle);
		setFlow(tempWorkspace, targetStream, components, monitor);
		
		String changeSetComment = "Share " + userID + " " + timeStampNow;
		logger.info("Uploading changes...");
		FileSystemToSCMUploader scmUploader = new FileSystemToSCMUploader();
		if(!scmUploader.uploadFileToComponent(inputFile.getAbsolutePath(), tempWorkspace, componentHandle, changeSetComment, monitor)){
			logger.info("No changes to deliver.");
			result = false;
		}
		
		// Compare the repository workspace with the stream to find the changes
		// Deliver the change sets
		logger.info("\nComparing Change Sets...");
		IChangeHistorySyncReport changeSetSync = tempWorkspace.compareTo(
				targetStream,
				WorkspaceComparisonFlags.CHANGE_SET_COMPARISON_ONLY,
				Collections.EMPTY_LIST, monitor);

		logger.info("Deliver Change Sets...");
		tempWorkspace.deliver(targetStream, changeSetSync,
				Collections.EMPTY_LIST,
				changeSetSync.outgoingChangeSets(componentHandle), monitor);
		String baselineName="Baseline " + userID + " " + timeStampNow;
		String baselineComment= baselineName +" created by automation.";
		// Create a baseline and compare the repository workspace with the
		// stream to find the changes and deliver the baselines
		logger.info("Creating Baseline...");
		@SuppressWarnings("unused")
		IBaselineConnection baseline = tempWorkspace.createBaseline(componentHandle,
				baselineName, baselineComment, monitor);
		
		logger.info("Comparing Baselines...");
		IChangeHistorySyncReport baselineSync = tempWorkspace.compareTo(
				targetStream, WorkspaceComparisonFlags.INCLUDE_BASELINE_INFO,
				Collections.EMPTY_LIST, monitor);

		// Deliver the baselines
		logger.info("Deliver Baselines...");
		tempWorkspace.deliver(targetStream, baselineSync,
				baselineSync.outgoingBaselines(componentHandle),
				baselineSync.outgoingChangeSets(componentHandle), monitor);
		logger.info("Operation successful, baseline name is '{}'.", baselineName);
		result=true;
		return result;
		} finally {
			if(tempWorkspace!=null){
				wm.deleteWorkspace(tempWorkspace.getResolvedWorkspace(), monitor);				
			}
		}
	}



	/**
	 * Set the flow targets and the component scope
	 * 
	 * @param source
	 * @param dest
	 * @param components
	 * @param monitor
	 * @throws TeamRepositoryException
	 */
	private void setFlow(IWorkspaceConnection source,
			IWorkspaceConnection dest, Collection<IComponentHandle> components,
			IProgressMonitor monitor) throws TeamRepositoryException {
		// Get the current flow table
		IFlowTable cflowTable = source.getFlowTable().getWorkingCopy();
		// Set the accept and deliver flow to the target
		cflowTable.addAcceptFlow(dest.getResolvedWorkspace(), dest
				.teamRepository().getId(), dest.teamRepository()
				.getRepositoryURI(), components /* Collections.EMPTY_LIST */,
				"Accept Flow");
		cflowTable.addDeliverFlow(dest.getResolvedWorkspace(), dest
				.teamRepository().getId(), dest.teamRepository()
				.getRepositoryURI(), components /* Collections.EMPTY_LIST */,
				"Deliver Flow");
		// Limit the scope for accept and delivery to the components that are
		// needed
		cflowTable.setComponentScopes(dest.getResolvedWorkspace(), components);
		// We want incoming and outgoing default and current flow
		// Set the incoming flow for current and default
		IFlowEntry acceptEntry = cflowTable.getAcceptFlow(dest
				.getResolvedWorkspace());
		cflowTable.setCurrent(acceptEntry);
		cflowTable.setDefault(acceptEntry);
		// Set the incoming flow for current and default
		IFlowEntry deliverEntry = cflowTable.getDeliverFlow(dest
				.getResolvedWorkspace());
		cflowTable.setCurrent(deliverEntry);
		cflowTable.setDefault(deliverEntry);

		// Set the modified flow table
		source.setFlowTable(cflowTable, monitor);
	}

}
