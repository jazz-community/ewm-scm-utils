/*******************************************************************************
 * Copyright (c) 2015-2019 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.statistics.sizerange;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.scm.utils.FileInfo;
import com.ibm.js.team.supporttools.scm.utils.FormatUtil;

public class RangeStat implements IRangeStat {
	IRangeCalculator rangeCalc = null;

	private HashMap<Integer,IRangeInfo> rangeMap = new HashMap<Integer,IRangeInfo> ();
	public static final Logger logger = LoggerFactory.getLogger(RangeStat.class);
	private long totalFileCount = 0;

	class RangeInfoIterator implements Iterator<IRangeInfo>{
		
		int index = -1;
		int rangesLeft=-1;
		
		public RangeInfoIterator() {
			super();
			this.index = 0;
			this.rangesLeft= rangeMap.size();
		}

		@Override
		public boolean hasNext() {
			return rangesLeft>0;
		}

		@Override
		public IRangeInfo next() {
			Integer current = new Integer(this.index++);
			IRangeInfo range = rangeMap.get(current);
			if(range!=null){
				this.rangesLeft--;
			} else {
				range = new RangeInfo(current);	
			}
			return range;
		}
	}
	
	public RangeStat(IRangeCalculator rangeCalc) {
		super();
		this.rangeCalc = rangeCalc;
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.js.team.supporttools.scm.statistics.sizerange.IRangeStat#analyze(com.ibm.js.team.supporttools.scm.utils.FileInfo)
	 */
	@Override
	public void analyze(FileInfo fileInfo) {
		IRangeInfo potRange = geRangeInfo(fileInfo.getSize());
		potRange.addFileStat(fileInfo);		
		totalFileCount++;
	}
	
	/* (non-Javadoc)
	 * @see com.ibm.js.team.supporttools.scm.statistics.sizerange.IRangeStat#printRangeInfos()
	 */
	@Override
	public void printRangeInfos(){
		int totalFileCount=0;
		logger.info(rangeCalc.getName() + "...");
		Set<Integer> keys = rangeMap.keySet();
		int rangesLeft = keys.size();
		int currentInterval=0;
		while(rangesLeft>0){
			long fileCount = 0;
			IRangeInfo range = rangeMap.get(currentInterval);
			String extensions ="";
			if(range!=null){
				fileCount = range.getFileCount();
				extensions = range.getExtensionStatus().extensionsSimple();
				rangesLeft--;
			}
			logger.info("{} files to {} bytes. (Range {}), {}", fileCount, FormatUtil.getLeftAligned(rangeCalc.getTopThreshold(currentInterval)), currentInterval, extensions);			
			totalFileCount+=fileCount;
			currentInterval++;
		}
		logger.info("Total {} \n", totalFileCount);		
	}

	private IRangeInfo geRangeInfo(long size) {
		int index=0;
		if(size>0){
			index = rangeCalc.getInterval(size);
			if(index<0){ // Ensure index is always positive to avoid endless loop during output.
				index=0;
			}
		}
	    IRangeInfo ring = rangeMap.get(index);
		if (ring == null){
			ring = new RangeInfo(index);
			rangeMap.put(index, ring);
		}
		return ring;
	}

	public Iterator<IRangeInfo> iterator() {
		return new RangeInfoIterator();
	}

	@Override
	public long getTotalFiles() {
		return this.totalFileCount;
	}

	@Override
	public IRangeCalculator getRangeCalculator() {
		return this.rangeCalc;
	}

}
