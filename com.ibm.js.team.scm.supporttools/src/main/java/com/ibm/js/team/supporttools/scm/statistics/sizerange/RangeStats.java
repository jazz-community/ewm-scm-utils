package com.ibm.js.team.supporttools.scm.statistics.sizerange;

import com.ibm.js.team.supporttools.scm.utils.FileInfo;

public class RangeStats {

	RangeStat base2 = new RangeStat(new Base2RangeCalculator());
	RangeStat base10 = new RangeStat(new Base10RangeCalculator());
	
	public void analyze(FileInfo fileInfo){
		base2.analyze(fileInfo);
		base10.analyze(fileInfo);
	}

	public void logRangeInfo(){
		base2.printRangeInfos();
		base10.printRangeInfos();
	}
}
