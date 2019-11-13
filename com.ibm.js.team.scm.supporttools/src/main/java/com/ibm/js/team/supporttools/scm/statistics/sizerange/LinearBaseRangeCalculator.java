package com.ibm.js.team.supporttools.scm.statistics.sizerange;

public class LinearBaseRangeCalculator implements IRangeCalculator {
	String name = "Linear base"; 
	
	public LinearBaseRangeCalculator(int base) {
		super();
		this.base=base;
		name = "Linear base " + new Integer(base).toString();
	}

	int base=0;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getInterval(long size) {
		return (int) Math.ceil(size/this.base);
	}

	@Override
	public double getTopThreshold(int interval) {
		return (interval+1)*this.base;
	}

}
