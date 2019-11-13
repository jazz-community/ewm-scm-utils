package com.ibm.js.team.supporttools.scm.statistics.sizerange;

public class Base2RangeCalculator implements IRangeCalculator {
	String name = "Log 2";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getInterval(long size) {
		return (int) Math.ceil(log2(size));
	}

	@Override
	public double getTopThreshold(int interval) {
		return Math.pow(2,interval);
	}

	double log2(long x){
		return log(x,2);
	}	
	
	double log(long x, long base){
		return (Math.log(x)/Math.log(base));
	}

}
