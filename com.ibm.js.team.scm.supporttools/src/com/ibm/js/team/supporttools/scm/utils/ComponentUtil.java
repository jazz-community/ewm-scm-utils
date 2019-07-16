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
import com.ibm.team.scm.client.SCMPlatform;
import com.ibm.team.scm.common.IComponent;
import com.ibm.team.scm.common.IComponentHandle;
import com.ibm.team.scm.common.IWorkspaceHandle;
import com.ibm.team.scm.common.dto.IComponentSearchCriteria;
import com.ibm.team.scm.common.dto.IWorkspaceSearchCriteria;

public class ComponentUtil {

	public  static IComponent resolveComponent(ITeamRepository teamRepository, IComponentHandle handle,
			IProgressMonitor monitor) throws TeamRepositoryException {
		@SuppressWarnings("unchecked")
		IComponent component = (IComponent) teamRepository.itemManager().fetchCompleteItem(handle, IItemManager.DEFAULT,
				monitor);
		return component;
	}

	public  static List<IComponent> resolveComponents(ITeamRepository teamRepository, List<IComponentHandle> wsComponents,
			IProgressMonitor monitor) throws TeamRepositoryException {
		@SuppressWarnings("unchecked")
		List<IComponent> components = (List<IComponent>) teamRepository.itemManager().fetchCompleteItems(wsComponents,
				IItemManager.DEFAULT, monitor);
		return components;
	}

	/**
	 * @param teamRepository
	 * @param workspaceConnection
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public  static List<IComponent> getComponents(ITeamRepository teamRepository, IWorkspaceConnection workspaceConnection,
			IProgressMonitor monitor) throws TeamRepositoryException {
		// Remove all components
		List wsComponents = workspaceConnection.getComponents();
	
		return resolveComponents(teamRepository, wsComponents, monitor);
	
	}

	public static List<? extends IWorkspaceConnection> getWorkspaceConnections(ITeamRepository teamRepository,List<? extends IWorkspaceHandle> connections, IProgressMonitor monitor) throws TeamRepositoryException {
		IWorkspaceManager wm = SCMPlatform.getWorkspaceManager(teamRepository);
		return wm.getWorkspaceConnections(connections, monitor);
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
	public static List<IWorkspaceHandle> findWorkspacesByName(ITeamRepository teamRepository,
			String scmConnectionName, int kind, IProgressMonitor monitor) throws TeamRepositoryException {
		IWorkspaceManager wm = SCMPlatform.getWorkspaceManager(teamRepository);
		IWorkspaceSearchCriteria criteria = IWorkspaceSearchCriteria.FACTORY.newInstance().setKind(kind);
		if (scmConnectionName != null) {
			criteria.setExactName(scmConnectionName);
		}
		List<IWorkspaceHandle> connections = wm.findWorkspaces(criteria, Integer.MAX_VALUE, monitor);
		return connections;
		//return wm.getWorkspaceConnections(connections, monitor);
	}

	/**
	 * Gets all existing components and puts them in a map for easier usage.
	 * 
	 * @param wm
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static HashMap<String, IComponentHandle> getComponentMap(
			IWorkspaceManager wm, IProgressMonitor monitor)
			throws TeamRepositoryException {
		HashMap<String, IComponentHandle> allComponents = new HashMap<String, IComponentHandle>();
		// These are the components I need
	
		// Try to find all components
		Set<String> components = wm.findAllComponentNames(monitor);
		for (String compName : components) {
			IComponentSearchCriteria criteria = IComponentSearchCriteria.FACTORY
					.newInstance();
			criteria.setExactName(compName);
			List<IComponentHandle> found = wm.findComponents(criteria,
					Integer.MAX_VALUE, monitor);
	
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
