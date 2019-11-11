package com.ibm.js.team.supporttools.scm.statistics.sizerange;

public interface IRangeCalculator {
	
	int getInterval(long size);
	int getTopThreshold(int interval);
	String getName();

}
