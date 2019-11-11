package com.ibm.js.team.supporttools.scm.statistics.sizerange;

import java.util.HashMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.js.team.supporttools.scm.statistics.RangeInfo;
import com.ibm.js.team.supporttools.scm.utils.FileInfo;

public class RangeStat {
	IRangeCalculator rangeCalc = null;
	HashMap<Integer,RangeInfo> rangeMap = new HashMap<Integer,RangeInfo> ();
	public static final Logger logger = LoggerFactory.getLogger(RangeStat.class);
	

	public RangeStat(IRangeCalculator rangeCalc) {
		super();
		this.rangeCalc = rangeCalc;
	}
	
	public void analyze(FileInfo fileInfo) {
		RangeInfo potRange = geRangeInfo(fileInfo.getSize());
		potRange.addFileStat(fileInfo);			
	}
	
	public void printRangeInfos(){
		int totalFileCount=0;
		logger.info(rangeCalc.getName() + "...");
		Set<Integer> keys = rangeMap.keySet();
		int rangesLeft = keys.size();
		int currentInterval=0;
		while(rangesLeft!=0){
			long fileCount = 0;
			RangeInfo range = rangeMap.get(currentInterval);
			String extensions ="";
			if(range!=null){
				fileCount = range.getFileCount();
				extensions = range.getExtensionStatus().extensionsSimple();
				rangesLeft--;
			}
			logger.info("{} - {} # {} {}", currentInterval, rangeCalc.getTopThreshold(currentInterval), fileCount, extensions);			
			totalFileCount+=fileCount;
			currentInterval++;
		}
		logger.info("Total {} \n", totalFileCount);		
	}

	private RangeInfo geRangeInfo(long size) {
		int index=0;
		if(size>0){
			index = rangeCalc.getInterval(size);
			if(index<0){ // Ensure index is always positive to avoid endless loop during output.
				index=0;
			}
		}
	    RangeInfo ring = rangeMap.get(index);
		if (ring == null){
			ring = new RangeInfo();
			rangeMap.put(index, ring);
		}
		return ring;
	}

}
