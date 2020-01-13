/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.statistics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.scm.statistics.sizerange.RangeStats;
import com.ibm.js.team.supporttools.scm.utils.ComponentUtil;
import com.ibm.js.team.supporttools.scm.utils.ConnectionUtil;
import com.ibm.js.team.supporttools.scm.utils.FileInfo;
import com.ibm.team.filesystem.client.FileSystemCore;
import com.ibm.team.filesystem.client.IFileContentManager;
import com.ibm.team.filesystem.common.IFileItem;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.repository.common.UUID;
import com.ibm.team.scm.client.IConfiguration;
import com.ibm.team.scm.client.IWorkspaceConnection;
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
 * Analyzes the statistics for a workspace connection, its components and the
 * file size range statistics across all components.
 * 
 */
public class ConnectionAnalyzer {

	public static final Logger logger = LoggerFactory.getLogger(ConnectionAnalyzer.class);
	private int fProgress = 0;
	private ConnectionStats connectionStats = null;
	private RangeStats connectionRangeStats = null;
	private RangeStats multiConnectionRangeStats = null;
	// private boolean silent = false;
	private ITeamRepository teamRepository = null;
	private IProgressMonitor monitor = null;

	/**
	 * Just for the simple case to analyze a workspace connection.
	 * 
	 * @param teamRepository
	 * @param monitor
	 */
	public ConnectionAnalyzer(ITeamRepository teamRepository, IProgressMonitor monitor) {
		this(teamRepository, monitor, null);
	}

	/**
	 * To be able to collect range statistics information across multiple
	 * workspace connections, pass a range statistics object that collects the
	 * data across the connections.
	 * 
	 * @param teamRepository
	 * @param monitor
	 * @param multiConnection
	 */
	public ConnectionAnalyzer(ITeamRepository teamRepository, IProgressMonitor monitor, RangeStats multiConnection) {
		super();
		this.teamRepository = teamRepository;
		this.monitor = monitor;
		this.multiConnectionRangeStats = multiConnection;
	}

	/**
	 * Analyze a repository workspace. Collect data such as hierarchy depth,
	 * extensions, encoding, file, folder numbers, averages and sizing.
	 * 
	 * @param outputfolder
	 * @param scmConnectionName
	 * @return
	 * @throws TeamRepositoryException
	 * @throws IOException
	 */
	public boolean analyzeWorkspace(String scmConnectionName) throws TeamRepositoryException, IOException {
		boolean result = false;
		logger.info("Find and open workspace '{}'...", scmConnectionName);
		List<IWorkspaceHandle> connections = ConnectionUtil.findWorkspacesByName(teamRepository, scmConnectionName,
				IWorkspaceSearchCriteria.ALL, monitor);
		if (connections.size() < 1) {
			logger.error("Error: workspace '{}' not found.", scmConnectionName);
			return result;
		}
		if (connections.size() > 1) {
			logger.error("Error: workspace '{}' not unique.", scmConnectionName);
			return result;
		}
		List<? extends IWorkspaceConnection> connection = ConnectionUtil.getWorkspaceConnections(teamRepository,
				connections, monitor);
		IWorkspaceConnection workspace = connection.get(0);
		return analyzeWorkspace(workspace);
	}

	/**
	 * Analyze a workspace connection.
	 * 
	 * @param workspace
	 * @return
	 * @throws TeamRepositoryException
	 * @throws IOException
	 */
	public boolean analyzeWorkspace(IWorkspaceConnection workspace) throws TeamRepositoryException, IOException {
		connectionRangeStats = new RangeStats();
		connectionStats = new ConnectionStats(workspace.getName());
		logger.info("Analyze component hierarchy of '{}'...", workspace.getName());
		IComponentHierarchyResult hierarchy = workspace.getComponentHierarchy(new ArrayList<IComponentHandle>());
		analyzeComponentHierarchyDepth(hierarchy);
		logger.info("Anylaze components...");
		analyzeConnectionComponents(workspace, hierarchy);
		return true;
	}

	public ConnectionStats getConnectionStats() {
		return connectionStats;
	}

	public RangeStats getConnectionRangeStats() {
		return connectionRangeStats;
	}

	public RangeStats getMultiConnectionRangeStats() {
		return multiConnectionRangeStats;
	}

	/**
	 * Calculates the component hierarchy depth information beginning with the
	 * root elements down to the subcomponents.
	 * 
	 * @param hierarchy
	 */
	private void analyzeComponentHierarchyDepth(IComponentHierarchyResult hierarchy) {
		Map<UUID, Collection<IComponentHandle>> subcomponents = hierarchy.getParentToChildrenMap();
		Collection<IComponentHierarchyNode> roots = hierarchy.getRoots();
		for (IComponentHierarchyNode node : roots) {
			analyzeComponentHierarchyDepth(0, node.getComponentHandle(), subcomponents);
		}
	}

	/**
	 * Calculates the component hierarchy depth for the subcomponents.
	 * 
	 * @param depth
	 * @param componentHandle
	 * @param subcomponents
	 */
	private void analyzeComponentHierarchyDepth(int depth, IComponentHandle componentHandle,
			Map<UUID, Collection<IComponentHandle>> subcomponents) {
		ComponentStat comp = connectionStats.getNewComponent(componentHandle.getItemId());
		comp.setComponentHierarchyDepth(depth);
		logger.info("\t{} ", componentHandle.getItemId().toString());
		depth++;
		Collection<IComponentHandle> children = subcomponents.get(componentHandle.getItemId());
		for (IComponentHandle child : children) {
			analyzeComponentHierarchyDepth(depth, child, subcomponents);
		}
	}

	/**
	 * Analyze the content of each component in a component hierarchy.
	 * 
	 * @param workspace
	 * @param hierarchy
	 *            The component hierarchy containing all components to analyze.
	 * @throws TeamRepositoryException
	 * @throws IOException
	 */
	private void analyzeConnectionComponents(IWorkspaceConnection workspace, IComponentHierarchyResult hierarchy)
			throws TeamRepositoryException, IOException {
		Collection<IComponentHandle> componentHandles = hierarchy.getFlattenedElementsMap().values();
		List<IComponent> components = ComponentUtil.resolveComponents(teamRepository,
				new ArrayList<IComponentHandle>(componentHandles), monitor);
		for (IComponent component : components) {
			analyzeComponentConfigurationContent(workspace, component);
		}
	}

	/**
	 * Analyze the configuration content of a component, folders, files, etc.
	 * 
	 * @param connection
	 * @param component
	 * @throws TeamRepositoryException
	 * @throws IOException
	 */
	private void analyzeComponentConfigurationContent(IWorkspaceConnection connection, IComponent component)
			throws TeamRepositoryException, IOException {
		logger.info("\t{}", component.getName());
		System.out.print("\t");
		// Start walking the workspace contents
		connectionStats.getComponentStat(component.getItemId()).setComponentName(component.getName());
		try {
			IConfiguration compConfig = connection.configuration(component);
			IFileContentManager contentManager = FileSystemCore.getContentManager(teamRepository);
			analyzeComponentConfigurationContentRoot(contentManager, compConfig);
		} finally {
			System.out.println("");
		}
	}

	/**
	 * Get all versionables in the root of the components. This represents a
	 * root folder. analyze the files and folders it contains.
	 * 
	 * @param contentManager
	 * @param compConfig
	 * @throws TeamRepositoryException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	private void analyzeComponentConfigurationContentRoot(IFileContentManager contentManager, IConfiguration compConfig)
			throws TeamRepositoryException, IOException {

		Map<String, IVersionableHandle> handles = compConfig.childEntriesForRoot(monitor);
		List<IVersionable> items = compConfig.fetchCompleteItems(new ArrayList<IVersionableHandle>(handles.values()),
				monitor);
		analyzeFolder(contentManager, compConfig, items, "", 0);
	}

	/**
	 * Analyze a folder containing versionables. The versionables can be folders
	 * and files. Recreate the file path and file names based on the folder
	 * recursion. This is obsolete, as the information is never used.
	 * 
	 * @param contentManager
	 * @param compConfig
	 * @param items
	 * @param path
	 *            path relative to the root of the component.
	 * @param depth
	 * @throws IOException
	 * @throws TeamRepositoryException
	 */
	@SuppressWarnings("unchecked")
	private void analyzeFolder(IFileContentManager contentManager, IConfiguration compConfig, List<IVersionable> items,
			String path, int depth) throws IOException, TeamRepositoryException {
		long folders = 0;
		long files = 0;
		// depth++;
		ComponentStat compStat = connectionStats.getComponentStat(compConfig.component().getItemId());
		for (IVersionable v : items) {
			if (v instanceof IFolder) {
				folders++;
				// analyze the directory
				String dirPath = path + v.getName() + "/";
				compStat.addFolderStat((IFolder) v, depth, path);

				Map<String, IVersionableHandle> children = compConfig.childEntries((IFolderHandle) v, monitor);
				List<IVersionable> completeChildren = compConfig
						.fetchCompleteItems(new ArrayList<IVersionableHandle>(children.values()), monitor);
				// Recursion into the contained folders
				analyzeFolder(contentManager, compConfig, completeChildren, dirPath, depth + 1);
			} else if (v instanceof IFileItem) {
				// Analyze the file contained in this folder.
				IFileItem file = (IFileItem) v;
				FileInfo fInfo = FileInfo.getFileInfo(file);
				compStat.addFileStat(fInfo, depth);
				analyzeSizeRange(fInfo);
				files++;
			}
		}
		compStat.addFolderStats(folders, files, depth);
		showProgress();
	}

	private void analyzeSizeRange(FileInfo fInfo) {
		if (null != connectionRangeStats) {
			connectionRangeStats.analyze(fInfo);
		}
		if (null != multiConnectionRangeStats) {
			multiConnectionRangeStats.analyze(fInfo);
		}
	}

	/**
	 * This prints one '.' for every for 10 times it is called to show some
	 * progress. Can be used to show more fine grained progress.
	 */
	private void showProgress() {
		fProgress++;
		if (fProgress > 8) {
			System.out.print(".");
			fProgress = 0;
		}
	}
}
