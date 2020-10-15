/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scmutils.utils;

import org.eclipse.core.runtime.IProgressMonitor;

import com.ibm.team.build.client.ITeamBuildClient;
import com.ibm.team.build.common.BuildItemFactory;
import com.ibm.team.build.common.model.IBuildResult;
import com.ibm.team.build.common.model.IBuildResultContribution;
import com.ibm.team.build.common.model.IBuildResultHandle;
import com.ibm.team.repository.client.IItemManager;
import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.TeamRepositoryException;
import com.ibm.team.repository.common.UUID;

/**
 * Utility to interact with build request and build results.
 *
 */
public class BuildUtil {

	/**
	 * @param teamRepository
	 * @param buildResultUUID
	 * @param monitor
	 * @return
	 */
	public static IBuildResult getBuildResult(ITeamRepository teamRepository, String buildResultUUID,
			IProgressMonitor monitor) {
		IBuildResultHandle resultHandle = null;
		IBuildResult buildResult = null;
		try {
			resultHandle = (IBuildResultHandle) IBuildResult.ITEM_TYPE.createItemHandle(UUID.valueOf(buildResultUUID),
					null);
			buildResult = (IBuildResult) teamRepository.itemManager().fetchCompleteItem(resultHandle,
					IItemManager.REFRESH, monitor);
			return buildResult;
		} catch (TeamRepositoryException e) {
			return null;
		}
	}

	/**
	 * @param teamRepository
	 * @param buildResult
	 * @param component
	 * @param linkURL
	 * @param linkLabel
	 * @param monitor
	 * @throws IllegalArgumentException
	 * @throws TeamRepositoryException
	 */
	public static void publishLink(ITeamRepository teamRepository, IBuildResult buildResult, String component,
			String linkURL, String linkLabel, IProgressMonitor monitor)
			throws IllegalArgumentException, TeamRepositoryException {

		if (linkURL == null) {
			throw new IllegalArgumentException("Link URL can not be null");
		}
		if (linkLabel == null) {
			throw new IllegalArgumentException("Link Label can not be null");
		}
		IBuildResult wcBuildResult = (IBuildResult) buildResult.getWorkingCopy();
		IBuildResultContribution link = BuildItemFactory.createBuildResultContribution();
		if (component != null) {
			link.setComponentName(component);
		}
		link.setLabel(linkLabel);
		link.setExtendedContributionTypeId(IBuildResultContribution.LINK_EXTENDED_CONTRIBUTION_ID);
		link.setExtendedContributionProperty(IBuildResultContribution.PROPERTY_NAME_URL, linkURL);

		// Save Link
		ITeamBuildClient buildClient = (ITeamBuildClient) teamRepository.getClientLibrary(ITeamBuildClient.class);
		buildClient.addBuildResultContribution((IBuildResultHandle) wcBuildResult.getItemHandle(), link, monitor);
		buildClient.save(wcBuildResult, null);
	}
}
