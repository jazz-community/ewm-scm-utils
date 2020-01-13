/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.SupportToolsFrameworkConstants;
import com.ibm.js.team.supporttools.framework.framework.AbstractTeamrepositoryCommand;
import com.ibm.js.team.supporttools.framework.framework.ICommand;
import com.ibm.js.team.supporttools.framework.util.FileUtil;
import com.ibm.js.team.supporttools.scm.ScmSupportToolsConstants;
import com.ibm.js.team.supporttools.scm.utils.ComponentUtil;
import com.ibm.js.team.supporttools.scm.utils.ConnectionUtil;
import com.ibm.js.team.supporttools.scm.utils.FileContentUtil;
import com.ibm.team.filesystem.client.FileSystemCore;
import com.ibm.team.filesystem.client.IFileContentManager;
import com.ibm.team.filesystem.common.FileLineDelimiter;
import com.ibm.team.filesystem.common.IFileContent;
import com.ibm.team.filesystem.common.IFileItem;
import com.ibm.team.repository.client.ITeamRepository;
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
 * Allows to export a repository workspace, all its components and the current
 * SCM data into a persistent format. The persistent format can later be used to
 * import and recreate a repository workspace and the component and the latest
 * content.
 * 
 */
public class ExportWorkspace extends AbstractTeamrepositoryCommand implements ICommand {

	/**
	 * The supported modes to export the data
	 *
	 */
	enum ExportMode {
		RANDOMIZE, OBFUSCATE, PRESERVE
	}

	public static final Logger logger = LoggerFactory.getLogger(ExportWorkspace.class);
	public ExportMode fExportMode = ExportMode.RANDOMIZE;
	private File fOutputFolder = null;
	private int fProgress = 0;
	private FileContentUtil fFileUtil = null;

	/**
	 * Constructor, set the command name which will be used as option value for
	 * the command option. The name is used in the UIs and the option parser.
	 */
	public ExportWorkspace() {
		super(ScmSupportToolsConstants.CMD_EXPORT_WORKSPACE);
	}

	/**
	 * Method to add the options this command requires.
	 */
	@Override
	public Options addTeamRepositoryCommandOptions(Options options) {
		options.addOption(ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID, true,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_DESCRIPTION);
		options.addOption(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER, true,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_DESCRIPTION);
		options.addOption(ScmSupportToolsConstants.PARAMETER_EXPORT_MODE, true,
				ScmSupportToolsConstants.PARAMETER_EXPORT_MODE_DESCRIPTION);
		return options;
	}

	@Override
	public boolean checkTeamreposiroyCommandParameters(CommandLine cmd) {
		// Check for required options
		boolean isValid = true;

		if (!(cmd.hasOption(ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID)
				&& cmd.hasOption(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER))) {
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
		logger.info(ScmSupportToolsConstants.CMD_EXPORT_WORKSPACE_DESCRIPTION);
		// General syntax
		logger.info("\n\tSyntax: -{} {} -{} {} -{} {} -{} {} -{} {} -{} {}",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND, getCommandName(),
				SupportToolsFrameworkConstants.PARAMETER_URL, SupportToolsFrameworkConstants.PARAMETER_URL_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_USER,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_PROTOTYPE,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_PROTOTYPE, ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_PROTOTYPE);
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
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_DESCRIPTION);
		// Optional parameters
		logger.info("\n\tOptional parameter syntax: -{} {}", ScmSupportToolsConstants.PARAMETER_EXPORT_MODE,
				ScmSupportToolsConstants.PARAMETER_EXPORT_MODE_PROTOTYPE);
		// Optional parameters description
		logger.info("\n\tOptional parameter description: \n\t -{} \t {}",
				ScmSupportToolsConstants.PARAMETER_EXPORT_MODE,
				ScmSupportToolsConstants.PARAMETER_EXPORT_MODE_DESCRIPTION);
		// Examples
		logger.info("\n\tExample: -{} {} -{} {} -{} {} -{} {} -{} {} -{} {}",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND, getCommandName(),
				SupportToolsFrameworkConstants.PARAMETER_URL, SupportToolsFrameworkConstants.PARAMETER_URL_EXAMPLE,
				SupportToolsFrameworkConstants.PARAMETER_USER, SupportToolsFrameworkConstants.PARAMETER_USER_ID_EXAMPLE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_EXAMPLE,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_EXAMPLE, ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER,
				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_EXAMPLE);
		// Optional parameter examples
		logger.info("\n\tExample optional parameter: -{} {}", ScmSupportToolsConstants.PARAMETER_EXPORT_MODE,
				ScmSupportToolsConstants.PARAMETER_EXPORT_MODE_EXAMPLE);
	}

	@Override
	public boolean executeTeamRepositoryCommand() throws TeamRepositoryException {
		boolean result = false;
		// Execute the code
		// Get all the option values
		String scmWorkspace = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID);
		String outputFolderPath = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER);
		String exportMode = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_EXPORT_MODE);

		try {
			File outputfolder = new File(outputFolderPath);
			if (!outputfolder.exists()) {
				FileUtil.createFolderWithParents(outputfolder);
				if (!outputfolder.exists()) {
					logger.error("Error: Outputfolder '{}' could not be created.", outputFolderPath);
					return result;
				}
			}
			if (!outputfolder.isDirectory()) {
				logger.error("Error: '{}' is not a directory.", outputFolderPath);
				return result;
			}
			fOutputFolder = outputfolder;
			setExportMode(exportMode);
			result = exportWorkspace(getTeamRepository(), scmWorkspace, getMonitor());
		} catch (IOException e) {
			logger.error("IOException: {}", e.getMessage());
		}
		return result;
	}

	/**
	 * Managing the export mode based on a parameter.
	 * 
	 * @param exportMode
	 */
	private void setExportMode(String exportMode) {
		if (exportMode == null) {
			return;
		}
		if (ScmSupportToolsConstants.EXPORT_MODE_RANDOMIZE.equals(exportMode)) {
			fExportMode = ExportMode.RANDOMIZE;
			return;
		}
		if (ScmSupportToolsConstants.EXPORT_MODE_PRESERVE.equals(exportMode)) {
			fExportMode = ExportMode.PRESERVE;
			return;
		}
		if (ScmSupportToolsConstants.EXPORT_MODE_OBFUSCATE.equals(exportMode)) {
			fExportMode = ExportMode.OBFUSCATE;
			return;
		}

	}

	/**
	 * Export a repository workspace, all its components and the current SCM
	 * data into a persistent format. The persistent format can later be used to
	 * import and recreate a repository workspace and the component and the
	 * latest content.
	 * 
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

		logger.info("Find and open workspace '{}'...", scmConnection);
		List<IWorkspaceHandle> connections = ConnectionUtil.findWorkspacesByName(teamRepository, scmConnection,
				IWorkspaceSearchCriteria.ALL, monitor);
		if (connections.size() < 1) {
			logger.error("Error: workspace '{}' not found.", scmConnection);
			return result;
		}
		if (connections.size() > 1) {
			logger.error("Error: workspace '{}' not unique.", scmConnection);
			return result;
		}
		List<? extends IWorkspaceConnection> connection = ConnectionUtil.getWorkspaceConnections(teamRepository,
				connections, monitor);
		IWorkspaceConnection workspace = connection.get(0);

		logger.info("Analyze and store component hierarchy for '{}'...", scmConnection);
		IComponentHierarchyResult hierarchy = workspace.getComponentHierarchy(new ArrayList<IComponentHandle>());
		writeHierarchy(teamRepository, hierarchy, monitor);
		logger.info("Package and Ramdomize Components...");
		Collection<IComponentHandle> components = hierarchy.getFlattenedElementsMap().values();
		result = packageComponentHandles(teamRepository, fOutputFolder, workspace, components, monitor);
		return result;
	}

	/**
	 * Persist the component and component hierarchy information into a JSON
	 * file.
	 * 
	 * @param teamRepository
	 * @param hierarchy
	 * @param monitor
	 * @throws TeamRepositoryException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private void writeHierarchy(ITeamRepository teamRepository, IComponentHierarchyResult hierarchy,
			IProgressMonitor monitor) throws TeamRepositoryException, UnsupportedEncodingException, IOException {
		Map<UUID, Collection<IComponentHandle>> par2Child = hierarchy.getParentToChildrenMap();
		Map<UUID, IComponentHandle> flat = hierarchy.getFlattenedElementsMap();
		writeChildMap(teamRepository, flat, par2Child, monitor);
	}

	/**
	 * Create a JSON file containing all components of the repository workspace.
	 * Each component also has a list of child components.
	 * 
	 * @param teamRepository
	 * @param flat
	 * @param par2Child
	 * @param monitor
	 * @throws TeamRepositoryException
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private void writeChildMap(ITeamRepository teamRepository, final Map<UUID, IComponentHandle> flat,
			final Map<UUID, Collection<IComponentHandle>> par2Child, IProgressMonitor monitor)
			throws TeamRepositoryException, UnsupportedEncodingException, IOException {
		File jsonFile = new File(fOutputFolder, ScmSupportToolsConstants.HIERARCHY_JSON_FILE);
		logger.info("Persist component hierarchy in '{}'...", jsonFile.getAbsolutePath());

		JSONArray jsonhierarchy = new JSONArray();

		Set<UUID> parents = par2Child.keySet();
		for (Iterator<UUID> iterator = parents.iterator(); iterator.hasNext();) {
			UUID parent = (UUID) iterator.next();
			IComponent parentComp = ComponentUtil.resolveComponent(teamRepository, flat.get(parent), monitor);
			JSONObject component = new JSONObject();
			logger.info("\tComponent... '{}'", parentComp.getName());
			component.put(ScmSupportToolsConstants.JSON_COMPONENT_NAME, parentComp.getName());
			component.put(ScmSupportToolsConstants.JSON_COMPONENT_UUID, parentComp.getItemId().getUuidValue());
			JSONArray jsonChildren = new JSONArray();
			Collection<IComponentHandle> children = par2Child.get(parent);
			for (Iterator<IComponentHandle> childIter = children.iterator(); childIter.hasNext();) {
				IComponentHandle handle = (IComponentHandle) childIter.next();
				IComponent child = ComponentUtil.resolveComponent(teamRepository, handle, monitor);
				JSONObject childComponent = new JSONObject();
				childComponent.put(ScmSupportToolsConstants.JSON_COMPONENT_NAME, child.getName());
				childComponent.put(ScmSupportToolsConstants.JSON_COMPONENT_UUID, child.getItemId().getUuidValue());
				jsonChildren.add(childComponent);
				component.put(ScmSupportToolsConstants.JSON_COMPONENT_CHILDREN, jsonChildren);
			}
			jsonhierarchy.add(component);
		}
		jsonhierarchy.serialize(new FileWriter(jsonFile), true);
	}

	/**
	 * Iterate all components. For each component package the folder and file
	 * structure into a zip file.
	 * 
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

		int currentComponent = 1;
		int noOfComponents = components.size();
		for (IComponent component : components) {
			logger.info("\tPacking {} of {} components: '{}' '{}'", currentComponent++, noOfComponents,
					component.getName(), component.getItemId().getUuidValue());
			result &= packageComponent(teamRepository, connection, component, monitor);
		}
		logger.info("Packing components finished...");
		return result;
	}

	/**
	 * Package the components from component handles.
	 * 
	 * @param teamRepository
	 * @param outputFolder
	 * @param connection
	 * @param componentHandles
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 * @throws IOException
	 */
	private boolean packageComponentHandles(ITeamRepository teamRepository, File outputFolder,
			IWorkspaceConnection connection, Collection<IComponentHandle> componentHandles, IProgressMonitor monitor)
			throws TeamRepositoryException, IOException {
		List<IComponent> components = ComponentUtil.resolveComponents(teamRepository,
				new ArrayList<IComponentHandle>(componentHandles), monitor);

		return packageComponents(teamRepository, outputFolder, connection, components, monitor);
	}

	/**
	 * Package the component file and folder structure.
	 * 
	 * @param teamRepository
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
			// Fetch the items at the root of each component. We do this to
			// initialize our
			// queue of stuff to download.
			@SuppressWarnings("unchecked")
			Map<String, IVersionableHandle> handles = compConfig.childEntriesForRoot(monitor);
			@SuppressWarnings("unchecked")
			List<IVersionable> items = compConfig
					.fetchCompleteItems(new ArrayList<IVersionableHandle>(handles.values()), monitor);

			// Recursion into each folder in the root
			loadDirectory(contentManager, compConfig, zos, "", items, monitor);

			zos.close();
			result = true;
		} finally {
			out.close();
			System.out.println("");
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

				// Recursion into the contained folders
				loadDirectory(contentManager, compConfig, zos, dirPath, completeChildren, monitor);

			} else if (v instanceof IFileItem) {
				// Get the file contents. Generate contents to save them into
				// the directory
				IFileItem file = (IFileItem) v;
				zos.putNextEntry(new ZipEntry(path + v.getName()));
				generateContent(file, contentManager, zos, monitor);
				zos.closeEntry();
			}
			showProgress();
		}
	}

	/**
	 * This generates the content to be persisted. There are three different
	 * options available that can be used.
	 * 
	 * For all options: the file and folder names are kept.
	 * 
	 * Randomize (default): Generates data based on random values. The size of
	 * the data a generated is the same as the original data. The generated data
	 * does not keep line ending and file encoding.
	 * 
	 * Obfuscate: Generates data based on sample code available. The size of the
	 * data a generated is similar or the same as the original data. The data is
	 * generated from example code snippets from a file. The generated data
	 * keeps line ending and file encoding, where available.
	 * 
	 * Preserve: Stores the original file and folder content unchanged.
	 * 
	 * @param file
	 * @param contentManager
	 * @param zos
	 * @param monitor
	 * @throws TeamRepositoryException
	 * @throws IOException
	 */
	private void generateContent(IFileItem file, IFileContentManager contentManager, ZipOutputStream zos,
			IProgressMonitor monitor) throws TeamRepositoryException, IOException {
		FileLineDelimiter lineDelimiter = FileLineDelimiter.LINE_DELIMITER_NONE;
		String encoding = null;
		IFileContent filecontent = file.getContent();
		if (filecontent != null) {
			encoding = filecontent.getCharacterEncoding();
			lineDelimiter = filecontent.getLineDelimiter();
		}
		logger.trace(" Filename: '{}' encoding: '{}' delimiter: '{}' content type: '{}'", file.getName(), encoding,
				lineDelimiter.toString(), file.getContentType());

		InputStream in = contentManager.retrieveContentStream(file, filecontent, monitor);
		switch (fExportMode) {
		case OBFUSCATE:
			getFileContentUtil().obfuscateSource(in, zos, lineDelimiter, encoding);
			break;
		case PRESERVE:
			getFileContentUtil().copyInput(in, zos);
			break;
		case RANDOMIZE:
			getFileContentUtil().randomizeBinary(in, zos);
			break;
		default:
			getFileContentUtil().randomizeBinary(in, zos);
			break;
		}
	}

	/**
	 * Instantiate the FileContentUtil that performs the data conversion.
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	FileContentUtil getFileContentUtil() throws UnsupportedEncodingException, FileNotFoundException, IOException {
		if (null == fFileUtil) {
			return new FileContentUtil();
		}
		return fFileUtil;
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
