/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.framework.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class to allow creation of printable time stamps. Conversion of .
 *
 */
public class TimeStampUtil {

	public static final String SIMPLE_DATE_FORMAT_PATTERN_YYYY_MM_DD_HH_MM_SS_Z = "yyyy/MM/dd HH:mm:ss z";
	public static final String SIMPLE_DATE_FORMAT_PATTERN_YYYY_MM_DD = "yyyy/MM/dd";

	/**
	 * Get a string representation for a timestamp in a specified pattern
	 * 
	 * @param date
	 *            A time stamp data to convert into a string.
	 * @param timeFormatPattern
	 *            A time format pattern or null (which results in a default
	 *            pattern being used)
	 * @return The string representation of the time stamp create with the
	 *         specified format pattern
	 */
	public static String getDate(final Timestamp date, final String timeFormatPattern) {
		String pattern = SIMPLE_DATE_FORMAT_PATTERN_YYYY_MM_DD_HH_MM_SS_Z;
		if (null != timeFormatPattern) {
			pattern = timeFormatPattern;
		}
		SimpleDateFormat sDFormat = new SimpleDateFormat(pattern);
		return sDFormat.format(date);
	}

	/**
	 * Get a string representation for a timestamp in a specified pattern
	 * 
	 * @param date
	 *            A time stamp data to convert into a string.
	 * @param timeFormatPattern
	 *            A time format pattern or null (which results in a default
	 *            pattern being used)
	 * @return The string representation of the time stamp create with the
	 *         specified format pattern
	 */
	public static String getDate(final Date date, final String timeFormatPattern) {
		String pattern = SIMPLE_DATE_FORMAT_PATTERN_YYYY_MM_DD_HH_MM_SS_Z;
		if (null != timeFormatPattern) {
			pattern = timeFormatPattern;
		}
		SimpleDateFormat sDFormat = new SimpleDateFormat(pattern);
		return sDFormat.format(date);
	}
	
	/**
	 * @return a current timestamp as string
	 */
	public static String getTimestamp() {
		return getDate(new Timestamp((new Date()).getTime()), null);
	}

}
