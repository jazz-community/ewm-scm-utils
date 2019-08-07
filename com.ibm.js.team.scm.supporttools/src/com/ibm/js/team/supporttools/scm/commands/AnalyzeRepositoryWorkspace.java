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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.framework.SupportToolsFrameworkConstants;
import com.ibm.js.team.supporttools.framework.framework.AbstractCommand;
import com.ibm.js.team.supporttools.framework.framework.ICommand;
import com.ibm.js.team.supporttools.framework.util.FileUtil;
import com.ibm.js.team.supporttools.scm.ScmSupportToolsConstants;
import com.ibm.js.team.supporttools.scm.statistics.ComponentStat;
import com.ibm.js.team.supporttools.scm.statistics.ConnectionStat;
import com.ibm.js.team.supporttools.scm.utils.ComponentUtil;
import com.ibm.team.filesystem.client.FileSystemCore;
import com.ibm.team.filesystem.client.IFileContentManager;
import com.ibm.team.filesystem.common.IFileItem;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.client.TeamPlatform;
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
public class AnalyzeRepositoryWorkspace extends AbstractCommand implements ICommand {

	public static final Logger logger = LoggerFactory.getLogger(AnalyzeRepositoryWorkspace.class);
//	private File fOutputFolder = null;
	private int fProgress = 0;
	private ConnectionStat connectionStat = null;

	/**
	 * Constructor, set the command name which will be used as option value for the
	 * command option. The name is used in the UIs and the option parser.
	 */
	public AnalyzeRepositoryWorkspace() {
		super(ScmSupportToolsConstants.CMD_ANYLYZEWORKSPACE);
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
//		options.addOption(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER, true,
//				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_DESCRIPTION);
//		options.addOption(ScmSupportToolsConstants.PARAMETER_EXPORT_MODE, true,
//				ScmSupportToolsConstants.PARAMETER_EXPORT_MODE_DESCRIPTION);
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
				//&& cmd.hasOption(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER)
				)) {
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
		logger.info(ScmSupportToolsConstants.CMD_EXPORTWORKSPACE_DESCRIPTION);
		// General syntax
		logger.info("\n\tSyntax: -{} {} -{} {} -{} {} -{} {} -{} {} -{} {}",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND, getCommandName(),
				SupportToolsFrameworkConstants.PARAMETER_URL, SupportToolsFrameworkConstants.PARAMETER_URL_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_USER,
				SupportToolsFrameworkConstants.PARAMETER_USER_ID_PROTOTYPE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_PROTOTYPE,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_PROTOTYPE 
//				,ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER,
//				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_PROTOTYPE
				);
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
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_DESCRIPTION
//				,ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER,
//				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_DESCRIPTION
				);
		// Optional parameters
//		logger.info("\n\tOptional parameter syntax: -{} {}", ScmSupportToolsConstants.PARAMETER_EXPORT_MODE,
//				ScmSupportToolsConstants.PARAMETER_EXPORT_MODE_PROTOTYPE);
		// Optional parameters description
//		logger.info("\n\tOptional parameter description: \n\t -{} \t {}",
//				ScmSupportToolsConstants.PARAMETER_EXPORT_MODE,
//				ScmSupportToolsConstants.PARAMETER_EXPORT_MODE_DESCRIPTION);
		// Examples
		logger.info("\n\tExample: -{} {} -{} {} -{} {} -{} {} -{} {} -{} {}",
				SupportToolsFrameworkConstants.PARAMETER_COMMAND, getCommandName(),
				SupportToolsFrameworkConstants.PARAMETER_URL, SupportToolsFrameworkConstants.PARAMETER_URL_EXAMPLE,
				SupportToolsFrameworkConstants.PARAMETER_USER, SupportToolsFrameworkConstants.PARAMETER_USER_ID_EXAMPLE,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD,
				SupportToolsFrameworkConstants.PARAMETER_PASSWORD_EXAMPLE,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID,
				ScmSupportToolsConstants.PARAMETER_WORKSPACE_EXAMPLE 
//				,ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER,
//				ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER_EXAMPLE
				);
		// Optional parameter examples
		logger.info("\n\tExample optional parameter: -{} {}", ScmSupportToolsConstants.PARAMETER_EXPORT_MODE,
				ScmSupportToolsConstants.PARAMETER_EXPORT_MODE_EXAMPLE);
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
		String repositoryURI = getCmd().getOptionValue(SupportToolsFrameworkConstants.PARAMETER_URL);
		final String userId = getCmd().getOptionValue(SupportToolsFrameworkConstants.PARAMETER_USER);
		final String userPassword = getCmd().getOptionValue(SupportToolsFrameworkConstants.PARAMETER_PASSWORD);
		String scmWorkspace = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_WORKSPACE_NAME_OR_ID);
//		String outputFolderPath = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_OUTPUTFOLDER);
//		String exportMode = getCmd().getOptionValue(ScmSupportToolsConstants.PARAMETER_EXPORT_MODE);

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
//			File outputfolder = new File(outputFolderPath);
//			if (!outputfolder.exists()) {
//				FileUtil.createFolderWithParents(outputfolder);
//				if (!outputfolder.exists()) {
//					logger.error("Error: Outputfolder '{}' could not be created.", outputFolderPath);
//					return result;
//				}
//			}
//			if (!outputfolder.isDirectory()) {
//				logger.error("Error: '{}' is not a directory.", outputFolderPath);
//				return result;
//			}
//			fOutputFolder = outputfolder;
//			setExportMode(exportMode);
			result = analyzeWorkspace(teamRepository, scmWorkspace, monitor);
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
	 * Export a repository workspace, all its components and the current SCM data
	 * into a persistent format. The persistent format can later be used to import
	 * and recreate a repository workspace and the component and the latest content.
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
		logger.info("Find and open WorkspaceConnection '{}'...", scmConnection);
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
		List<? extends IWorkspaceConnection> connection = ComponentUtil.getWorkspaceConnections(teamRepository,
				connections, monitor);
		IWorkspaceConnection workspace = connection.get(0);

		logger.info("Analyze component hierarchy from '{}'...", scmConnection);
		IComponentHierarchyResult hierarchy = workspace.getComponentHierarchy(new ArrayList<IComponentHandle>());
		analyzeComponentHierarchy(teamRepository, hierarchy, monitor);
		logger.info("Anylaze components...");
		analyzeComponentContent(teamRepository, monitor, workspace, hierarchy);
		logger.info("Show results...");
		connectionStat.log();
		return true;
	}

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

	private void analyzeComponent(ITeamRepository teamRepository, IWorkspaceConnection connection, IComponent component,
			IProgressMonitor monitor) throws TeamRepositoryException, IOException {
		logger.info("\t{}", component.getName());
		System.out.print("\t");
		// Start walking the workspace contents
		IFileContentManager contentManager = FileSystemCore.getContentManager(teamRepository);
		connectionStat.getComponentStat(component.getItemId()).setComponentName(component.getName());
		try {
			IConfiguration compConfig = connection.configuration(component);
			// Fetch the items at the root of each component. We do this to initialize our
			// queue of stuff to download.
			@SuppressWarnings("unchecked")
			Map<String, IVersionableHandle> handles = compConfig.childEntriesForRoot(monitor);
			@SuppressWarnings("unchecked")
			List<IVersionable> items = compConfig
					.fetchCompleteItems(new ArrayList<IVersionableHandle>(handles.values()), monitor);

			// Recursion into each folder in the root
			analyzeFolder(0, contentManager, compConfig, "", items, monitor);

		} finally {
			System.out.println("");
		}

	}

	/**
	 * @param depth
	 * @param contentManager
	 * @param compConfig
	 * @param zos
	 * @param path
	 * @param items
	 * @param monitor
	 * @throws IOException
	 * @throws TeamRepositoryException
	 */
	private void analyzeFolder(int depth, IFileContentManager contentManager, IConfiguration compConfig, String path,
			List<IVersionable> items, IProgressMonitor monitor) throws IOException, TeamRepositoryException {
		depth++;
		long folders=0;
		long files=0;
		ComponentStat compStat = connectionStat.getComponentStat(compConfig.component().getItemId());
		for (IVersionable v : items) {
			if (v instanceof IFolder) {
				folders++;
				// Write the directory
				String dirPath = path + v.getName() + "/";
				compStat.addFolderStat((IFolder) v, depth, path);
				@SuppressWarnings("unchecked")
				Map<String, IVersionableHandle> children = compConfig.childEntries((IFolderHandle) v, monitor);
				@SuppressWarnings("unchecked")
				List<IVersionable> completeChildren = compConfig
						.fetchCompleteItems(new ArrayList<IVersionableHandle>(children.values()), monitor);
				// Recursion into the contained folders
				analyzeFolder(depth, contentManager, compConfig, dirPath, completeChildren, monitor);

			} else if (v instanceof IFileItem) {
				// Get the file contents. Generate contents to save them into the directory
				IFileItem file = (IFileItem) v;
				compStat.addFileStat(file, depth);
				files++;
			}
		}
		compStat.addFolderStats(folders,files, depth);
		showProgress();
	}

	private void analyzeComponentHierarchy(ITeamRepository teamRepository, IComponentHierarchyResult hierarchy,
			IProgressMonitor monitor) {
		Map<UUID, Collection<IComponentHandle>> par2Child = hierarchy.getParentToChildrenMap();
		Collection<IComponentHierarchyNode> roots = hierarchy.getRoots();
		for (IComponentHierarchyNode node : roots) {
			analyzeComponent(0, node.getComponentHandle(), par2Child);
		}

	}

	private void analyzeComponent(int depth, IComponentHandle componentHandle,
			Map<UUID, Collection<IComponentHandle>> par2Child) {
		depth++;
		ComponentStat comp = connectionStat.getNewComponent(componentHandle.getItemId());
		comp.setComponentHierarchyDepth(depth);
		logger.info("\t{} ", componentHandle.getItemId().toString());
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
