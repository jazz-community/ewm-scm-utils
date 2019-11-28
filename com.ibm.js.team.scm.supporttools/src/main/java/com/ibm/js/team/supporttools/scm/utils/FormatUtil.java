/*******************************************************************************
 * Copyright (c) 2015-2019 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scm.utils;

public class FormatUtil {

	public static String getLeftAligned(double d) {
		Double data = new Double(d);
		String blanks = "                    ";
		String sValue = blanks + data.intValue();
		return sValue.substring(sValue.length() - 10);
	}

}
