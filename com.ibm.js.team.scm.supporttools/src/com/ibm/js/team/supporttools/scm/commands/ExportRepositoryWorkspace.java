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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import com.ibm.js.team.supporttools.scm.utils.ComponentUtil;
import com.ibm.team.filesystem.client.FileSystemCore;
import com.ibm.team.filesystem.client.IFileContentManager;
import com.ibm.team.filesystem.common.IFileItem;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.repository.common.UUID;
import com.ibm.team.repository.common.json.JSONArray;
import com.ibm.team.repository.common.json.JSONObject;
import com.ibm.team.scm.client.IConfiguration;
import com.ibm.team.scm.client.IWorkspaceConnection;
import com.ibm.team.scm.common.IComponent;
import com.ibm.team.scm.common.IComponentHandle;
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
	private Object fConfidentialityMode="randomize";
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

		List<IWorkspaceHandle> connections = ComponentUtil.findWorkspacesByName(teamRepository, scmConnection,
				IWorkspaceSearchCriteria.WORKSPACES, monitor);
		if (connections.size() < 1) {
			logger.error("Error: WorkspaceConnection '{}' not found.", scmConnection);
			return result;
		}
		if (connections.size() > 1) {
			logger.error("Error: WorkspaceConnection '{}' not unique.", scmConnection);
			return result;
		}
		List<? extends IWorkspaceConnection> connection = ComponentUtil.getWorkspaceConnections(teamRepository, connections, monitor);
		IWorkspaceConnection workspace = connection.get(0);
		IComponentHierarchyResult hierarchy = connection.get(0).getComponentHierarchy(new ArrayList<IComponentHandle>());
		writeHierarchy(teamRepository, hierarchy, monitor);
		logger.info("Packaging and Ramdomizing Components...");
		Collection<IComponentHandle> components = hierarchy.getFlattenedElementsMap().values();
		result = packageComponentHandles(teamRepository, fOutputFolder, workspace, components, monitor);			
		return result;
	}

	private void writeHierarchy(ITeamRepository teamRepository, IComponentHierarchyResult hierarchy,
			IProgressMonitor monitor) throws TeamRepositoryException, UnsupportedEncodingException, IOException {
		Map<UUID, Collection<IComponentHandle>> par2Child = hierarchy.getParentToChildrenMap();
		Map<UUID, IComponentHandle> flat = hierarchy.getFlattenedElementsMap();
		writeChildMap(teamRepository, flat, par2Child, monitor);	
	}


	private void writeChildMap(ITeamRepository teamRepository, Map<UUID, IComponentHandle> flat,
			Map<UUID, Collection<IComponentHandle>> par2Child, IProgressMonitor monitor) throws TeamRepositoryException, UnsupportedEncodingException, IOException {
		logger.info("Persist component hierarchy...");
		
		JSONArray jsonhierarchy = new JSONArray();

			Set<UUID> parents = par2Child.keySet();
			for (Iterator<UUID> iterator = parents.iterator(); iterator.hasNext();) {
				UUID parent = (UUID) iterator.next();
				IComponent parentComp = ComponentUtil.resolveComponent(teamRepository, flat.get(parent), monitor);
				JSONObject component = new JSONObject();
				logger.info("  Parent... '{}'" , parentComp.getName());
				component.put(ScmSupportToolsConstants.COMPONENT_NAME, parentComp.getName());
				component.put(ScmSupportToolsConstants.COMPONENT_UUID, parentComp.getItemId().getUuidValue());
				JSONArray jsonChildren = new JSONArray();
				Collection<IComponentHandle> children = par2Child.get(parent);
				for (Iterator<IComponentHandle> childIter = children.iterator(); childIter.hasNext();) {
					IComponentHandle handle = (IComponentHandle) childIter.next();
					IComponent child = ComponentUtil.resolveComponent(teamRepository, handle, monitor);
					JSONObject childComponent = new JSONObject();
					childComponent.put(ScmSupportToolsConstants.COMPONENT_NAME, child.getName());
					childComponent.put(ScmSupportToolsConstants.COMPONENT_UUID, child.getItemId().getUuidValue());
					jsonChildren.add(childComponent);
					component.put(ScmSupportToolsConstants.COMPONENT_CHILDREN, jsonChildren);
				}
				jsonhierarchy.add(component);
			}
			jsonhierarchy.serialize(new FileWriter(new File(fOutputFolder,ScmSupportToolsConstants.HIERARCHY_JSON_FILE)), true);
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

//	private boolean packageComponentHandles(ITeamRepository teamRepository, File outputfolder,
//			List<? extends IWorkspaceConnection> connection, Collection<IComponentHandle> components2, IProgressMonitor monitor)
//			throws IOException, TeamRepositoryException {
//		List<IComponent> components = resolveComponents(teamRepository, components2, monitor);
//
//		return packageComponents(teamRepository, outputfolder, connection, components, monitor);
//	}

	private boolean packageComponentHandles(ITeamRepository teamRepository, File outputFolder,
			IWorkspaceConnection connection, Collection<IComponentHandle> componentHandles, IProgressMonitor monitor) throws TeamRepositoryException, IOException {
		List<IComponent> components = ComponentUtil.resolveComponents(teamRepository, new ArrayList<IComponentHandle>(componentHandles), monitor);

		return packageComponents(teamRepository, outputFolder, connection, components, monitor);
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

//	private void writeRoots(ITeamRepository teamRepository, Collection<IComponentHierarchyNode> roots,
//			IProgressMonitor monitor) throws TeamRepositoryException, UnsupportedEncodingException, IOException {
//		logger.info("Roots...");
//		FileOutputStream out = new FileOutputStream(new File(fOutputFolder, "rootcomponents.txt"));
//		try {
//			for (IComponentHierarchyNode node : roots) {
//				IComponent comp = ComponentUtil.resolveComponent(teamRepository, node.getComponentHandle(), monitor);
//				logger.info("Root '{}' UUID '{}'", comp.getName(), comp.getItemId().getUuidValue());
//				String componentInfo = comp.getName() + " " + comp.getItemId().getUuidValue() + "\n";
//				out.write(componentInfo.getBytes("UTF-8"));
//			}
//			out.flush();
//		} finally {
//			out.close();
//		}
//	}
//
//	private void writeComponents(List<IComponent> components) throws UnsupportedEncodingException, IOException {
//		logger.info("Components...");
//		FileOutputStream out = new FileOutputStream(new File(fOutputFolder, "components.txt"));
//		try {
//			for (IComponent comp : components) {
//				logger.info("'{}' UUID '{}'", comp.getName(), comp.getItemId().getUuidValue());
//				String componentInfo = comp.getName() + " " + comp.getItemId().getUuidValue() + "\n";
//				out.write(componentInfo.getBytes("UTF-8"));
//			}
//			out.flush();
//		} finally {
//			out.close();
//		}
//	}

}
