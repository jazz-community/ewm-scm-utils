package com.ibm.js.team.supporttools.scm.statistics.sizerange;

import java.util.Iterator;

import com.ibm.js.team.supporttools.scm.utils.FileInfo;

public interface IRangeStat {

	public Iterator<IRangeInfo> iterator();
	public void analyze(FileInfo fileInfo);
	public void printRangeInfos();
	public long getTotalFiles();
	public IRangeCalculator getRangeCalculator();

}