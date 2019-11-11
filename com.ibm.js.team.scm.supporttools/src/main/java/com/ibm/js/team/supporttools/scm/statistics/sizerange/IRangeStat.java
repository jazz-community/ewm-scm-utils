package com.ibm.js.team.supporttools.scm.statistics.sizerange;

import com.ibm.js.team.supporttools.scm.utils.FileInfo;

public interface IRangeStat {

	void analyze(FileInfo fileInfo);
	
	void printRangeInfos();

}