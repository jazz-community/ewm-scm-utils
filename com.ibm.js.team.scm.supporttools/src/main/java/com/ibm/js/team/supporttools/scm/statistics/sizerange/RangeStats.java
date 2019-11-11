package com.ibm.js.team.supporttools.scm.statistics.sizerange;

import java.util.ArrayList;
import java.util.Iterator;

import com.ibm.js.team.supporttools.scm.utils.FileInfo;

/**
 * @see https://www.callicoder.com/java-write-excel-file-apache-poi/
 *
 */
public class RangeStats {
	ArrayList<IRangeStat> rangeStats = new ArrayList<IRangeStat>();
	public RangeStats() {
		super();
		rangeStats.add((IRangeStat)new RangeStat(new Base2RangeCalculator()));
		rangeStats.add((IRangeStat)new RangeStat(new Base10RangeCalculator()));
	}
	
	class RangeStatsIterator implements Iterator<IRangeStat>{

		Iterator<IRangeStat> iterator = null;
		public RangeStatsIterator() {
			super();
			this.iterator = rangeStats.iterator();
		}

		@Override
		public boolean hasNext() {
			return this.iterator.hasNext();
		}

		@Override
		public IRangeStat next() {
			return this.iterator.next();
		}
	}
	
	public void analyze(FileInfo fileInfo){
		for (IRangeStat iRangeStat : rangeStats) {
			iRangeStat.analyze(fileInfo);
		}
	}

	public void logRangeInfo(){
		for (IRangeStat iRangeStat : rangeStats) {
			iRangeStat.printRangeInfos();
		}
	}
}
