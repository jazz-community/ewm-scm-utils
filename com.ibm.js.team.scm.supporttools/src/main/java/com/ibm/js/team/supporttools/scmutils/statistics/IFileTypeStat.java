/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scmutils.statistics;

import java.util.Set;

import com.ibm.team.filesystem.common.FileLineDelimiter;

/**
 * Interface for file type statistics.
 *
 */
public interface IFileTypeStat {

	String getExtensionName();

	Set<String> getLineDelimiters();

	Set<String> getEncodings();

	void analyze(String ext, FileLineDelimiter lineDelimiter, String encoding);

}