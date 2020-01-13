/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.statistics.sizerange;

import com.ibm.js.team.supporttools.scm.statistics.ExtensionsStats;
import com.ibm.js.team.supporttools.scm.statistics.FileInfo;
import com.ibm.js.team.supporttools.scm.statistics.IExtensions;
import com.ibm.team.filesystem.common.FileLineDelimiter;

public class RangeInfo implements IRangeInfo {

	public RangeInfo(long index) {
		super();
		this.index = index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ibm.js.team.supporttools.scm.statistics.sizerange.IRangeInfo#getIndex
	 * ()
	 */
	@Override
	public long getIndex() {
		return index;
	}

	public IExtensions getExt() {
		return ext;
	}

	long index = -1;
	IExtensions ext = new ExtensionsStats();
	private long hits = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.js.team.supporttools.scm.statistics.sizerange.IRangeInfo#
	 * addFileStat(java.lang.String, long, long, long, int,
	 * com.ibm.team.filesystem.common.FileLineDelimiter, java.lang.String)
	 */
	@Override
	public void addFileStat(String name, long size, long rawlength, long estLength, int depth,
			FileLineDelimiter lineDelimiter, String encoding) {
	}

	public long getFileCount() {
		return hits;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.js.team.supporttools.scm.statistics.sizerange.IRangeInfo#
	 * getExtensionStatus()
	 */
	@Override
	public IExtensions getExtensionStatus() {
		return ext;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.js.team.supporttools.scm.statistics.sizerange.IRangeInfo#
	 * addFileStat(com.ibm.js.team.supporttools.scm.utils.FileInfo)
	 */
	@Override
	public void addFileStat(FileInfo fileInfo) {
		hits++;
		ext.analyze(fileInfo.getName(), fileInfo.getLineDelimiter(), fileInfo.getEncoding());
	}
}
