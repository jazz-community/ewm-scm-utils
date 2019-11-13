package com.ibm.js.team.supporttools.scm.statistics.sizerange;

public interface IRangeCalculator {
	
	int getInterval(long size);
	double getTopThreshold(int interval);
	String getName();

}
