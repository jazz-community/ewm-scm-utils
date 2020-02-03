/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.SupportToolsFrameworkConstants;
import com.ibm.js.team.supporttools.framework.framework.AbstractTeamrepositoryCommand;
import com.ibm.js.team.supporttools.framework.framework.ICommand;
import com.ibm.js.team.supporttools.scm.ScmSupportToolsConstants;
import com.ibm.js.team.supporttools.scm.utils.ArchiveToSCMExtractor;
import com.ibm.js.team.supporttools.scm.utils.ConnectionUtil;
import com.ibm.team.filesystem.common.IFileContent;
import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.process.client.IProcessItemService;
import com.ibm.team.process.common.IProcessArea;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.IAuditableHandle;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.repository.common.UUID;
import com.ibm.team.repository.common.json.JSONArray;
import com.ibm.team.repository.common.json.JSONObject;
import com.ibm.team.scm.client.IWorkspaceConnection;
import com.ibm.team.scm.client.IWorkspaceManager;
import com.ibm.team.scm.client.SCMPlatform;
import com.ibm.team.scm.common.IChangeSetHandle;
import com.ibm.team.scm.common.IComponentHandle;
import com.ibm.team.scm.common.IWorkspaceHandle;
import com.ibm.team.scm.common.dto.IComponentSearchCriteria;
import com.ibm.team.scm.common.dto.IWorkspaceSearchCriteria;

/**
 * Command to import the component structure and content created in a Export
 * command. The command creates the components if necessary and imports and
 * creates the file and folder structure within each component based on the
 * output information.
 * 
 */
public class ImportWorkspace extends AbstractTeamrepositoryCommand implements ICommand {

	public static final Logger logger = LoggerFactory.getLogger(ImportWorkspace.class);
	private File fInputFolder = null;
	private IAuditableHandle fArea;
	private String fNamePrefix = null;
	private int fProgress = 0;
	private boolean reuseExistingWorkspace = false;
	private boolean skipUploadExistingComponent = false;

	/**
	 * Constructor, set the command name which will be used as option value for
	 * the command option. The name is used in the UIs and the option parser.
	 */
	public ImportWorkspace() {
		super(ScmSupportToolsConstants.CMD_IMPORT_WORKSPACE);
	}

	/**
	 * Method to add the options this command requires.
	 */
	@Override
	public Options addTeamRepositoryCommandOptions(Options options) {
		options.addOption(SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA, true,
				SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA_DESCRIPTION);
		options.addOption(ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID, true,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_DESCRIPTION);
		options.addOption(ScmSupportToolsConstants.PARAMETER_INPUTFOLDER, true,
				ScmSupportToolsConstants.PARAMETER_INPUTFOLDER_DESCRIPTION);
		options.addOption(ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER, true,
				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER_DESCRIPTION);
		options.addOption(ScmSupportToolsConstants.PARAMETER_REUSE_EXISTING_WORKSPACE_FLAG, false,
				ScmSupportToolsConstants.PARAMETER_REUSE_EXISTING_WORKSPACE_FLAG_DESCRIPTION);
		options.addOption(ScmSupportToolsConstants.PARAMETER_SKIP_UPLOADING_EXISTING_COMPONENT_FLAG, false,
				ScmSupportToolsConstants.PARAMETER_SKIP_UPLOADING_EXISTING_COMPONENT_FLAG_DESCRIPTION);
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
				&& cmd.hasOption(ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID)
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
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_PROTOTYPE, ScmSupportToolsConstants.PARAMETER_INPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_INPUTFOLDER_PROTOTYPE);
		// Parameter and description
		logger.info(
				"\n\tParameter description: \n\t -{} \t {} \n\t -{} \t{} \n\t -{} \t {} \n\t -{} \t {} \n\t -{} \t {} \n\t -{} \t {}",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				SupportToolsFrameworkConstants.PARAMETER_COMMAND_DESCRIPTION,
				SupportToolsFrameworkConstants.PARAMETER_URL, SupportToolsFrameworkConstants.PARAMETER_URL_DESCRIPTION,
				SupportToolsFrameworkConstants.PARAMETER_USER,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_DESCRIPTION,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_DESCRIPTION,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_DESCRIPTION,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_INPUTFOLDER_DESCRIPTION);
		// Optional parameters
		logger.info("\n\tOptional parameter syntax: -{} {} -{} -{}",
				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER,
				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER_PROTOTYPE,
				ScmSupportToolsConstants.PARAMETER_REUSE_EXISTING_WORKSPACE_FLAG,
				ScmSupportToolsConstants.PARAMETER_SKIP_UPLOADING_EXISTING_COMPONENT_FLAG);
		// Optional parameters description
		logger.info("\n\tOptional parameter description: \n\t -{} \t{} \n\t -{} \t {}\n\t -{} \t {}",
				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER,
				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER_DESCRIPTION,
				ScmSupportToolsConstants.PARAMETER_REUSE_EXISTING_WORKSPACE_FLAG,
				ScmSupportToolsConstants.PARAMETER_REUSE_EXISTING_WORKSPACE_FLAG_DESCRIPTION,
				ScmSupportToolsConstants.PARAMETER_SKIP_UPLOADING_EXISTING_COMPONENT_FLAG,
				ScmSupportToolsConstants.PARAMETER_SKIP_UPLOADING_EXISTING_COMPONENT_FLAG_DESCRIPTION);
		// Examples
		logger.info("\n\tExample: -{} {} -{} {} -{} {} -{} {} -{} {} -{} {} -{} {}",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND, getCommandName(),
				SupportToolsFrameworkConstants.PARAMETER_URL, SupportToolsFrameworkConstants.PARAMETER_URL_EXAMPLE,
				SupportToolsFrameworkConstants.PARAMETER_USER, SupportToolsFrameworkConstants.PARAMETER_USER_ID_EXAMPLE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_EXAMPLE,
				SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA,
				SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA_EXAMPLE,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_EXAMPLE, ScmSupportToolsConstants.PARAMETER_INPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_INPUTFOLDER_EXAMPLE);
		// Optional parameter examples
		logger.info("\n\tExample optional parameter: -{} {} -{} -{}",
				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER,
				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER_EXAMPLE,
				ScmSupportToolsConstants.PARAMETER_REUSE_EXISTING_WORKSPACE_FLAG,
				ScmSupportToolsConstants.PARAMETER_SKIP_UPLOADING_EXISTING_COMPONENT_FLAG);
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
		final String scmWorkspace = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID);
		final String inputFolderPath = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_INPUTFOLDER);
		final String componentNameModifier = getCmd()
				.getOptionValue(ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER);
		reuseExistingWorkspace = getCmd().hasOption(ScmSupportToolsConstants.PARAMETER_REUSE_EXISTING_WORKSPACE_FLAG);
		skipUploadExistingComponent = getCmd()
				.hasOption(ScmSupportToolsConstants.PARAMETER_SKIP_UPLOADING_EXISTING_COMPONENT_FLAG);
		if (componentNameModifier != null) {
			logger.info("Using prefix '{}' on component names to force creation of new components.",
					componentNameModifier);
			setComponentNamePrefix(componentNameModifier);
		}

		try {
			File inputfolder = new File(inputFolderPath);
			if (!inputfolder.exists()) {
				logger.error("Error: Outputfolder '{}' does not exist.", inputFolderPath);
				return result;
			}
			if (!inputfolder.isDirectory()) {
				logger.error("Error: Outputfolder '{}' is not a directory.", inputFolderPath);
				return result;
			}
			fInputFolder = inputfolder;
			result = importWorkspace(getTeamRepository(), projectAreaName, scmWorkspace, getMonitor());
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
	private boolean importWorkspace(ITeamRepository teamRepository, String projectAreaName, String scmConnection,
			IProgressMonitor monitor)
			throws TeamRepositoryException, UnsupportedEncodingException, FileNotFoundException, IOException {

		// Find Or Create workspaceConnection
		logger.info("Find or create workspaceConnection '{}'...", scmConnection);
		IWorkspaceConnection targetWorkspace = null;
		List<IWorkspaceHandle> connections = ConnectionUtil.findWorkspacesByName(teamRepository, scmConnection,
				IWorkspaceSearchCriteria.ALL, monitor);
		if (connections.size() > 0) {
			if (!reuseExistingWorkspace) {
				logger.error("The workspace '{}' already exists.", scmConnection);
				return false;
			}
			List<? extends IWorkspaceConnection> connection = ConnectionUtil.getWorkspaceConnections(teamRepository,
					connections, monitor);
			targetWorkspace = connection.get(0);
		}
		if (targetWorkspace == null) {
			IWorkspaceManager wm = SCMPlatform.getWorkspaceManager(teamRepository);
			targetWorkspace = wm.createWorkspace(teamRepository.loggedInContributor(), scmConnection,
					"Testworkspace " + scmConnection, monitor);
		}

		// Find Project Area
		IProcessClientService processClient = (IProcessClientService) teamRepository
				.getClientLibrary(IProcessClientService.class);
		fArea = findProjectAreaByFQN(projectAreaName, processClient, monitor);

		// Get the component mapping
		HashMap<String, UUID> sourceComponentName2UUIDMap = new HashMap<String, UUID>(3000);
		HashMap<String, ArrayList<String>> sourcePar2ChildMap = new HashMap<String, ArrayList<String>>(3000);
		readComponentHierarchyMapping(sourceComponentName2UUIDMap, sourcePar2ChildMap);

		logger.info("Find or create components...");
		IWorkspaceManager wm = SCMPlatform.getWorkspaceManager(teamRepository);
		JSONArray jsonComponentMap = new JSONArray();
		// Run 1 to get a map for the components needed. Find or create the
		// components.
		HashMap<String, IComponentHandle> targetComponentMap = new HashMap<String, IComponentHandle>(
				sourcePar2ChildMap.size());
		Set<String> compKeys = sourcePar2ChildMap.keySet();
		int currentComponent = 1;
		int noOfComponents = compKeys.size();
		Set<String> missingComponents = new HashSet<String>(noOfComponents);
		for (String compName : compKeys) {
			logger.info("\tComponent {} of {} '{}'", currentComponent++, noOfComponents, compName);
			IComponentHandle foundComponent = findComponentByName(wm, compName, monitor);
			if (foundComponent == null) {
				missingComponents.add(compName);
				foundComponent = createComponent(teamRepository, monitor, wm, compName);
			}
			targetComponentMap.put(compName, foundComponent);
			JSONObject jsonComponent = new JSONObject();
			jsonComponent.put(ScmSupportToolsConstants.JSON_COMPONENT_NAME, compName);
			jsonComponent.put(ScmSupportToolsConstants.JSON_SOURCE_COMPONENT_UUID,
					sourceComponentName2UUIDMap.get(compName).getUuidValue());
			jsonComponent.put(ScmSupportToolsConstants.JSON_TARGET_COMPONENT_UUID,
					foundComponent.getItemId().getUuidValue());
			jsonComponentMap.add(jsonComponent);
		}
		File jsonComponentMappingFile = new File(fInputFolder, ScmSupportToolsConstants.COMPONENT_MAPPING_JSON_FILE);
		logger.info("Persist component source to target UUID mapping '{}'...",
				jsonComponentMappingFile.getAbsolutePath());
		jsonComponentMap.serialize(new FileWriter(jsonComponentMappingFile), true);

		logger.info("Strip workspace from components...");
		removeAllCompoentsFormWorkspaceConnection(targetWorkspace, monitor);

		logger.info("Add components to workspace...");
		addComponentsToWorkspaceConnection(targetWorkspace, targetComponentMap, monitor);
		recreateComponentHierarchy(targetWorkspace, sourcePar2ChildMap, targetComponentMap, monitor);

		// Run 3 upload the source code
		uploadComponentContent(targetWorkspace, sourcePar2ChildMap, targetComponentMap, missingComponents, monitor);
		return true;
	}

	/**
	 * Recreate the component hierarchy for the repository workspace
	 * 
	 * @param targetWorkspace
	 * @param sourcePar2ChildMap
	 * @param targetComponentMap
	 * @param monitor
	 * @throws TeamRepositoryException
	 */
	private void recreateComponentHierarchy(IWorkspaceConnection targetWorkspace,
			HashMap<String, ArrayList<String>> sourcePar2ChildMap, HashMap<String, IComponentHandle> targetComponentMap,
			IProgressMonitor monitor) throws TeamRepositoryException {
		// Run 2 to get the child mapping
		logger.info("Recreate subcomponent structure in workspace...");
		Set<String> compKeys2 = sourcePar2ChildMap.keySet();
		int currentComponent = 1;
		int noOfComponents = compKeys2.size();
		for (String compName : compKeys2) {
			logger.info("\tComponent {} of {} '{}'", currentComponent++, noOfComponents, compName);
			IComponentHandle handle = targetComponentMap.get(compName);
			Collection<IComponentHandle> subcomponentsToAdd = new ArrayList<IComponentHandle>();
			ArrayList<String> children = sourcePar2ChildMap.get(compName);
			if (!children.isEmpty()) {
				for (String child : children) {
					IComponentHandle childHandle = targetComponentMap.get(child);
					subcomponentsToAdd.add(childHandle);
				}
				IChangeSetHandle subComponentChangeSet = targetWorkspace.createChangeSet(handle,
						"Subcomponents for " + compName, true, monitor);
				targetWorkspace.updateSubcomponentData(handle, subcomponentsToAdd, new ArrayList<IComponentHandle>(),
						subComponentChangeSet, monitor);
				targetWorkspace.closeChangeSets(Collections.singletonList(subComponentChangeSet), monitor);
			}
		}
	}

	/**
	 * Upload the content for the components.
	 * 
	 * @param targetWorkspace
	 * @param sourcePar2ChildMap
	 * @param targetComponentMap
	 * @param missingComponents
	 * @param monitor
	 * @throws TeamRepositoryException
	 * @throws Exception
	 */
	private void uploadComponentContent(final IWorkspaceConnection targetWorkspace,
			final HashMap<String, ArrayList<String>> sourcePar2ChildMap,
			final HashMap<String, IComponentHandle> targetComponentMap, final Set<String> missingComponents,
			IProgressMonitor monitor) throws TeamRepositoryException {
		logger.info("Import component data...");
		Set<String> compKeys3 = sourcePar2ChildMap.keySet();
		int currentComponent = 1;
		int noOfComponents = compKeys3.size();
		// Reuse the class
		for (String compName : compKeys3) {
			logger.info("\tComponent {} of {} '{}'", currentComponent++, noOfComponents, compName);
			if (skipUploadExistingComponent) {
				if (!missingComponents.contains(compName)) {
					logger.info("\t\tComponent exists skipping code upload..");
					continue;
				}
			}
			IComponentHandle handle = targetComponentMap.get(compName);
			File archiveFile = new File(fInputFolder, stripComponentNamePrefix(compName) + ".zip");
			ArchiveToSCMExtractor scmExt = new ArchiveToSCMExtractor();
			if (!scmExt.extractFileToComponent(archiveFile.getAbsolutePath(), targetWorkspace, handle,
					"Source for Component " + compName, monitor)) {
				System.out.println();
				logger.error("Exception extracting component '{}'", compName);
			}
			System.out.println();
			scmExt = null;
			archiveFile = null;
		}
	}

	/**
	 * Read the component structure and mapping from the input file.
	 * 
	 * @param sourceComponentName2UUIDMap
	 * @param sourcePar2ChildMap
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void readComponentHierarchyMapping(HashMap<String, UUID> sourceComponentName2UUIDMap,
			HashMap<String, ArrayList<String>> sourcePar2ChildMap)
			throws UnsupportedEncodingException, FileNotFoundException, IOException {
		File jsonInputFile = new File(fInputFolder, ScmSupportToolsConstants.HIERARCHY_JSON_FILE);
		Reader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(jsonInputFile), IFileContent.ENCODING_UTF_8));
		logger.info("Reading component structure from file '{}'...", jsonInputFile.getAbsolutePath());
		JSONArray comps = JSONArray.parse(reader);
		int currentComponent = 1;
		int noOfComponents = comps.size();
		for (Object comp : comps) {
			if (comp instanceof JSONObject) {
				String componentName = null;
				JSONObject jsonComp = (JSONObject) comp;
				Object oname = jsonComp.get(ScmSupportToolsConstants.JSON_COMPONENT_NAME);
				componentName = addComponentNamePrefix((String) oname);
				logger.info("\tComponent {} of {} '{}' done", currentComponent++, noOfComponents, componentName);
				String ouuid = (String) jsonComp.get(ScmSupportToolsConstants.JSON_COMPONENT_UUID);
				sourceComponentName2UUIDMap.put(componentName, UUID.valueOf(ouuid));
				ArrayList<String> childrenList = new ArrayList<String>(20);
				Object ochildren = jsonComp.get(ScmSupportToolsConstants.JSON_COMPONENT_CHILDREN);
				if (null != ochildren && ochildren instanceof JSONArray) {
					JSONArray children = (JSONArray) ochildren;
					for (Object ochild : children) {
						if (ochild instanceof JSONObject) {
							JSONObject child = (JSONObject) ochild;
							String childname = (String) child.get(ScmSupportToolsConstants.JSON_COMPONENT_NAME);
							childrenList.add(addComponentNamePrefix(childname));
						}
					}
				}
				sourcePar2ChildMap.put(componentName, childrenList);
			}
		}
	}

	/**
	 * Create the component.
	 * 
	 * @param teamRepository
	 * @param monitor
	 * @param wm
	 * @param compName
	 * @return
	 * @throws TeamRepositoryException
	 */
	private IComponentHandle createComponent(ITeamRepository teamRepository, IProgressMonitor monitor,
			IWorkspaceManager wm, String compName) throws TeamRepositoryException {
		IComponentHandle component;
		// Create Component
		component = wm.createComponent(compName, teamRepository.loggedInContributor(), monitor);
		wm.setComponentOwner(component, fArea, monitor);
		return component;
	}

	/**
	 * Find a component by its name.
	 * 
	 * @param wm
	 * @param compName
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	private IComponentHandle findComponentByName(IWorkspaceManager wm, String compName, IProgressMonitor monitor)
			throws TeamRepositoryException {
		IComponentSearchCriteria criteria = IComponentSearchCriteria.FACTORY.newInstance();
		criteria.setExactName(compName);
		List<IComponentHandle> found = wm.findComponents(criteria, Integer.MAX_VALUE, monitor);

		if (found.size() > 1) {
			logger.error("Ambiguous Component Name '{}'", compName);
			throw new RuntimeException("Ambiguous Component Name '{" + compName + "}'");
		}
		if (found.size() < 1) {
			return null;
		}
		return found.get(0);
	}

	/**
	 * Find a ProjectArea by fully qualified name The name has to be a fully
	 * qualified name with the full path e.g. "JKE Banking(Change
	 * Management)/Business Recovery Matters"
	 * 
	 * @param name
	 * @param processClient
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static IProjectArea findProjectAreaByFQN(String name, IProcessClientService processClient,
			IProgressMonitor monitor) throws TeamRepositoryException {
		IProcessArea processArea = findProcessAreaByFQN(name, processClient, monitor);
		if (null != processArea && processArea instanceof IProjectArea) {
			return (IProjectArea) processArea;
		}
		return null;
	}

	/**
	 * Find a ProcessArea by fully qualified name The name has to be a fully
	 * qualified name with the full path e.g. "JKE Banking(Change
	 * Management)/Business Recovery Matters"
	 * 
	 * @param name
	 * @param processClient
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static IProcessArea findProcessAreaByFQN(String name, IProcessClientService processClient,
			IProgressMonitor monitor) throws TeamRepositoryException {
		URI uri = getURIFromName(name);
		return (IProcessArea) processClient.findProcessArea(uri, IProcessItemService.ALL_PROPERTIES, monitor);
	}

	/**
	 * URI conversion to be able to find from a URI
	 * 
	 * @param name
	 * @return
	 */
	public static URI getURIFromName(String name) {
		URI uri = URI.create(name.replaceAll(" ", "%20"));
		return uri;
	}

	/**
	 * Removes all components from a workspace connection
	 * 
	 * @param workspaceConnection
	 * @param monitor
	 * @throws TeamRepositoryException
	 */
	@SuppressWarnings("rawtypes")
	private void removeAllCompoentsFormWorkspaceConnection(IWorkspaceConnection workspaceConnection,
			IProgressMonitor monitor) throws TeamRepositoryException {
		// Remove all components
		List wsComponents = workspaceConnection.getComponents();
		for (Object comp : wsComponents) {
			IComponentHandle cHandle = (IComponentHandle) comp;
			workspaceConnection.applyComponentOperations(
					Collections.singletonList(workspaceConnection.componentOpFactory().removeComponent(cHandle, false)),
					true, monitor);
		}
	}

	/**
	 * Adds components to a workspace connection
	 * 
	 * @param workspaceConnection
	 * @param extComponents
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	private IWorkspaceConnection addComponentsToWorkspaceConnection(IWorkspaceConnection workspaceConnection,
			HashMap<String, IComponentHandle> extComponents, IProgressMonitor monitor) throws TeamRepositoryException {

		// Add new components
		Set<String> componentNames = extComponents.keySet();
		int currentComponent = 1;
		int noOfComponents = componentNames.size();
		logger.info("\tAdding Components: '{}'", noOfComponents);
		for (String compName : componentNames) {
			logger.info("\tComponent {} of {} '{}' ", currentComponent++, noOfComponents, compName);
			IComponentHandle cHandle = (IComponentHandle) extComponents.get(compName);
			workspaceConnection.applyComponentOperations(
					Collections.singletonList(workspaceConnection.componentOpFactory().addComponent(cHandle, false)),
					true, monitor);
		}
		return workspaceConnection;
	}

	/**
	 * Add a name prefix to the component name.
	 * 
	 * @param name
	 * @return
	 */
	private String addComponentNamePrefix(String name) {
		if (fNamePrefix != null) {
			return fNamePrefix + name;
		}
		return name;
	}

	/**
	 * Remove the name prefix from the component name.
	 * 
	 * @param name
	 * @return
	 */
	private String stripComponentNamePrefix(String name) {
		if (fNamePrefix != null) {
			return name.substring(fNamePrefix.length());
		}
		return name;
	}

	/**
	 * @param fNameModifier
	 */
	private void setComponentNamePrefix(String fNameModifier) {
		this.fNamePrefix = fNameModifier;
	}

	/**
	 * This prints one '.' for every for 10 times it is called to show some
	 * progress. Can be used to show more fine grained progress.
	 */
	@SuppressWarnings("unused")
	private void showProgress() {
		fProgress++;
		if (fProgress % 10 == 9) {
			System.out.print(".");
		}
	}
}
