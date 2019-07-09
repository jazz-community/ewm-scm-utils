/**
 * Licensed Materials - Property of IBM
 *
 * © Copyright IBM Corporation 2010.
 * U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 * 
 */
package com.ibm.js.team.supporttools.framework.framework;

/**
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
