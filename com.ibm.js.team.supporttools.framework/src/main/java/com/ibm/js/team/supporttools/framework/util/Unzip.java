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
 * Unzip and log to the console
 * 
 * 
 */
public class Unzip extends AbstractUnzip {

	/**
	 * @param zipFile
	 * @param zipRootFolder
	 */
	public Unzip(File zipFile, File zipRootFolder) {
		super(zipFile, zipRootFolder);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.field.clm.tools.AbstractUnzip#log(java.lang.String)
	 */
	@Override
	protected void log(String message) {
		System.out.println(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.field.clm.tools.AbstractUnzip#logException(java.lang.String,
	 * java.lang.Exception)
	 */
	@Override
	protected void logException(String message, Exception e) {
		System.out.println(message + " " + e.getMessage());
	}

	public static void main(String[] args) {
		// Unzip unzip= new Unzip(new File ("C:/temp/ZipTest/workspaces.zip"),
		// new File ("C:/temp/ZipTest/ws"));
		Unzip unzip = new Unzip(new File("C:/temp/ZipTest/ccm.zip"), new File("C:/temp/ZipTest/ccm"));
		try {
			unzip.unZip();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
