/*******************************************************************************
 * Copyright (c) 2012 - 2013, 2018 IBM Corporation.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *
 *    Ralph Schoon - Initial implementation
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.SupportToolsFrameworkConstants;
import com.ibm.js.team.supporttools.framework.framework.AbstractCommand;
import com.ibm.js.team.supporttools.framework.framework.ICommand;
import com.ibm.js.team.supporttools.scm.ScmSupportToolsConstants;
import com.ibm.js.team.supporttools.scm.utils.ArchiveToSCMExtractor;
import com.ibm.js.team.supporttools.scm.utils.ComponentUtil;
import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.process.client.IProcessItemService;
import com.ibm.team.process.common.IProcessArea;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
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
 */
public class ImportRepositoryWorkspace extends AbstractCommand implements ICommand {

	public static final Logger logger = LoggerFactory.getLogger(ImportRepositoryWorkspace.class);
	private File fInputFolder = null;
	private IAuditableHandle fArea;
	private String fNamePrefixr = null;

	/**
	 * Constructor, set the command name which will be used as option value for the
	 * command option. The name is used in the UIs and the option parser.
	 */
	public ImportRepositoryWorkspace() {
		super(ScmSupportToolsConstants.CMD_IMPORTWORKSPACE);
	}

	/**
	 * Method to add the options this command requires.
	 */
	@Override
	public Options addCommandOptions(Options options) {
		options.addOption(SupportToolsFrameworkConstants.PARAMETER_URL, true,
				SupportToolsFrameworkConstants.PARAMETER_URL_DESCRIPTION);
		options.addOption(SupportToolsFrameworkConstants.PARAMETER_USER, true,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_DESCRIPTION);
		options.addOption(SupportToolsFrameworkConstants.PARAMETER_PASSWORD, true,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_DESCRIPTION);
		options.addOption(SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA, true,
				SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA_DESCRIPTION);
		options.addOption(ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID, true,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_DESCRIPTION);
		options.addOption(ScmSupportToolsConstants.PARAMETER_INPUTFOLDER, true,
				ScmSupportToolsConstants.PARAMETER_INPUTFOLDER_DESCRIPTION);
		options.addOption(ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER, true,
				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER_DESCRIPTION);
		return options;
	}

	/**
	 * Method to check if the required options/parameters required to perform the
	 * command are available.
	 */
	@Override
	public boolean checkParameters(CommandLine cmd) {
		// Check for required options
		boolean isValid = true;

		if (!(cmd.hasOption(SupportToolsFrameworkConstants.PARAMETER_URL)
				&& cmd.hasOption(SupportToolsFrameworkConstants.PARAMETER_USER)
				&& cmd.hasOption(SupportToolsFrameworkConstants.PARAMETER_PASSWORD)
				&& cmd.hasOption(SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA)
				&& cmd.hasOption(ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID)
				&& cmd.hasOption(ScmSupportToolsConstants.PARAMETER_INPUTFOLDER))) {
			isValid = false;
		}
		return isValid;
	}

	/**
	 * Method to print the syntax in case of missing options.
	 */
	@Override
	public void printSyntax() {
		logger.info("{}", getCommandName());
		logger.info(
				"\n\tCreates a repository workspace and its components from a JSON file describing the workspace component hierarchy structure. Imports the contents of each component from a into a set of zip files. ");
		logger.info("\n\tSyntax : -{} {} -{} {} -{} {} -{} {} -{} {} [ -{} {} ]",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND, getCommandName(),
				SupportToolsFrameworkConstants.PARAMETER_URL, SupportToolsFrameworkConstants.PARAMETER_URL_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_USER, SupportToolsFrameworkConstants.PARAMETER_USER_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA,
				SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA_PROTOTYPE,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_PROTOTYPE, ScmSupportToolsConstants.PARAMETER_INPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_INPUTFOLDER_PROTOTYPE);
		logger.info("\tExample: -{} {} -{} {} -{} {} -{} {} -{} {}", SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				getCommandName(), SupportToolsFrameworkConstants.PARAMETER_URL,
				SupportToolsFrameworkConstants.PARAMETER_URL_EXAMPLE, SupportToolsFrameworkConstants.PARAMETER_USER,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_EXAMPLE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_EXAMPLE,
				SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA,
				SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA_EXAMPLE,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_EXAMPLE, ScmSupportToolsConstants.PARAMETER_INPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_INPUTFOLDER_EXAMPLE);

		logger.info("\tOptional parameter: -{} {}", ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER,
				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER_PROTOTYPE);
		logger.info("\tExample optional parameter: -{} {}", ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER,
				ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER_EXAMPLE);
	}

	/**
	 * The main method that executes the behavior of this command.
	 */
	@SuppressWarnings("unused")
	@Override
	public boolean execute() {
		logger.info("Executing Command {}", this.getCommandName());
		boolean result = false;
		// Execute the code
		// Get all the option values
		String repositoryURI = getCmd().getOptionValue(SupportToolsFrameworkConstants.PARAMETER_URL);
		String userId = getCmd().getOptionValue(SupportToolsFrameworkConstants.PARAMETER_USER);
		String userPassword = getCmd().getOptionValue(SupportToolsFrameworkConstants.PARAMETER_PASSWORD);
		String projectAreaName = getCmd().getOptionValue(SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA);
		String scmWorkspace = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID);
		String inputFolderPath = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_INPUTFOLDER);
		String componentNameModifier = getCmd()
				.getOptionValue(ScmSupportToolsConstants.PARAMETER_COMPONENT_NAME_MODIFIER);

		if (componentNameModifier != null) {
			logger.info("Using prefix '{}' to on component names to force creation of new components.",
					componentNameModifier);
			setComponentNameModifier(componentNameModifier);
		}

		TeamPlatform.startup();
		try {
			IProgressMonitor monitor = new NullProgressMonitor();
			ITeamRepository teamRepository = TeamPlatform.getTeamRepositoryService().getTeamRepository(repositoryURI);
			teamRepository.registerLoginHandler(new ITeamRepository.ILoginHandler() {
				public ILoginInfo challenge(ITeamRepository repository) {
					return new ILoginInfo() {
						public String getUserId() {
							return userId;
						}

						public String getPassword() {
							return userPassword;
						}
					};
				}
			});
			teamRepository.login(monitor);
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
			result = importWorkspace(teamRepository, projectAreaName, scmWorkspace, monitor);
		} catch (TeamRepositoryException e) {
			logger.error("TeamRepositoryException: {}", e.getMessage());
			// e.printStackTrace();
		} catch (IOException e) {
			logger.error("IOException: {}", e.getMessage());
			// e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			TeamPlatform.shutdown();
		}

		return result;
	}

	private boolean importWorkspace(ITeamRepository teamRepository, String projectAreaName, String scmConnection,
			IProgressMonitor monitor) throws Exception {

		// Find Or Create Workspace
		logger.info("Find or create repository workspace '{}'...", scmConnection);

		IWorkspaceConnection targetWorkspace = null;
		List<IWorkspaceHandle> connections = ComponentUtil.findWorkspacesByName(teamRepository, scmConnection,
				IWorkspaceSearchCriteria.WORKSPACES, monitor);
		if (connections.size() > 0) {
			logger.info("WorkspaceConnection '{}' already exists.", scmConnection);
			// logger.error("WorkspaceConnection '{}' ambiguous.", scmConnection);
			List<? extends IWorkspaceConnection> connection = ComponentUtil.getWorkspaceConnections(teamRepository,
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

		// Get the required components
//		HashMap<String,UUID> sourceComponentMap = new HashMap<String,UUID>(3000);		
		HashMap<String, UUID> sourceComponentName2UUIDMap = new HashMap<String, UUID>(3000);
		HashMap<String, ArrayList<String>> sourcePar2ChildMap = new HashMap<String, ArrayList<String>>(3000);
		File jsonInputFile = new File(fInputFolder, ScmSupportToolsConstants.HIERARCHY_JSON_FILE);
		Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(jsonInputFile), "UTF-8")); //$NON-NLS-1$
		logger.info("Reading component structure from file '{}'...", jsonInputFile.getAbsolutePath());
		JSONArray comps = JSONArray.parse(reader);
		for (Object comp : comps) {
			if (comp instanceof JSONObject) {
				String componentName = null;
				JSONObject jsonComp = (JSONObject) comp;
				Object oname = jsonComp.get(ScmSupportToolsConstants.COMPONENT_NAME);
				componentName = modifyNameForTests((String) oname);
				String ouuid = (String) jsonComp.get(ScmSupportToolsConstants.COMPONENT_UUID);
				sourceComponentName2UUIDMap.put(componentName, UUID.valueOf(ouuid));
				ArrayList<String> childrenList = new ArrayList<String>(20);
				Object ochildren = jsonComp.get(ScmSupportToolsConstants.COMPONENT_CHILDREN);
				if (null != ochildren && ochildren instanceof JSONArray) {
					JSONArray children = (JSONArray) ochildren;
					for (Object ochild : children) {
						if (ochild instanceof JSONObject) {
							JSONObject child = (JSONObject) ochild;
							String childname = (String) child.get(ScmSupportToolsConstants.COMPONENT_NAME);
							childrenList.add(modifyNameForTests(childname));
						}
					}
				}
				sourcePar2ChildMap.put(componentName, childrenList);
				logger.info("\tComponent -> '{}' done", componentName);
			}
		}

		logger.info("Find or create components...");
		IWorkspaceManager wm = SCMPlatform.getWorkspaceManager(teamRepository);
		JSONArray jsonComponentMap = new JSONArray();
		// Run 1 to get a map for the components needed. Find or create the components.
		HashMap<String, IComponentHandle> targetComponentMap = new HashMap<String, IComponentHandle>(
				sourcePar2ChildMap.size());
		Set<String> compKeys = sourcePar2ChildMap.keySet();
		for (String compName : compKeys) {
			logger.info("\tComponent '{}'", compName);
			IComponentHandle foundComponent = findComponentByName(wm, compName, monitor);
			if (foundComponent == null) {
				foundComponent = createComponent(teamRepository, monitor, wm, compName);
			}
			targetComponentMap.put(compName, foundComponent);
			JSONObject jsonComponent = new JSONObject();
			jsonComponent.put(ScmSupportToolsConstants.COMPONENT_NAME, compName);
			jsonComponent.put(ScmSupportToolsConstants.SOURCE_COMPONENT_UUID,
					sourceComponentName2UUIDMap.get(compName).getUuidValue());
			jsonComponent.put(ScmSupportToolsConstants.TARGET_COMPONENT_UUID,
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
		// Run 2 to get the child mapping
		logger.info("Recreate subcomponent structure in workspace...");
		Set<String> compKeys2 = sourcePar2ChildMap.keySet();
		for (String compName : compKeys2) {
			logger.info("\tComponent '{}'", compName);
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

		// Run 3 upload the source code
		logger.info("Import component data...");
		Set<String> compKeys3 = sourcePar2ChildMap.keySet();
		for (String compName : compKeys3) {
			logger.info("\tComponent '{}'", compName);
			IComponentHandle handle = targetComponentMap.get(compName);
			ArchiveToSCMExtractor scmExt = new ArchiveToSCMExtractor();
			File archiveFile = new File(fInputFolder, normalizeName(compName) + ".zip");
			if (!scmExt.extractFileToComponent(archiveFile.getAbsolutePath(), targetWorkspace, handle,
					"Source for Component " + compName, monitor)) {
				System.out.println();
				throw new Exception("Exception extracting " + compName);
			}
			System.out.println();
		}
		return true;
	}

	private IComponentHandle createComponent(ITeamRepository teamRepository, IProgressMonitor monitor,
			IWorkspaceManager wm, String compName) throws TeamRepositoryException {
		IComponentHandle component;
		// Create Component
		component = wm.createComponent(compName, teamRepository.loggedInContributor(), monitor);
		wm.setComponentOwner(component, fArea, monitor);
		return component;
	}

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
		Collection<IComponentHandle> components = extComponents.values();
		for (Object comp : components) {
			IComponentHandle cHandle = (IComponentHandle) comp;
			workspaceConnection.applyComponentOperations(
					Collections.singletonList(workspaceConnection.componentOpFactory().addComponent(cHandle, false)),
					true, monitor);
		}
		return workspaceConnection;
	}

	private String modifyNameForTests(String name) {
		if (fNamePrefixr != null) {
			return fNamePrefixr + name;
		}
		return name;
	}

	private String normalizeName(String name) {
		if (fNamePrefixr != null) {
			return name.substring(fNamePrefixr.length());
		}
		return name;
	}

	private void setComponentNameModifier(String fNameModifier) {
		this.fNamePrefixr = fNameModifier;
	}

}
