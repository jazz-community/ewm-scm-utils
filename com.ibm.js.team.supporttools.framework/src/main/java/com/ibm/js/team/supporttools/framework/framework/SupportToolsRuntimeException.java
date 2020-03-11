/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/

package com.ibm.js.team.supporttools.framework.framework;

/**
 * A runtime exception that could be used in the framework. 
 *
 */
public class SupportToolsRuntimeException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5738420013759278337L;
	Throwable ex = null;

	public SupportToolsRuntimeException(String message) {
		super(message);
	}

	public SupportToolsRuntimeException(String message, Throwable e) {
		super(message);
		this.ex = e;
	}

}
