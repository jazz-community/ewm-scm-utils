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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
import com.ibm.team.filesystem.client.FileSystemCore;
import com.ibm.team.filesystem.client.IComponentHierarchyManager;
import com.ibm.team.filesystem.client.IFileContentManager;
import com.ibm.team.filesystem.common.IFileItem;
import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.process.client.IProcessItemService;
import com.ibm.team.process.common.IProcessArea;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.common.IAuditableHandle;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.repository.common.UUID;
import com.ibm.team.repository.common.json.JSONArray;
import com.ibm.team.repository.common.json.JSONObject;
import com.ibm.team.scm.client.IConfiguration;
import com.ibm.team.scm.client.IFlowNodeConnection;
import com.ibm.team.scm.client.IWorkspaceConnection;
import com.ibm.team.scm.client.IWorkspaceManager;
import com.ibm.team.scm.client.SCMPlatform;
import com.ibm.team.scm.common.IChangeSetHandle;
import com.ibm.team.scm.common.IComponent;
import com.ibm.team.scm.common.IComponentHandle;
import com.ibm.team.scm.common.IComponentHierarchyNode;
import com.ibm.team.scm.common.IComponentHierarchyResult;
import com.ibm.team.scm.common.IFolder;
import com.ibm.team.scm.common.IFolderHandle;
import com.ibm.team.scm.common.IVersionable;
import com.ibm.team.scm.common.IVersionableHandle;
import com.ibm.team.scm.common.IWorkspaceHandle;
import com.ibm.team.scm.common.dto.IComponentSearchCriteria;
import com.ibm.team.scm.common.dto.IWorkspaceSearchCriteria;

/**
 */
public class ImportRepositoryWorkspace extends AbstractCommand implements ICommand {

	public final String OBFUSCATE_MODE = "obfuscate";
	public static final Logger logger = LoggerFactory.getLogger(ImportRepositoryWorkspace.class);
	public static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
	public boolean fPreserve = false;
	private Object fConfidentialityMode;
	private File fOutputFolder = null;
	private IAuditableHandle fArea;

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
		options.addOption(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER, true,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_DESCRIPTION);
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
				&& cmd.hasOption(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER))) {
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
		logger.info("\n\tExports the contents of a repository workspace into a set of zip files.");
		logger.info("\n\tSyntax : -{} {} -{} {} -{} {} -{} {} -{} {} [ -{} {} ]",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND, getCommandName(),
				SupportToolsFrameworkConstants.PARAMETER_URL, SupportToolsFrameworkConstants.PARAMETER_URL_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_USER, SupportToolsFrameworkConstants.PARAMETER_USER_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA,
				SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA_PROTOTYPE,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_PROTOTYPE, ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_PROTOTYPE
//				,
//				SupportToolsFrameworkConstants.PARAMETER_CSV_FILE_PATH,
//				SupportToolsFrameworkConstants.PARAMETER_CSV_FILE_PATH_PROTOTYPE,
//				SupportToolsFrameworkConstants.PARAMETER_CSV_DELIMITER,
//				SupportToolsFrameworkConstants.PARAMETER_CSV_DELIMITER_PROTOTYPE
		);
		logger.info("\tExample: -{} {} -{} {} -{} {} -{} {} -{} {}", SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				getCommandName(), SupportToolsFrameworkConstants.PARAMETER_URL,
				SupportToolsFrameworkConstants.PARAMETER_URL_EXAMPLE, SupportToolsFrameworkConstants.PARAMETER_USER,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_EXAMPLE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_EXAMPLE,
				SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA,
				SupportToolsFrameworkConstants.PARAMETER_PROJECT_AREA_EXAMPLE,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_EXAMPLE, ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_EXAMPLE
//				,
//				SupportToolsFrameworkConstants.PARAMETER_CSV_FILE_PATH,
//				SupportToolsFrameworkConstants.PARAMETER_CSV_FILE_PATH_EXAMPLE
		);

		logger.info("\tOptional parameter: -{} {}"
//				, 
//				SupportToolsFrameworkConstants.PARAMETER_CSV_DELIMITER,
//				SupportToolsFrameworkConstants.PARAMETER_CSV_DELIMITER_PROTOTYPE
		);
		logger.info("\tExample optional parameter: -{} {}"
//				, 
//				SupportToolsFrameworkConstants.PARAMETER_CSV_DELIMITER,
//				SupportToolsFrameworkConstants.PARAMETER_CSV_DELIMITER_EXAMPLE
		);
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
		String outputFolderPath = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER);

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
			File outputfolder = new File(outputFolderPath);
			if (!outputfolder.exists()) {
				logger.error("Error: Outputfolder '{}' does not exist.", outputFolderPath);
				return result;
			}
			if (!outputfolder.isDirectory()) {
				logger.error("Error: Outputfolder '{}' is not a directory.", outputFolderPath);
				return result;
			}
			fOutputFolder = outputfolder;
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

	
	

	private boolean importWorkspace(ITeamRepository teamRepository, String projectAreaName, String scmConnection, IProgressMonitor monitor) throws Exception {

		boolean result=false;
		String nameExt = "Test";
		
		// Find Or Create Workspace
		
		IWorkspaceConnection targetWorkspace=null;
		List<IWorkspaceHandle> connections = ComponentUtil.findWorkspacesByName(teamRepository, scmConnection,
				IWorkspaceSearchCriteria.WORKSPACES, monitor);
		if (connections.size() > 0) {
			logger.info("WorkspaceConnection '{}' already exists.", scmConnection);
			//logger.error("WorkspaceConnection '{}' ambiguous.", scmConnection);
			List<? extends IWorkspaceConnection> connection = ComponentUtil.getWorkspaceConnections(teamRepository, connections, monitor);
			targetWorkspace = connection.get(0);
		}
		if(targetWorkspace==null) {
			IWorkspaceManager wm = SCMPlatform
					.getWorkspaceManager(teamRepository);
			targetWorkspace = wm.createWorkspace(
					teamRepository.loggedInContributor(), scmConnection,
					"Testworkspace " + scmConnection, monitor);			
		}

		// Find Project Area
		IProcessClientService processClient = (IProcessClientService) teamRepository.getClientLibrary(IProcessClientService.class);
		fArea = findProjectAreaByFQN(projectAreaName, processClient, monitor);
		
		// Get the required components
//		HashMap<String,UUID> sourceComponentMap = new HashMap<String,UUID>(3000);
		HashMap<String,ArrayList<String>> sourcePar2ChildMap = new HashMap<String,ArrayList<String>>(3000);
		Reader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(fOutputFolder,ScmSupportToolsConstants.HIERARCHY_JSON_FILE)), "UTF-8")); //$NON-NLS-1$
		JSONArray comps = JSONArray.parse(reader);
		for (Object comp : comps) {
			if (comp instanceof JSONObject) {
				String componentName = null;
				UUID compUUID = null; 
				JSONObject jsonComp = (JSONObject) comp;
				Object oname = jsonComp.get(ScmSupportToolsConstants.COMPONENT_NAME);
				if (oname instanceof String) {
					componentName = (String) oname + nameExt;
				}
				Object ouuid = jsonComp.get(ScmSupportToolsConstants.COMPONENT_UUID);
				if (ouuid instanceof String) {
					String componentUUID = (String) ouuid;
					compUUID = UUID.valueOf(componentUUID);
				}
				ArrayList<String> childrenList = new ArrayList<String>(20);
				Object ochildren = jsonComp.get(ScmSupportToolsConstants.COMPONENT_CHILDREN);
				if (null!=ochildren && ochildren instanceof JSONArray) {
					JSONArray children = (JSONArray) ochildren;
					for (Object ochild : children) {
						if (ochild instanceof JSONObject) {
							JSONObject child = (JSONObject) ochild;
							String childname = (String)child.get(ScmSupportToolsConstants.COMPONENT_NAME);
							childrenList.add(childname + nameExt);  
						}						
					}
				}
				sourcePar2ChildMap.put(componentName, childrenList);
				//sourceComponentMap.put(componentName, compUUID);
				logger.info("Component -> '{}'", jsonComp.toString());		
			}
		}
		IWorkspaceManager wm = SCMPlatform.getWorkspaceManager(teamRepository);

		// Run 1 to get a map for the components needed. Find or create the components.
		HashMap<String, IComponentHandle> targetComponentMap = new HashMap<String, IComponentHandle>(sourcePar2ChildMap.size());
		Set<String> compKeys = sourcePar2ChildMap.keySet();
		for (String compName : compKeys) {
			IComponentHandle foundComponent = findComponentByName(wm, compName, monitor);
			if(foundComponent==null) {
				foundComponent = createComponent(teamRepository, monitor, wm, compName);
			}	
			targetComponentMap.put(compName, foundComponent);
		}
		
		removeAllCompoentsFormWorkspaceConnection(targetWorkspace, monitor);
		addComponentsToWorkspaceConnection(targetWorkspace, targetComponentMap, monitor);
		// Run 2 to get the child mapping
		Set<String> compKeys2 = sourcePar2ChildMap.keySet();
		for (String compName : compKeys2) {
			IComponentHandle handle = targetComponentMap.get(compName);
			Collection<IComponentHandle> subcomponentsToAdd = new ArrayList<IComponentHandle>();
			ArrayList<String> children = sourcePar2ChildMap.get(compName);
			if (!children.isEmpty()) {
				for (String child : children) {
					IComponentHandle childHandle = targetComponentMap.get(child);
					subcomponentsToAdd.add(childHandle);
				}
				IChangeSetHandle changeSet = targetWorkspace.createChangeSet(handle, "Subcomponents for " + compName,
						true, monitor);
				targetWorkspace.updateSubcomponentData(handle, subcomponentsToAdd, new ArrayList<IComponentHandle>(),
						changeSet, monitor);
			}
		}
		
		// Run 3 upload the source code 
		Set<String> compKeys3 = sourcePar2ChildMap.keySet();
		for (String compName : compKeys3) {
			IComponentHandle handle = targetComponentMap.get(compName);
			ArchiveToSCMExtractor scmExt = new ArchiveToSCMExtractor();
			File archiveFile = new File(fOutputFolder,compName.substring(0, compName.length()-4) +".zip");
			scmExt.extractFileToComponent(archiveFile.getAbsolutePath(), targetWorkspace, handle, "Source for Component", monitor);
		}
		

		return true;
	}

	private IComponentHandle createComponent(ITeamRepository teamRepository, IProgressMonitor monitor, IWorkspaceManager wm,
			String compName) throws TeamRepositoryException {
		IComponentHandle component;
		// Create Component
		component = wm.createComponent(compName,
				teamRepository.loggedInContributor(), monitor);
		wm.setComponentOwner(component, fArea, monitor);
		return component;
	}

	private IComponentHandle findComponentByName(IWorkspaceManager wm, String compName, IProgressMonitor monitor) throws TeamRepositoryException {
		IComponentSearchCriteria criteria = IComponentSearchCriteria.FACTORY
				.newInstance();
		criteria.setExactName(compName);
		List<IComponentHandle> found = wm.findComponents(criteria,
				Integer.MAX_VALUE, monitor);

		if (found.size() > 1) {
			logger.error("Ambiguous Component Name '{}'", compName);
			throw new RuntimeException("Ambiguous Component Name '{"+compName+"}'");
		}
		if(found.size()<1) {
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
	private void removeAllCompoentsFormWorkspaceConnection(
			IWorkspaceConnection workspaceConnection, IProgressMonitor monitor)
			throws TeamRepositoryException {
		// Remove all components
		List wsComponents = workspaceConnection.getComponents();
		for (Object comp : wsComponents) {
			IComponentHandle cHandle = (IComponentHandle) comp;
			workspaceConnection.applyComponentOperations(Collections
					.singletonList(workspaceConnection.componentOpFactory()
							.removeComponent(cHandle, false)), true, monitor);
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
	private IWorkspaceConnection addComponentsToWorkspaceConnection(
			IWorkspaceConnection workspaceConnection,
			HashMap<String, IComponentHandle> extComponents,
			IProgressMonitor monitor) throws TeamRepositoryException {

		// Add new components
		Collection<IComponentHandle> components = extComponents.values();
		for (Object comp : components) {
			IComponentHandle cHandle = (IComponentHandle) comp;
			workspaceConnection.applyComponentOperations(Collections
					.singletonList(workspaceConnection.componentOpFactory()
							.addComponent(cHandle, false)), true, monitor);
		}
		return workspaceConnection;
	}

}
