/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scmutils.utils;

import java.net.URI;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.process.client.IProcessClientService;
import com.ibm.team.process.client.IProcessItemService;
import com.ibm.team.process.common.IProcessArea;
import com.ibm.team.process.common.IProcessAreaHandle;
import com.ibm.team.process.common.IProjectArea;
import com.ibm.team.process.common.ITeamArea;
import com.ibm.team.process.common.ITeamAreaHandle;
import com.ibm.team.process.common.ITeamAreaHierarchy;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;

/**
 * Utility class for working with project areas.
 *
 */
public class ProjectAreaUtil {

	/**
	 * Find a ProcessArea by fully qualified name The name has to be a fully
	 * qualified name with the full path e.g. "JKE Banking(Change
	 * Management)/Business Recovery Matters"
	 * 
	 * @param name
	 * @param processClient
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static IProcessArea findProcessAreaByFQN(String name, IProcessClientService processClient,
			IProgressMonitor monitor) throws TeamRepositoryException {
		URI uri = URIUtil.getURIFromName(name);
		return (IProcessArea) processClient.findProcessArea(uri, IProcessItemService.ALL_PROPERTIES, monitor);
	}

	/**
	 * Find a ProjectArea by fully qualified name The name has to be a fully
	 * qualified name with the full path e.g. "JKE Banking(Change
	 * Management)/Business Recovery Matters"
	 * 
	 * @param name
	 * @param processClient
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static IProjectArea findProjectAreaByFQN(String name, IProcessClientService processClient,
			IProgressMonitor monitor) throws TeamRepositoryException {
		IProcessArea processArea = findProcessAreaByFQN(name, processClient, monitor);
		if (null != processArea && processArea instanceof IProjectArea) {
			return (IProjectArea) processArea;
		}
		return null;
	}
	
	/**
	 * Resolve a ProcessArea
	 * 
	 * @param handle
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static IProcessArea resolveProcessArea(IProcessAreaHandle handle, IProgressMonitor monitor)
			throws TeamRepositoryException {
		// To avoid having to resolve if it already resolved
		if (handle instanceof IProcessArea) {
			return (IProcessArea) handle;
		}
		// Resolve handle
		return (IProcessArea) ((ITeamRepository) handle.getOrigin()).itemManager().fetchCompleteItem(handle,
				IItemManager.DEFAULT, monitor);
	}

	/**
	 * Resolve a Team Area
	 * 
	 * @param handle
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static ITeamArea resolveTeamArea(IProcessAreaHandle handle, IProgressMonitor monitor)
			throws TeamRepositoryException {
		// To avoid having to resolve if it already resolved
		if (handle instanceof ITeamArea) {
			return (ITeamArea) handle;
		}
		// Resolve handle
		return (ITeamArea) ((ITeamRepository) handle.getOrigin()).itemManager().fetchCompleteItem(handle,
				IItemManager.DEFAULT, monitor);
	}

	/**
	 * Resolve a Project Area
	 * 
	 * @param handle
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static IProjectArea resolveProjectArea(IProcessAreaHandle handle, IProgressMonitor monitor)
			throws TeamRepositoryException {
		// To avoid having to resolve if it already resolved
		if (handle instanceof IProjectArea) {
			return (IProjectArea) handle;
		}
		// Resolve handle
		return (IProjectArea) ((ITeamRepository) handle.getOrigin()).itemManager().fetchCompleteItem(handle,
				IItemManager.DEFAULT, monitor);
	}

	
	/**
	 * @param handle
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	public static String getFullQualifiedName(IProcessAreaHandle handle, IProgressMonitor monitor)
			throws TeamRepositoryException {

		IProcessArea area = resolveProcessArea(handle, monitor);
		if (area instanceof IProjectArea) {
			return area.getName();
		} else if (area instanceof ITeamArea) {
			ITeamArea tArea = (ITeamArea) area;
			IProjectArea pArea = (IProjectArea) resolveProjectArea(area.getProjectArea(), monitor);
			return pArea.getName() + "/"
					+ getFullQualifiedName(tArea, pArea.getTeamAreaHierarchy(), monitor);
		}
		return "";
	}

	/**
	 * @param tArea
	 * @param teamAreaHierarchy
	 * @param monitor
	 * @return
	 * @throws TeamRepositoryException
	 */
	private static String getFullQualifiedName(ITeamArea tArea, ITeamAreaHierarchy teamAreaHierarchy,
			IProgressMonitor monitor) throws TeamRepositoryException {
		ITeamAreaHandle parent = teamAreaHierarchy.getParent(tArea);
		String teamAreName = tArea.getName();
		if (parent == null) {
			return teamAreName;
		}
		ITeamArea parentArea = resolveTeamArea(parent, monitor);
		return getFullQualifiedName(parentArea, teamAreaHierarchy, monitor) + "/" + teamAreName;
	}

}
