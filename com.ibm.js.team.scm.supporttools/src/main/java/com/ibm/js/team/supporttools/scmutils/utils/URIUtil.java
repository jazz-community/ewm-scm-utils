/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scmutils.utils;

import java.net.URI;

import com.ibm.team.repository.client.ITeamRepository;
import com.ibm.team.repository.common.IItemHandle;
import com.ibm.team.repository.common.Location;

/**
 * For operations on URIs.
 *
 */
public class URIUtil {

	/**
	 * URI conversion to be able to find from a URI
	 * 
	 * @param name
	 * @return
	 */
	public static URI getURIFromName(String name) {
		URI uri = URI.create(name.replaceAll(" ", "%20"));
		return uri;
	}

	public static URI getURIForItem(IItemHandle item) {
		// CopyBaselineWebURLAction
		// Location.itemLocation(item, repoUrl, query, serviceName)
		Location location = Location.itemLocation(item, getPublicURI(item));
		return location.toAbsoluteUri();
	}

	public static String getPublicURI(IItemHandle item) {
		return getPublicURI(((ITeamRepository) item.getOrigin()));
	}

	/**
	 * Returns the public URI root used to construct public locations for the
	 * given repository. If the repository has no public URI root configured,
	 * its regular URI is returned.
	 *
	 * @param repo
	 *            the repository
	 * @return the public URI root for the repository (never <code>null</code>)
	 */
	public static String getPublicURI(ITeamRepository repo) {
		String uri = repo.publicUriRoot();
		if (uri == null) {
			uri = repo.getRepositoryURI();
		}
		return uri;
	}
}
