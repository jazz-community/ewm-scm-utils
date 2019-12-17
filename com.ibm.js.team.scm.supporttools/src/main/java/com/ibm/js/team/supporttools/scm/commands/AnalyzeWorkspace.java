/*******************************************************************************
 * Copyright (c) 2015-2019 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.commands;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.SupportToolsFrameworkConstants;
import com.ibm.js.team.supporttools.framework.framework.AbstractTeamrepositoryCommand;
import com.ibm.js.team.supporttools.framework.framework.ICommand;
import com.ibm.js.team.supporttools.scm.ScmSupportToolsConstants;
import com.ibm.js.team.supporttools.scm.statistics.ComponentStat;
import com.ibm.js.team.supporttools.scm.statistics.ConnectionStat;
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
 * Allows to analyze a repository workspace, all its components and the current
 * SCM data.
 * 
 */
public class AnalyzeWorkspace extends AbstractTeamrepositoryCommand implements ICommand {

	public static final Logger logger = LoggerFactory.getLogger(AnalyzeWorkspace.class);
	private int fProgress = 0;
	private ConnectionStat connectionStat = null;
	private RangeStats rangeStats = new RangeStats();

	/**
	 * Constructor, set the command name which will be used as option value for
	 * the command option. The name is used in the UIs and the option parser.
	 */
	public AnalyzeWorkspace() {
		super(ScmSupportToolsConstants.CMD_ANYLYZEWORKSPACECONNECTION);
	}

	@Override
	public String getScenarioName() {
		return ScmSupportToolsConstants.EXPENSIVESCENARIO_SCMTOOLS + getCommandName();
	}

	/**
	 * Method to add the additional options this command requires.
	 */
	@Override
	public Options addTeamRepositoryCommandOptions(Options options) {
		options.addOption(ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID, true,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_DESCRIPTION);
		return options;
	}

	/**
	 * Method to check if the additional required options/parameters required to
	 * perform the command are available.
	 */
	@Override
	public boolean checkTeamreposiroyCommandParameters(CommandLine cmd) {
		// Check for required options
		boolean isValid = true;

		if (!(cmd.hasOption(ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID))) {
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
		logger.info(ScmSupportToolsConstants.CMD_ANALYSE_WORKSPACECONNECTION_DESCRIPTION);
		// General syntax
		logger.info("\n\tSyntax: -{} {} -{} {} -{} {} -{} {} -{} {} ", SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				getCommandName(), SupportToolsFrameworkConstants.PARAMETER_URL,
				SupportToolsFrameworkConstants.PARAMETER_URL_PROTOTYPE, SupportToolsFrameworkConstants.PARAMETER_USER,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_PROTOTYPE,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_PROTOTYPE);
		// Parameter and description
		logger.info(
				"\n\tParameter description: \n\t -{} \t {} \n\t -{} \t{} \n\t -{} \t {} \n\t -{} \t {} \n\t -{} \t {}",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				SupportToolsFrameworkConstants.PARAMETER_COMMAND_DESCRIPTION,
				SupportToolsFrameworkConstants.PARAMETER_URL, SupportToolsFrameworkConstants.PARAMETER_URL_DESCRIPTION,
				SupportToolsFrameworkConstants.PARAMETER_USER,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_DESCRIPTION,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_DESCRIPTION,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_DESCRIPTION);
		// Examples
		logger.info("\n\tExample: -{} {} -{} {} -{} {} -{} {} -{} {}", SupportToolsFrameworkConstants.PARAMETER_COMMAND,
				getCommandName(), SupportToolsFrameworkConstants.PARAMETER_URL,
				SupportToolsFrameworkConstants.PARAMETER_URL_EXAMPLE, SupportToolsFrameworkConstants.PARAMETER_USER,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_EXAMPLE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_EXAMPLE,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_EXAMPLE);
	}

	/**
	 * The main method that executes the behavior of this command.
	 * 
	 * @throws TeamRepositoryException
	 */
	@Override
	public boolean executeTeamRepositoryCommand() throws TeamRepositoryException {
		boolean result = false;
		String scmWorkspace = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID);
		try {
			return analyzeWorkspace(getTeamRepository(), scmWorkspace, getMonitor());
		} catch (IOException e) {
			logger.error("IOException: {}", e.getMessage());
		}
		return result;
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
	private boolean analyzeWorkspace(ITeamRepository teamRepository, String scmConnection, IProgressMonitor monitor)
			throws TeamRepositoryException, IOException {
		boolean result = false;
		connectionStat = new ConnectionStat(scmConnection);
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

		logger.info("Analyze component hierarchy from '{}'...", scmConnection);
		IComponentHierarchyResult hierarchy = workspace.getComponentHierarchy(new ArrayList<IComponentHandle>());
		analyzeComponentHierarchy(teamRepository, hierarchy, monitor);
		logger.info("Anylaze components...");
		analyzeComponentContent(teamRepository, monitor, workspace, hierarchy);
		logger.info("Show results...");
		connectionStat.log();
		rangeStats.generateWorkBook();
		return true;
	}

	/**
	 * @param teamRepository
	 * @param monitor
	 * @param workspace
	 * @param hierarchy
	 * @throws TeamRepositoryException
	 * @throws IOException
	 */
	private void analyzeComponentContent(ITeamRepository teamRepository, IProgressMonitor monitor,
			IWorkspaceConnection workspace, IComponentHierarchyResult hierarchy)
			throws TeamRepositoryException, IOException {
		Collection<IComponentHandle> componentHandles = hierarchy.getFlattenedElementsMap().values();
		List<IComponent> components = ComponentUtil.resolveComponents(teamRepository,
				new ArrayList<IComponentHandle>(componentHandles), monitor);
		for (IComponent component : components) {
			analyzeComponent(teamRepository, workspace, component, monitor);
		}
	}

	/**
	 * @param teamRepository
	 * @param connection
	 * @param component
	 * @param monitor
	 * @throws TeamRepositoryException
	 * @throws IOException
	 */
	private void analyzeComponent(ITeamRepository teamRepository, IWorkspaceConnection connection, IComponent component,
			IProgressMonitor monitor) throws TeamRepositoryException, IOException {
		logger.info("\t{}", component.getName());
		System.out.print("\t");
		// Start walking the workspace contents
		IFileContentManager contentManager = FileSystemCore.getContentManager(teamRepository);
		connectionStat.getComponentStat(component.getItemId()).setComponentName(component.getName());
		try {
			IConfiguration compConfig = connection.configuration(component);
			// Fetch the items at the root of each component. We do this to
			// initialize our
			// queue of stuff to download.
			analyzeComponentRoot(contentManager, compConfig, monitor);

		} finally {
			System.out.println("");
		}
	}

	@SuppressWarnings("unchecked")
	private void analyzeComponentRoot(IFileContentManager contentManager, IConfiguration compConfig,
			IProgressMonitor monitor) throws TeamRepositoryException, IOException {

		Map<String, IVersionableHandle> handles = compConfig.childEntriesForRoot(monitor);
		List<IVersionable> items = compConfig.fetchCompleteItems(new ArrayList<IVersionableHandle>(handles.values()),
				monitor);
		// ArrayList<Long> breadthInfo = new ArrayList<Long>();
		// Recursion analyze all items in the root
		analyzeFolder(contentManager, compConfig, items, "", 0, monitor);
	}

	/**
	 * @param contentManager
	 * @param compConfig
	 * @param items
	 * @param path
	 * @param depth
	 * @param breadthInfo
	 * @param monitor
	 * @param zos
	 * @throws IOException
	 * @throws TeamRepositoryException
	 */
	@SuppressWarnings("unchecked")
	private void analyzeFolder(IFileContentManager contentManager, IConfiguration compConfig, List<IVersionable> items,
			String path, int depth, IProgressMonitor monitor) throws IOException, TeamRepositoryException {
		long folders = 0;
		long files = 0;
		// depth++;
		ComponentStat compStat = connectionStat.getComponentStat(compConfig.component().getItemId());
		for (IVersionable v : items) {
			if (v instanceof IFolder) {
				folders++;
				// Write the directory
				String dirPath = path + v.getName() + "/";
				compStat.addFolderStat((IFolder) v, depth, path);

				Map<String, IVersionableHandle> children = compConfig.childEntries((IFolderHandle) v, monitor);
				List<IVersionable> completeChildren = compConfig
						.fetchCompleteItems(new ArrayList<IVersionableHandle>(children.values()), monitor);
				// Recursion into the contained folders
				analyzeFolder(contentManager, compConfig, completeChildren, dirPath, depth + 1, monitor);
			} else if (v instanceof IFileItem) {
				// Get the file contents. Generate contents to save them into
				// the directory
				IFileItem file = (IFileItem) v;
				FileInfo fInfo = FileInfo.getFileInfo(file);
				compStat.addFileStat(fInfo, depth);
				rangeStats.analyze(fInfo);
				files++;
			}
		}
		compStat.addFolderStats(folders, files, depth);
		showProgress();
	}

	/**
	 * @param teamRepository
	 * @param hierarchy
	 * @param monitor
	 */
	private void analyzeComponentHierarchy(ITeamRepository teamRepository, IComponentHierarchyResult hierarchy,
			IProgressMonitor monitor) {
		Map<UUID, Collection<IComponentHandle>> par2Child = hierarchy.getParentToChildrenMap();
		Collection<IComponentHierarchyNode> roots = hierarchy.getRoots();
		for (IComponentHierarchyNode node : roots) {
			analyzeComponent(0, node.getComponentHandle(), par2Child);
		}
	}

	/**
	 * @param depth
	 * @param componentHandle
	 * @param par2Child
	 */
	private void analyzeComponent(int depth, IComponentHandle componentHandle,
			Map<UUID, Collection<IComponentHandle>> par2Child) {
		ComponentStat comp = connectionStat.getNewComponent(componentHandle.getItemId());
		comp.setComponentHierarchyDepth(depth);
		logger.info("\t{} ", componentHandle.getItemId().toString());
		depth++;
		Collection<IComponentHandle> children = par2Child.get(componentHandle.getItemId());
		for (IComponentHandle child : children) {
			analyzeComponent(depth, child, par2Child);
		}
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
