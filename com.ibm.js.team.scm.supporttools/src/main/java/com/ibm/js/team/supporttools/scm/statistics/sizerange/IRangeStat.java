/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
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