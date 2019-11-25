/*******************************************************************************
 * Copyright (c) 2015-2019 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.statistics;

import java.util.HashMap;

import com.ibm.team.filesystem.common.FileLineDelimiter;

public interface IExtensions {

	public HashMap<String, IFileTypeStat> getExtensions();
	public void analyze(String ext, FileLineDelimiter lineDelimiter, String encoding);
	String extensionsSimple();
	int getNoExtensions();
	String getExtensionsCompressed();

}