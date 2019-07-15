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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import com.ibm.team.filesystem.client.FileSystemCore;
import com.ibm.team.filesystem.client.IComponentHierarchyManager;
import com.ibm.team.filesystem.client.IFileContentManager;
import com.ibm.team.filesystem.common.IFileItem;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.repository.common.UUID;
import com.ibm.team.repository.common.json.JSONArray;
import com.ibm.team.repository.common.json.JSONObject;
import com.ibm.team.scm.client.IConfiguration;
import com.ibm.team.scm.client.IFlowNodeConnection;
import com.ibm.team.scm.client.IWorkspaceConnection;
import com.ibm.team.scm.client.IWorkspaceManager;
import com.ibm.team.scm.client.SCMPlatform;
import com.ibm.team.scm.common.IComponent;
import com.ibm.team.scm.common.IComponentHandle;
import com.ibm.team.scm.common.IComponentHierarchyNode;
import com.ibm.team.scm.common.IComponentHierarchyResult;
import com.ibm.team.scm.common.IFolder;
import com.ibm.team.scm.common.IFolderHandle;
import com.ibm.team.scm.common.IVersionable;
import com.ibm.team.scm.common.IVersionableHandle;
import com.ibm.team.scm.common.IWorkspaceHandle;
import com.ibm.team.scm.common.dto.IWorkspaceSearchCriteria;

/**
 */
public class ExportRepositoryWorkspace extends AbstractCommand implements ICommand {

	public final String OBFUSCATE_MODE = "obfuscate";
	public static final Logger logger = LoggerFactory.getLogger(ExportRepositoryWorkspace.class);
	public static final String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
	public boolean fPreserve = false;
	private Object fConfidentialityMode;
	private File fOutputFolder = null;

	/**
	 * Constructor, set the command name which will be used as option value for the
	 * command option. The name is used in the UIs and the option parser.
	 */
	public ExportRepositoryWorkspace() {
		super(ScmSupportToolsConstants.CMD_EXPORTWORKSPACE);
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
			result = exportWorkspace(teamRepository, scmWorkspace, monitor);
		} catch (TeamRepositoryException e) {
			logger.error("TeamRepositoryException: {}", e.getMessage());
			// e.printStackTrace();
		} catch (IOException e) {
			logger.error("IOException: {}", e.getMessage());
			// e.printStackTrace();
		} finally {
			TeamPlatform.shutdown();
		}

		return result;
	}

	/**
	 * @param teamRepository
	 * @param outputfolder
	 * @param scmConnection
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 * @throws IOException
	 */
	private boolean exportWorkspace(ITeamRepository teamRepository, String scmConnection, IProgressMonitor monitor)
			throws TeamRepositoryException, IOException {
		boolean result = false;

		List<? extends IWorkspaceConnection> connections = findConnectionByName(teamRepository, scmConnection,
				IWorkspaceSearchCriteria.WORKSPACES, monitor);
		if (connections.size() < 1) {
			logger.error("Error: WorkspaceConnection '{}' not found.", scmConnection);
			return result;
		}
		if (connections.size() > 1) {
			logger.error("Error: WorkspaceConnection '{}' not unique.", scmConnection);
			return result;
		}
		IWorkspaceConnection connection = connections.get(0);

//		List wsComponents = connection.getComponents();
//		List<IComponent> components = resolveComponents(teamRepository, wsComponents, monitor);
//		for (Object comp : components) {
//			IComponent component = (IComponent) comp;
//			logger.info("Component Name '{}' UUID '{}'", component.getName(), component.getItemId().getUuidValue());
//		}
//		JSONObject jsonhierarchy = new JSONObject();
//		jsonhierarchy hierarchy.put(key, value);
		// TODO
		IComponentHierarchyResult hierarchy = connection.getComponentHierarchy(new ArrayList<IComponentHandle>());
		writeHierarchy(teamRepository, hierarchy, monitor);
	//	IFlowNodeConnection conn = null;
		// IWorkspaceConnection#updateSubcomponentData(...).
//		connection.applyComponentOperations(Collections
//				.singletonList(connection.componentOpFactory()
//						.addComponent((IComponentHandle) components.get(0), false)), true, monitor);
		/**
		 * There is no out-of-the-box feature which will generate a load rule that will
		 * load and respect the component hierarchy in the workspace.
		 * 
		 * You might have to write your own code to do this. The client-side API for
		 * retrieving a component hierarchy of a particular stream or repository
		 * workspace is: IWorkspaceConnection.getComponentHierarchy() (although
		 * technically the method is on IFlowNodeConnection, which is the parent class
		 * of IWorkspaceConnection). From there you would have to walk the hierarchy to
		 * determine what you need. Note: There is no API to give the 'parents' of a
		 * given component. A component only knows about it's children, and not the
		 * other way around. Also be aware that a given component may appear in multiple
		 * hierarchy branches (i.e. have multiple parents).
		 */
		logger.info("Ramdomizing Components...");
		Collection<IComponentHandle> components = hierarchy.getFlattenedElementsMap().values();
		for (Iterator<IComponentHandle> iterator = components.iterator(); iterator.hasNext();) {
			IComponentHandle handle = (IComponentHandle) iterator.next();
		}
		result = packageComponentHandles(teamRepository, fOutputFolder, connection, components, monitor);			
		return result;
	}

	private void writeHierarchy(ITeamRepository teamRepository, IComponentHierarchyResult hierarchy,
			IProgressMonitor monitor) throws TeamRepositoryException, UnsupportedEncodingException, IOException {
		Map<UUID, Collection<IComponentHandle>> par2Child = hierarchy.getParentToChildrenMap();
		//Map<UUID, Collection<IComponentHandle>> chil2Par = hierarchy.getChildToParentsMap();

		Map<UUID, IComponentHandle> flat = hierarchy.getFlattenedElementsMap();
		createChildMap(teamRepository, flat, par2Child, monitor);	
	}

	private void printHierarchy(ITeamRepository teamRepository, IComponentHierarchyResult hierarchy,
			IProgressMonitor monitor) throws TeamRepositoryException, UnsupportedEncodingException, IOException {
		Map<UUID, Collection<IComponentHandle>> par2Child = hierarchy.getParentToChildrenMap();
		//Map<UUID, Collection<IComponentHandle>> chil2Par = hierarchy.getChildToParentsMap();

		Map<UUID, IComponentHandle> flat = hierarchy.getFlattenedElementsMap();
		Collection<IComponentHandle> allComponents = flat.values();
		createChildMap(teamRepository, flat, par2Child, monitor);
		
//		List componentList = new ArrayList<>(allComponents);
//		List<IComponent> components = resolveComponents(teamRepository, componentList, monitor);
//		
//		writeComponents(components);
//		Collection<IComponentHierarchyNode> roots = hierarchy.getRoots();
//
//		writeRoots(teamRepository, roots, monitor);
//
//		writeChildMap(teamRepository, flat, par2Child, monitor);
	}

	private void createChildMap(ITeamRepository teamRepository, Map<UUID, IComponentHandle> flat,
			Map<UUID, Collection<IComponentHandle>> par2Child, IProgressMonitor monitor) throws TeamRepositoryException, UnsupportedEncodingException, IOException {
		logger.info("Hierarchy...");
		
		JSONArray jsonhierarchy = new JSONArray();

//		FileOutputStream out = new FileOutputStream(new File(fOutputFolder, "hierarchymap.txt"));
		try {
			Set parents = par2Child.keySet();
			for (Iterator iterator = parents.iterator(); iterator.hasNext();) {
				UUID parent = (UUID) iterator.next();
				IComponent parentComp = resolveComponent(teamRepository, flat.get(parent), monitor);
				JSONObject component = new JSONObject();
				component.put(ScmSupportToolsConstants.COMPONENT_NAME, parentComp.getName());
				component.put(ScmSupportToolsConstants.COMPONENT_UUID, parentComp.getItemId().getUuidValue());
				JSONArray jsonChildren = new JSONArray();
				Collection<IComponentHandle> children = par2Child.get(parent);
				for (Iterator<IComponentHandle> childIter = children.iterator(); childIter.hasNext();) {
					IComponentHandle handle = (IComponentHandle) childIter.next();
					IComponent child = resolveComponent(teamRepository, handle, monitor);
					JSONObject childComponent = new JSONObject();
					childComponent.put(ScmSupportToolsConstants.COMPONENT_NAME, child.getName());
					childComponent.put(ScmSupportToolsConstants.COMPONENT_UUID, child.getItemId().getUuidValue());
					jsonChildren.add(childComponent);
					component.put(ScmSupportToolsConstants.COMPONENT_CHILDREN, jsonChildren);
//					logger.info("Map '{}' UUID '{}' '{}' UUID '{}'", parentComp.getName(), parentComp.getItemId().getUuidValue(), child.getName(), child.getItemId().getUuidValue());
//					String componentInfo = parentComp.getName() + " " + parentComp.getItemId().getUuidValue() + " " + child.getName() + " " + child.getItemId().getUuidValue() + "\n";
					//out.write(componentInfo.getBytes("UTF-8"));
					
				}
				jsonhierarchy.add(component);
			}
			jsonhierarchy.serialize(new FileWriter(new File(fOutputFolder,ScmSupportToolsConstants.HIERARCHY_JSON_FILE)), true);
//			for (IComponentHierarchyNode node : flat) {
//				IComponent comp = resolveComponent(teamRepository, node.getComponentHandle(), monitor);
//				logger.info("Root '{}' UUID '{}'", comp.getName(), comp.getItemId().getUuidValue());
//				String componentInfo = comp.getName() + " " + comp.getItemId().getUuidValue() + "\n";
//				out.write(componentInfo.getBytes("UTF-8"));
//			}
			//out.flush();
		} finally {
			//out.close();
		}
	}

	
	private void writeChildMap(ITeamRepository teamRepository, Map<UUID, IComponentHandle> flat,
			Map<UUID, Collection<IComponentHandle>> par2Child, IProgressMonitor monitor) throws TeamRepositoryException, UnsupportedEncodingException, IOException {
		logger.info("Hierarchy...");
		FileOutputStream out = new FileOutputStream(new File(fOutputFolder, "hierarchymap.txt"));
		try {
			Set parents = par2Child.keySet();
			for (Iterator iterator = parents.iterator(); iterator.hasNext();) {
				UUID parent = (UUID) iterator.next();
				IComponent parentComp = resolveComponent(teamRepository, flat.get(parent), monitor);
				Collection<IComponentHandle> children = par2Child.get(parent);
				for (Iterator<IComponentHandle> childIter = children.iterator(); childIter.hasNext();) {
					IComponentHandle handle = (IComponentHandle) childIter.next();

					IComponent child = resolveComponent(teamRepository, handle, monitor);
					logger.info("Map '{}' UUID '{}' '{}' UUID '{}'", parentComp.getName(), parentComp.getItemId().getUuidValue(), child.getName(), child.getItemId().getUuidValue());
					String componentInfo = parentComp.getName() + " " + parentComp.getItemId().getUuidValue() + " " + child.getName() + " " + child.getItemId().getUuidValue() + "\n";
					out.write(componentInfo.getBytes("UTF-8"));
					
				}
			}
			
//			for (IComponentHierarchyNode node : flat) {
//				IComponent comp = resolveComponent(teamRepository, node.getComponentHandle(), monitor);
//				logger.info("Root '{}' UUID '{}'", comp.getName(), comp.getItemId().getUuidValue());
//				String componentInfo = comp.getName() + " " + comp.getItemId().getUuidValue() + "\n";
//				out.write(componentInfo.getBytes("UTF-8"));
//			}
			out.flush();
		} finally {
			out.close();
		}
	}

	private void writeRoots(ITeamRepository teamRepository, Collection<IComponentHierarchyNode> roots,
			IProgressMonitor monitor) throws TeamRepositoryException, UnsupportedEncodingException, IOException {
		logger.info("Roots...");
		FileOutputStream out = new FileOutputStream(new File(fOutputFolder, "rootcomponents.txt"));
		try {
			for (IComponentHierarchyNode node : roots) {
				IComponent comp = resolveComponent(teamRepository, node.getComponentHandle(), monitor);
				logger.info("Root '{}' UUID '{}'", comp.getName(), comp.getItemId().getUuidValue());
				String componentInfo = comp.getName() + " " + comp.getItemId().getUuidValue() + "\n";
				out.write(componentInfo.getBytes("UTF-8"));
			}
			out.flush();
		} finally {
			out.close();
		}
	}

	private void writeComponents(List<IComponent> components) throws UnsupportedEncodingException, IOException {
		logger.info("Components...");
		FileOutputStream out = new FileOutputStream(new File(fOutputFolder, "components.txt"));
		try {
			for (IComponent comp : components) {
				logger.info("'{}' UUID '{}'", comp.getName(), comp.getItemId().getUuidValue());
				String componentInfo = comp.getName() + " " + comp.getItemId().getUuidValue() + "\n";
				out.write(componentInfo.getBytes("UTF-8"));
			}
			out.flush();
		} finally {
			out.close();
		}
	}

	/**
	 * @param teamRepository
	 * @param outputfolder
	 * @param connection
	 * @param components
	 * @param monitor
	 * @return
	 * @throws IOException
	 * @throws TeamRepositoryException
	 */
	private boolean packageComponents(ITeamRepository teamRepository, File outputfolder,
			IWorkspaceConnection connection, List<IComponent> components, IProgressMonitor monitor)
			throws IOException, TeamRepositoryException {
		boolean result = true;

		for (IComponent component : components) {
			logger.info("Packing component'{}' UUID '{}'", component.getName(), component.getItemId().getUuidValue());
			result &= packageComponent(teamRepository, connection, component, monitor);
		}
		return result;
	}

	private boolean packageComponentHandles(ITeamRepository teamRepository, File outputfolder,
			IWorkspaceConnection connection, List<IComponentHandle> componentHandles, IProgressMonitor monitor)
			throws IOException, TeamRepositoryException {
		List<IComponent> components = resolveComponents(teamRepository, componentHandles, monitor);

		return packageComponents(teamRepository, outputfolder, connection, components, monitor);
	}

	private boolean packageComponentHandles(ITeamRepository teamRepository, File outputFolder,
			IWorkspaceConnection connection, Collection<IComponentHandle> componentHandles, IProgressMonitor monitor) throws TeamRepositoryException, IOException {
		List<IComponent> components = resolveComponents(teamRepository, new ArrayList<IComponentHandle>(componentHandles), monitor);

		return packageComponents(teamRepository, outputFolder, connection, components, monitor);
	}

	/**
	 * @param teamRepository
	 * @param workspaceConnection
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<IComponent> getComponents(ITeamRepository teamRepository, IWorkspaceConnection workspaceConnection,
			IProgressMonitor monitor) throws TeamRepositoryException {
		// Remove all components
		List wsComponents = workspaceConnection.getComponents();

		return resolveComponents(teamRepository, wsComponents, monitor);

	}

	private List<IComponent> resolveComponents(ITeamRepository teamRepository, List<IComponentHandle> wsComponents,
			IProgressMonitor monitor) throws TeamRepositoryException {
		@SuppressWarnings("unchecked")
		List<IComponent> components = (List<IComponent>) teamRepository.itemManager().fetchCompleteItems(wsComponents,
				IItemManager.DEFAULT, monitor);
		return components;
	}

	private IComponent resolveComponent(ITeamRepository teamRepository, IComponentHandle handle,
			IProgressMonitor monitor) throws TeamRepositoryException {
		@SuppressWarnings("unchecked")
		IComponent component = (IComponent) teamRepository.itemManager().fetchCompleteItem(handle, IItemManager.DEFAULT,
				monitor);
		return component;
	}

	/**
	 * @param teamRepository
	 * @param outputfolder
	 * @param connection
	 * @param component
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 * @throws IOException
	 */
	private boolean packageComponent(ITeamRepository teamRepository, IWorkspaceConnection connection,
			IComponent component, IProgressMonitor monitor) throws TeamRepositoryException, IOException {
		boolean result = false;
		// Start walking the workspace contents
		IFileContentManager contentManager = FileSystemCore.getContentManager(teamRepository);
		File base = fOutputFolder;

		FileOutputStream out = new FileOutputStream(new File(base, component.getName().trim() + ".zip"));
		try {
			ZipOutputStream zos = new ZipOutputStream(out);

			IConfiguration compConfig = connection.configuration(component);
			// Fetch the items at the root of each component. We do this to initialize our
			// queue of stuff to download.
			@SuppressWarnings("unchecked")
			Map<String, IVersionableHandle> handles = compConfig.childEntriesForRoot(monitor);
			@SuppressWarnings("unchecked")
			List<IVersionable> items = compConfig
					.fetchCompleteItems(new ArrayList<IVersionableHandle>(handles.values()), monitor);
			loadDirectory(contentManager, compConfig, zos, "", items, monitor);

			zos.close();
			result = true;

		} finally {
			out.close();
		}
		return result;
	}

	/**
	 * @param contentManager
	 * @param compConfig
	 * @param zos
	 * @param path
	 * @param items
	 * @param monitor
	 * @throws IOException
	 * @throws TeamRepositoryException
	 */
	private void loadDirectory(IFileContentManager contentManager, IConfiguration compConfig, ZipOutputStream zos,
			String path, List<IVersionable> items, IProgressMonitor monitor)
			throws IOException, TeamRepositoryException {

		for (IVersionable v : items) {
			if (v instanceof IFolder) {
				// Write the directory
				String dirPath = path + v.getName() + "/";
				zos.putNextEntry(new ZipEntry(dirPath));

				@SuppressWarnings("unchecked")
				Map<String, IVersionableHandle> children = compConfig.childEntries((IFolderHandle) v, monitor);
				@SuppressWarnings("unchecked")
				List<IVersionable> completeChildren = compConfig
						.fetchCompleteItems(new ArrayList<IVersionableHandle>(children.values()), monitor);

				loadDirectory(contentManager, compConfig, zos, dirPath, completeChildren, monitor);
			} else if (v instanceof IFileItem) {
				// Get the file contents and write them into the directory
				IFileItem file = (IFileItem) v;
				zos.putNextEntry(new ZipEntry(path + v.getName()));

				InputStream in = contentManager.retrieveContentStream(file, file.getContent(), monitor);
				byte[] arr = new byte[1024];
				int w;
				while (-1 != (w = in.read(arr))) {
					byte[] orr = process(arr);
					zos.write(orr, 0, w);
				}
				zos.closeEntry();
			}

		}
	}

	private byte[] process(byte[] arr) {
		if (fPreserve) {
			return arr;
		}
		if (OBFUSCATE_MODE.equals(fConfidentialityMode)) {
			return obfuscate(arr);
		}
		return randomize(arr);
	}

	private byte[] randomize(byte[] arr) {
		byte[] orr = new byte[arr.length];
		new Random().nextBytes(orr);
		return orr;
	}

	private byte[] obfuscate(byte[] arr) {
		byte[] orr = new byte[arr.length];
		System.arraycopy(LOREM_IPSUM.getBytes(), 0, orr, 0, arr.length);
		return orr;
	}

	/**
	 * Finds Streams or workspaces by name. If Name is null, finds all.
	 * 
	 * @param teamRepository
	 * @param name
	 * @param kind
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	private List<? extends IWorkspaceConnection> findConnectionByName(ITeamRepository teamRepository,
			String scmConnectionName, int kind, IProgressMonitor monitor) throws TeamRepositoryException {
		IWorkspaceManager wm = SCMPlatform.getWorkspaceManager(teamRepository);
		IWorkspaceSearchCriteria criteria = IWorkspaceSearchCriteria.FACTORY.newInstance().setKind(kind);
		if (scmConnectionName != null) {
			criteria.setExactName(scmConnectionName);
		}
		List<IWorkspaceHandle> connections = wm.findWorkspaces(criteria, Integer.MAX_VALUE, monitor);
		return wm.getWorkspaceConnections(connections, monitor);
	}

}
