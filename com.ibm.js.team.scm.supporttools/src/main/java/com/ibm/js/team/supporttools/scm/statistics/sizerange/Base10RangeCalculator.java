/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.statistics.sizerange;

/**
 * Calculates number of file using a log 10 based size mesh.
 *
 */
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
		return Math.pow(10, interval);
	}

}
