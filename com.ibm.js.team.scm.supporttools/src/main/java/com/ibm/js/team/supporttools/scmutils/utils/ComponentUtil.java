/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scmutils.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.IAuditableHandle;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.client.IWorkspaceConnection;
import com.ibm.team.scm.client.IWorkspaceManager;
import com.ibm.team.scm.common.IComponent;
import com.ibm.team.scm.common.IComponentHandle;
import com.ibm.team.scm.common.dto.IComponentSearchCriteria;
import com.ibm.team.scm.common.dto.IReadScope;

/**
 * Utility Class to provide component operations.
 *
 */
public class ComponentUtil {

	public static final Logger logger = LoggerFactory.getLogger(ComponentUtil.class);

	/**
	 * Create the component.
	 * 
	 * @param teamRepository
	 * @param wm
	 * @param compName
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static IComponentHandle createComponent(ITeamRepository teamRepository, IWorkspaceManager wm, String compName,
			IAuditableHandle owner, IProgressMonitor monitor) throws TeamRepositoryException {
		IComponentHandle component;
		// Create Component
		component = wm.createComponent(compName, teamRepository.loggedInContributor(), monitor);
		wm.setComponentOwner(component, owner, monitor);
		return component;
	}
	
	/**
	 * Find a component by its name.
	 * 
	 * @param wm
	 * @param compName
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static IComponentHandle findComponentByName(IWorkspaceManager wm, String compName, IProgressMonitor monitor)
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
	
	public static void setComponentOwnerAndVisibility(IWorkspaceManager wm, IComponentHandle componentHandle, IAuditableHandle owner, IReadScope readScope, IProgressMonitor monitor) throws TeamRepositoryException{
		wm.setComponentOwnerAndVisibility(componentHandle, owner, readScope, monitor);
	}

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
