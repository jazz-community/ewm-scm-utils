/*******************************************************************************
 * Copyright (c) 2015-2019 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.utils;

public class PrintUtil {

	private static final String BYTE_STRING = "B";
	private static final String KILOBYTE_STRING = "KB";
	private static final String MEGABYTE_STRING = "MB";
	private static final String GIGABYTE_STRING = "GB";
	private static final String TERABYTE_STRING = "TB";
	public static final Double KILOBYTE = new Double(Math.pow(2, 10));
	public static final Double MEGABYTE = new Double(Math.pow(2, 20));
	public static final Double GIGABYTE = new Double(Math.pow(2, 30));
	public static final Double TERABYTE = new Double(Math.pow(2, 40));

	public static String asBinaryMagnitude(long cumulatedFileSize) {
		return asBinaryMagnitude(new Double(cumulatedFileSize));
	}

	public static String asBinaryMagnitude(Double cumulatedFileSize) {
		Double adjusted;
		if (cumulatedFileSize > TERABYTE) {
			adjusted = CalcUtil.divide(cumulatedFileSize, TERABYTE);
			return CalcUtil.roundPrecision2(adjusted).toString() + " " + TERABYTE_STRING;
		}
		if (cumulatedFileSize > GIGABYTE) {
			adjusted = CalcUtil.divide(cumulatedFileSize, GIGABYTE);
			return CalcUtil.roundPrecision2(adjusted).toString() + " " + GIGABYTE_STRING;
		}
		if (cumulatedFileSize > MEGABYTE) {
			adjusted = CalcUtil.divide(cumulatedFileSize, MEGABYTE);
			return CalcUtil.roundPrecision2(adjusted).toString() + " " + MEGABYTE_STRING;
		}
		if (cumulatedFileSize > KILOBYTE) {
			adjusted = CalcUtil.divide(cumulatedFileSize, KILOBYTE);
			return CalcUtil.roundPrecision2(adjusted).toString() + " " + KILOBYTE_STRING;
		}
		return cumulatedFileSize + " " + BYTE_STRING;
	}

	public static String asPrecision2(long value) {
		return asPrecision2(new Double(value));
	}

	public static String asPrecision2(Double value) {
		if (value == null) {
			return "N/A";
		}
		return CalcUtil.roundPrecision2(value).toString();
	}

	public static String handleNull(Double value) {
		if (value == null) {
			return "N/A";
		}
		return value.toString();
	}

}
