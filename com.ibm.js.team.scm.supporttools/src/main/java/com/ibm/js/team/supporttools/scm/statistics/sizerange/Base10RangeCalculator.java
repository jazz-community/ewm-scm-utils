package com.ibm.js.team.supporttools.scm.statistics.sizerange;

public class Base10RangeCalculator implements IRangeCalculator {
	String name = "Log 10";

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getInterval(long size) {
		return (int) Math.ceil(Math.log10(size));
	}

	@Override
	public double getTopThreshold(int interval) {
		return Math.pow(10,interval);
	}

}
