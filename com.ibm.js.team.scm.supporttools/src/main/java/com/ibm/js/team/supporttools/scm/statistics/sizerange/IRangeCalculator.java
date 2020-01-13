/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.statistics.sizerange;

public interface IRangeCalculator {

	int getInterval(long size);

	double getTopThreshold(int interval);

	String getName();

}
