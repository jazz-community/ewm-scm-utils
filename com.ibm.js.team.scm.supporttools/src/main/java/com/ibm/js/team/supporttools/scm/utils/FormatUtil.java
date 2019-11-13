package com.ibm.js.team.supporttools.scm.utils;

public class FormatUtil {
	
	public static String getLeftAligned(double d){
		Double data = new Double(d);
		String blanks = "                    ";
		String sValue = blanks+data.intValue();
		return sValue.substring(sValue.length()-10);
	}

}
