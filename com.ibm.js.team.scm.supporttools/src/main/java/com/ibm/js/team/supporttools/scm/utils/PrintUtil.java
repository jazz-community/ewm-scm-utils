/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.utils;

/**
 * Utility Class for printing/logging
 *
 */
/**
 * @author RalphSchoon
 *
 */
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

	/**
	 * Get a size based on binary magnitudes e.g. 12 GB
	 * 
	 * @param size
	 * @return
	 */
	public static String asBinaryMagnitude(long size) {
		return asBinaryMagnitude(new Double(size));
	}

	/**
	 * Get a size based on binary magnitudes e.g. 12 GB
	 * 
	 * @param size
	 * @return
	 */
	public static String asBinaryMagnitude(Double size) {
		Double adjusted;
		if (size > TERABYTE) {
			adjusted = CalcUtil.divide(size, TERABYTE);
			return CalcUtil.roundPrecision2(adjusted).toString() + " " + TERABYTE_STRING;
		}
		if (size > GIGABYTE) {
			adjusted = CalcUtil.divide(size, GIGABYTE);
			return CalcUtil.roundPrecision2(adjusted).toString() + " " + GIGABYTE_STRING;
		}
		if (size > MEGABYTE) {
			adjusted = CalcUtil.divide(size, MEGABYTE);
			return CalcUtil.roundPrecision2(adjusted).toString() + " " + MEGABYTE_STRING;
		}
		if (size > KILOBYTE) {
			adjusted = CalcUtil.divide(size, KILOBYTE);
			return CalcUtil.roundPrecision2(adjusted).toString() + " " + KILOBYTE_STRING;
		}
		return size + " " + BYTE_STRING;
	}

	/**
	 * Round a value to precision 2 and create a string. Handles null values.
	 * 
	 * @param value
	 * @return
	 */
	public static String asPrecision2(long value) {
		return asPrecision2(new Double(value));
	}

	/**
	 * Round a value to precision 2
	 * 
	 * @param value
	 * @return
	 */
	public static String asPrecision2(Double value) {
		if (value == null) {
			return "N/A";
		}
		return CalcUtil.roundPrecision2(value).toString();
	}

	/**
	 * Convert null values to N/A
	 * 
	 * @param value
	 * @return
	 */
	public static String handleNull(Double value) {
		if (value == null) {
			return "N/A";
		}
		return value.toString();
	}

	public static String getLeftAligned(double d) {
		Double data = new Double(d);
		String blanks = "                    ";
		String sValue = blanks + data.intValue();
		return sValue.substring(sValue.length() - 10);
	}

}
