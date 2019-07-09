/**
 * Licensed Materials - Property of IBM
 *
 * Constants.java
 * © Copyright IBM Corporation 2010.
 * U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 * 
 */

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
	public final static boolean ON_WINDOWS = System
			.getProperty("os.name").toLowerCase().contains("windows"); //$NON-NLS-1$//$NON-NLS-2$

	public final static String OSNAME = System.getProperty("os.name"); //$NON-NLS-1$//$NON-NLS-2$

}
