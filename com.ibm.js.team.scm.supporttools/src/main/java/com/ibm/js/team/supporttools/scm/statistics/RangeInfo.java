package com.ibm.js.team.supporttools.scm.statistics;

import com.ibm.js.team.supporttools.scm.utils.FileInfo;
import com.ibm.team.filesystem.common.FileLineDelimiter;

public class RangeInfo {

	ExtensionsStats ext = new ExtensionsStats();
	private long hits = 0;

	public void addFileStat(String name, long size, long rawlength, long estLength, int depth,
			FileLineDelimiter lineDelimiter, String encoding) {
	}
	
	public long getFileCount(){
		return hits;
	}
	
	public ExtensionsStats getExtensionStatus(){
		return ext;
	}

	public void addFileStat(FileInfo fileInfo) {
		hits++;
		ext.analyze(fileInfo.getName(), fileInfo.getLineDelimiter(), fileInfo.getEncoding());
	}
}
