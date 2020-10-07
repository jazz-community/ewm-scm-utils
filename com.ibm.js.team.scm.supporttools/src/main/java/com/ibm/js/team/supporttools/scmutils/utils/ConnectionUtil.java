/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scmutils.utils;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.client.IWorkspaceConnection;
import com.ibm.team.scm.client.IWorkspaceManager;
import com.ibm.team.scm.client.SCMPlatform;
import com.ibm.team.scm.common.IComponentHandle;
import com.ibm.team.scm.common.IHistoryReference;
import com.ibm.team.scm.common.IWorkspaceHandle;
import com.ibm.team.scm.common.dto.IWorkspaceSearchCriteria;

/**
 * Utility Class to provide connection operations.
 *
 */
public class ConnectionUtil {

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
	public static List<IWorkspaceHandle> findWorkspacesByName(ITeamRepository teamRepository, String scmConnectionName,
			int kind, IProgressMonitor monitor) throws TeamRepositoryException {
		IWorkspaceManager wm = SCMPlatform.getWorkspaceManager(teamRepository);
		IWorkspaceSearchCriteria criteria = IWorkspaceSearchCriteria.FACTORY.newInstance().setKind(kind);
		if (scmConnectionName != null) {
			criteria.setExactName(scmConnectionName);
		}
		List<IWorkspaceHandle> connections = wm.findWorkspaces(criteria, Integer.MAX_VALUE, monitor);
		return connections;
	}

	/**
	 * Get all components from a workspace connection and resolve them.
	 * 
	 * @param teamRepository
	 * @param connections
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static List<? extends IWorkspaceConnection> getWorkspaceConnections(ITeamRepository teamRepository,
			List<? extends IWorkspaceHandle> connections, IProgressMonitor monitor) throws TeamRepositoryException {
		IWorkspaceManager wm = SCMPlatform.getWorkspaceManager(teamRepository);
		return wm.getWorkspaceConnections(connections, monitor);
	}

	/**
	 * Removes all components from a workspace connection
	 * 
	 * @param workspaceConnection
	 * @param monitor
	 * @throws TeamRepositoryException
	 */
	@SuppressWarnings("rawtypes")
	public static void removeAllComponentsFormWorkspaceConnection(IWorkspaceConnection workspaceConnection,
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
	 * Adds a component to a workspace connection
	 * 
	 * @param workspaceConnection
	 * @param componentHandle
	 * @param monitor
	 * @throws TeamRepositoryException
	 */
	public static void addComponentToWorkspaceConnection(IWorkspaceConnection workspaceConnection,
			IComponentHandle componentHandle, IProgressMonitor monitor) throws TeamRepositoryException {

		workspaceConnection.applyComponentOperations(Collections.singletonList(
				workspaceConnection.componentOpFactory().addComponent(componentHandle, false)), true, monitor);
	}

	/**
	 * Adds a component to a workspace connection base the component on the
	 * configuration of a stream.
	 * 
	 * @param workspaceConnection
	 * @param componentHandle
	 * @param seed
	 * @param monitor
	 * @throws TeamRepositoryException
	 */
	public static void addComponentFromSeedToWorkspaceConnection(IWorkspaceConnection workspaceConnection,
			IComponentHandle componentHandle, IWorkspaceConnection seed, IProgressMonitor monitor)
			throws TeamRepositoryException {

		workspaceConnection.applyComponentOperations(
				Collections.singletonList(
						workspaceConnection.componentOpFactory().addComponent(componentHandle, seed, false)),
				true, monitor);
	}

	public static void addComponentFromSeedToWorkspaceConnection(IWorkspaceConnection workspaceConnection,
			IComponentHandle componentHandle, IHistoryReference history, IProgressMonitor monitor)
			throws TeamRepositoryException {

		workspaceConnection.applyComponentOperations(
				Collections.singletonList(
						workspaceConnection.componentOpFactory().addComponent(componentHandle, history, false)),
				true, monitor);
	}

	/**
	 * Test if a component is in a connection.
	 * 
	 * @param wsConnection
	 * @param componentHandle
	 * @return
	 * @throws TeamRepositoryException
	 */
	@SuppressWarnings("rawtypes")
	public static boolean isComponentInWorkspace(IWorkspaceConnection wsConnection, IComponentHandle componentHandle)
			throws TeamRepositoryException {
		List components = wsConnection.getComponents();

		for (Iterator iterator = components.iterator(); iterator.hasNext();) {
			IComponentHandle compHandle = (IComponentHandle) iterator.next();
			if (componentHandle.getItemId().equals(compHandle.getItemId())) {
				return true;
			}
		}
		return false;
	}

}
