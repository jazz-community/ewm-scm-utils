package com.ibm.js.team.supporttools.scm.statistics.sizerange;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.scm.statistics.IExtensions;
import com.ibm.js.team.supporttools.scm.utils.FileInfo;
import com.ibm.team.filesystem.common.FileLineDelimiter;

public interface IRangeInfo {
	public static final Logger logger = LoggerFactory.getLogger(IRangeInfo.class);

	long getIndex();

	public void addFileStat(String name, long size, long rawlength, long estLength, int depth, FileLineDelimiter lineDelimiter,
			String encoding);

	public IExtensions getExtensionStatus();

	public void addFileStat(FileInfo fileInfo);
	
	public long getFileCount();

}