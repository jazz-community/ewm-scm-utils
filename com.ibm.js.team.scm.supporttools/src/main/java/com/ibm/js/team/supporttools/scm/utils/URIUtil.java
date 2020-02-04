package com.ibm.js.team.supporttools.scm.utils;

import java.net.URI;

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
