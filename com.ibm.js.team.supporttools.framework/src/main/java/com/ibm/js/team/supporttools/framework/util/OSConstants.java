/*******************************************************************************
 * Copyright (c) 2015-2019 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.framework.util;

/**
 * @see package com.ibm.team.filesystem.cli.core.Constants;
 * 
 */
public class OSConstants {
	/**
	 * Set to <code>true</code> when running on MS Windows, <code>false</code>
	 * otherwise.
	 */
	public final static boolean ON_WINDOWS = System.getProperty("os.name").toLowerCase().contains("windows"); //$NON-NLS-1$//$NON-NLS-2$

	public final static String OSNAME = System.getProperty("os.name"); //$NON-NLS-1$ //$NON-NLS-2$

}
