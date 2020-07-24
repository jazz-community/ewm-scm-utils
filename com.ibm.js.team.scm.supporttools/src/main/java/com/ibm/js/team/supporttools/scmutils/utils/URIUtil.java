/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scmutils.utils;

import java.net.URI;

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

}
