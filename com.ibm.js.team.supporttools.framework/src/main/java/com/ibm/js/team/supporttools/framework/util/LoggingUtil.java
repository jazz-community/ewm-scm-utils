/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.framework.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Some convenient tooling for logging
 *
 */
public class LoggingUtil {

	public static final String OFF = "OFF";
	public static final String FATAL = "FATAL";
	public static final String ERROR = "ERROR";
	public static final String WARN = "WARN";
	public static final String INFO = "INFO";
	public static final String DEBUG = "DEBUG";
	public static final String TRACE = "TRACE";
	public static final String ALL = "ALL";

	/**
	 * Can be used to change the logging level dynamically.
	 * 
	 * @param level the level that should be used.
	 */
	public static void setLoggingLevel(final String level) {
		Logger logger4j = Logger.getRootLogger();
		logger4j.setLevel(Level.toLevel(level));
	}

}
