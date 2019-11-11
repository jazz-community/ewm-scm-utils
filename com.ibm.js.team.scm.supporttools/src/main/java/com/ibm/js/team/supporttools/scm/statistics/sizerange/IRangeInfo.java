package com.ibm.js.team.supporttools.scm.statistics.sizerange;

import com.ibm.js.team.supporttools.scm.statistics.IExtensions;
import com.ibm.js.team.supporttools.scm.utils.FileInfo;
import com.ibm.team.filesystem.common.FileLineDelimiter;

public interface IRangeInfo {

	long getIndex();

	public void addFileStat(String name, long size, long rawlength, long estLength, int depth, FileLineDelimiter lineDelimiter,
			String encoding);

	public IExtensions getExtensionStatus();

	public void addFileStat(FileInfo fileInfo);
	
	public long getFileCount();

}