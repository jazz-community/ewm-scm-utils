/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.framework.framework;

/**
 * An exception that could be used in the framework. Not yet used.
 *
 */
public class SupportToolsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 899587962212598561L;

	public SupportToolsException() {
	}

	public SupportToolsException(String arg0) {
		super(arg0);
	}

	public SupportToolsException(Throwable arg0) {
		super(arg0);
	}

	public SupportToolsException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public SupportToolsException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
