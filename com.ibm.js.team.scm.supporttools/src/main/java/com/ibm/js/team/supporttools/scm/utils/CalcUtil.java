/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.utils;

public class CalcUtil {

	public static long calcMax(long a, long b) {
		if (a > b) {
			return a;
		}
		return b;
	}

	public static Double divide(long a, long b) {
		return divide(new Double(a), new Double(b));
	}

	public static Double divide(Double a, Double b) {
		if (b == 0) {
			return null;
		}
		return a / b;
	}

	public static Double roundPrecision2(Double value) {
		if (value == null) {
			return null;
		}
		Double result = Math.round(value * 100d) / 100d;
		return result;
	}
}
