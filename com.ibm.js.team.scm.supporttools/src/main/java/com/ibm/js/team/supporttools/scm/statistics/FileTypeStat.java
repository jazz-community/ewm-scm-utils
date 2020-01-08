/*******************************************************************************
 * Copyright (c) 2015-2019 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.statistics;

import java.util.HashSet;
import java.util.Set;

import com.ibm.team.filesystem.common.FileLineDelimiter;

public class FileTypeStat implements IFileTypeStat {
	
	String extensionName = null;
	Set<String> lineDelimiters = new HashSet<String>();
	Set<String> encodings = new HashSet<String>();
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.js.team.supporttools.scm.statistics.IFileTypeStat#
	 * getExtensionName()
	 */
	@Override
	public String getExtensionName() {
		return extensionName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.js.team.supporttools.scm.statistics.IFileTypeStat#
	 * getLineDelimiters()
	 */
	@Override
	public Set<String> getLineDelimiters() {
		return lineDelimiters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.js.team.supporttools.scm.statistics.IFileTypeStat#getEncodings()
	 */
	@Override
	public Set<String> getEncodings() {
		return encodings;
	}

	public FileTypeStat(String ext) {
		extensionName = ext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.js.team.supporttools.scm.statistics.IFileTypeStat#analyze(java.
	 * lang.String, com.ibm.team.filesystem.common.FileLineDelimiter,
	 * java.lang.String)
	 */
	@Override
	public void analyze(String ext, FileLineDelimiter lineDelimiter, String encoding) {
		if (lineDelimiter == null) {
			return;
		}
		lineDelimiters.add(lineDelimiter.toString());
		encodings.add(encoding);
	}

	@Override
	public String toString() {
		String extension = "'" + extensionName + "'";
		if (lineDelimiters.size() > 0) {
			extension += " - " + lineDelimiters.toString() + " : " + encodings.toString();
		}
		return extension;
	}
}
