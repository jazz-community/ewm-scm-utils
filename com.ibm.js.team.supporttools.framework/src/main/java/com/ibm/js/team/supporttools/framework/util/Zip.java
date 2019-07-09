/**
 * Licensed Materials - Property of IBM
 *
 * © Copyright IBM Corporation 2010.
 * U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 * 
 */
package com.ibm.js.team.supporttools.framework.util;

import java.io.File;

/**
 * Zip and log to the console
 * 
 * 
 */
public class Zip extends AbstractZip {

	public Zip(File zipFile, File zipRootFolder) {
		super(zipFile, zipRootFolder);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.field.clm.tools.AbstractZip#log(java.lang.String)
	 */
	@Override
	protected void log(String message) {
		System.out.println(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.field.clm.tools.AbstractZip#logException(java.lang.String,
	 * java.lang.Exception)
	 */
	@Override
	protected void logException(String message, Exception e) {
		System.out.println(message + " " + e.getMessage());
	}

}
