/**
 * Licensed Materials - Property of IBM
 *
 * © Copyright IBM Corporation 2010.
 * U.S. Government Users Restricted Rights:  Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 * 
 */
package com.ibm.js.team.supporttools.scm.compression;

import java.io.File;

import com.ibm.js.team.supporttools.framework.util.AbstractZip;

/**
 * Zip and use IStatusCollector for output
 * 
 * 
 */
public class Zip extends AbstractZip {

//	IStatusCollector status = null;

	/**
	 * @param zipFile
	 * @param zipRootFolder
	 * @param status
	 */
	public Zip(File zipFile, File zipRootFolder) {
//		public Zip(File zipFile, File zipRootFolder, IStatusCollector status) {
		super(zipFile, zipRootFolder);
//		this.status = status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.field.clm.tools.AbstractZip#log(java.lang.String)
	 */
	protected void log(String message) {
//		if (null != status) {
//			status.logDetail(message);
//		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.field.clm.tools.AbstractZip#logException(java.lang.String,
	 * java.lang.Exception)
	 */
	protected void logException(String message, Exception e) {
//		if (null != status) {
//			status.logException(message, e);
//		}
	}
}
