/*******************************************************************************
 * Copyright (c) 2015-2019 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.client.IWorkspaceConnection;
import com.ibm.team.scm.client.IWorkspaceManager;
import com.ibm.team.scm.common.IComponent;
import com.ibm.team.scm.common.IComponentHandle;
import com.ibm.team.scm.common.dto.IComponentSearchCriteria;

/**
 * Utility Class to provide component operations.
 *
 */
public class ComponentUtil {

	/**
	 * Resolve a component from a handle.
	 * 
	 * @param teamRepository
	 * @param handle
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static IComponent resolveComponent(ITeamRepository teamRepository, IComponentHandle handle,
			IProgressMonitor monitor) throws TeamRepositoryException {
		IComponent component = (IComponent) teamRepository.itemManager().fetchCompleteItem(handle, IItemManager.DEFAULT,
				monitor);
		return component;
	}

	/**
	 * Resolve a list of components from a list of component handles.
	 * 
	 * @param teamRepository
	 * @param wsComponents
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static List<IComponent> resolveComponents(ITeamRepository teamRepository,
			List<IComponentHandle> wsComponents, IProgressMonitor monitor) throws TeamRepositoryException {
		@SuppressWarnings("unchecked")
		List<IComponent> components = (List<IComponent>) teamRepository.itemManager().fetchCompleteItems(wsComponents,
				IItemManager.DEFAULT, monitor);
		return components;
	}

	/**
	 * Get all components from a workspace connection and resolve them.
	 * 
	 * @param teamRepository
	 * @param workspaceConnection
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<IComponent> getComponents(ITeamRepository teamRepository,
			IWorkspaceConnection workspaceConnection, IProgressMonitor monitor) throws TeamRepositoryException {
		// Remove all components
		List wsComponents = workspaceConnection.getComponents();

		return resolveComponents(teamRepository, wsComponents, monitor);

	}

	/**
	 * Gets all existing components and puts them in a map for easier usage.
	 * 
	 * @param wm
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static HashMap<String, IComponentHandle> getComponentMap(IWorkspaceManager wm, IProgressMonitor monitor)
			throws TeamRepositoryException {
		HashMap<String, IComponentHandle> allComponents = new HashMap<String, IComponentHandle>();
		// These are the components I need

		// Try to find all components
		Set<String> components = wm.findAllComponentNames(monitor);
		for (String compName : components) {
			IComponentSearchCriteria criteria = IComponentSearchCriteria.FACTORY.newInstance();
			criteria.setExactName(compName);
			List<IComponentHandle> found = wm.findComponents(criteria, Integer.MAX_VALUE, monitor);

			if (found.size() > 1) {
				System.out.println("Ambiguous Component Names");
			}

			for (IComponentHandle iComponentHandle : found) {
				allComponents.put(compName, iComponentHandle);
			}
		}
		return allComponents;
	}
}
