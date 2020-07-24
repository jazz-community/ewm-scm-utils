/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scmutils.statistics.sizerange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.scmutils.statistics.FileInfo;
import com.ibm.js.team.supporttools.scmutils.statistics.IExtensions;
import com.ibm.team.filesystem.common.FileLineDelimiter;

public interface IRangeInfo {
	public static final Logger logger = LoggerFactory.getLogger(IRangeInfo.class);

	long getIndex();

	public void addFileStat(String name, long size, long rawlength, long estLength, int depth,
			FileLineDelimiter lineDelimiter, String encoding);

	public IExtensions getExtensionStatus();

	public void addFileStat(FileInfo fileInfo);

	public long getFileCount();

}