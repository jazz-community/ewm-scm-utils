package com.ibm.js.team.supporttools.scm.statistics;

public class CalcUtil {
	
	public static String divideFloat(long a , long b) {
		if(b==0) {
			return "devide by zero";
		}
		float c = (float)a/(float)b;
		return Float.toString(c);
	}

	public static String divideLong(long a , long b) {
		if(b==0) {
			return "devide by zero";
		}
		long c = a/b;
		return String.valueOf(c);
	}
	
	public static long calcMax(long a, long b) {
		if (a > b) {
			return a;
		}
		return b;
	}
	
}
