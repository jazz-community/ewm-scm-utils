/*******************************************************************************
 * Copyright (c) 2015-2019 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.statistics;

import java.text.DecimalFormat;

public class CalcUtil {

	public static long KILOBYTE = (long) Math.pow(2, 10);
	public static long MEGABYTE = (long) Math.pow(2, 20);
	public static long GIGABYTE = (long) Math.pow(2, 30);
	public static long TERABYTE = (long) Math.pow(2, 40);

	public static String divideFloatWithPrecision2AsString(long a, long b) {

		if (b == 0) {
			return "N/A"; // return "devide by zero";
		}
		return formatPrecision2(divideFloat(a, b));
	}

	public static String formatPrecision2(float num) {
		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(num);
	}

	public static String formatPrecision2(double num) {
		DecimalFormat df = new DecimalFormat("#.##");
		return df.format(num);
	}

	public static long divideLong(long a, long b) {
		return a / b;
	}

	public static float divideFloat(float a, float b) {
		return a / b;
	}

	public static String divideFloatAsString(long a, long b) {
		if (b == 0) {
			return "N/A"; // return "devide by zero";
		}
		float c = (float) a / (float) b;
		return Float.toString(c);
	}

	public static String divideLongAsString(long a, long b) {
		if (b == 0) {
			return "N/A"; // return "devide by zero";
		}
		long c = a / b;
		return String.valueOf(c);
	}

	public static long calcMax(long a, long b) {
		if (a > b) {
			return a;
		}
		return b;
	}

	public static String byBinaryMagnitudeAsString(long cumulatedFileSize) {
		if (cumulatedFileSize > TERABYTE) {
			return divideFloatWithPrecision2AsString(cumulatedFileSize, TERABYTE) + " TB";
		}
		if (cumulatedFileSize > GIGABYTE) {
			return divideFloatWithPrecision2AsString(cumulatedFileSize, GIGABYTE) + " GB";
		}
		if (cumulatedFileSize > MEGABYTE) {
			return divideFloatWithPrecision2AsString(cumulatedFileSize, MEGABYTE) + " MB";
		}
		if (cumulatedFileSize > KILOBYTE) {
			return divideFloatWithPrecision2AsString(cumulatedFileSize, KILOBYTE) + " KB";
		}
		return cumulatedFileSize + " B";
	}

}
