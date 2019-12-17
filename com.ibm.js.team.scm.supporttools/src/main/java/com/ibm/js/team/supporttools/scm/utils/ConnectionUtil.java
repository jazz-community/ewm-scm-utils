package com.ibm.js.team.supporttools.scm.utils;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.scm.client.IWorkspaceConnection;
import com.ibm.team.scm.client.IWorkspaceManager;
import com.ibm.team.scm.client.SCMPlatform;
import com.ibm.team.scm.common.IWorkspaceHandle;
import com.ibm.team.scm.common.dto.IWorkspaceSearchCriteria;

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
		// return wm.getWorkspaceConnections(connections, monitor);
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

}
