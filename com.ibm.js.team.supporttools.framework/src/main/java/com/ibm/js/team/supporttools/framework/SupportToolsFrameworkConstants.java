/*******************************************************************************
 * Copyright (c) 2012 - 2013, 2018 IBM Corporation.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *
 *    Ralph Schoon - Initial implementation
 *******************************************************************************/
package com.ibm.js.team.supporttools.framework;

/**
 * Various constants used in the application.
 *
 */
public interface SupportToolsFrameworkConstants {

	public static final String FRAMEWORKVERSIONINFO = "1.0";

	// Commands and parameters
	public static final String PARAMETER_COMMAND = "command";
	public static final String PARAMETER_COMMAND_DESCRIPTION = "The command to execute.";
	public static final String PARAMETER_COMMAND_EXAMPLE = "exportConfigurations";

	public static final String PARAMETER_URL = "url";
	public static final String PARAMETER_URL_DESCRIPTION = "The Public URI of the application.";
	public static final String PARAMETER_URL_EXAMPLE = "https://clm.example.com:9443/rm/";
	public static final String PARAMETER_URL_PROTOTYPE = "https://<server>:port/<context>/";

	public static final String PARAMETER_USER = "user";
	public static final String PARAMETER_USER_ID_DESCRIPTION = "The user ID of a user.";
	public static final String PARAMETER_USER_ID_EXAMPLE = "ADMIN";
	public static final String PARAMETER_USER_PROTOTYPE = "<userId>";

	public static final String PARAMETER_PASSWORD = "password";
	public static final String PARAMETER_PASSWORD_DESCRIPTION = "The password of the user.";
	public static final String PARAMETER_PASSWORD_EXAMPLE = "******";
	public static final String PARAMETER_PASSWORD_PROTOTYPE = "<password>";

	public static final String PARAMETER_PROJECT_AREA = "projectarea";
	public static final String PARAMETER_PROJECT_AREA_DESCRIPTION = "A project Area name.";
	public static final String PARAMETER_PROJECT_AREA_EXAMPLE = "\"JKE Banking (Requirements Management)\"";
	public static final String PARAMETER_PROJECT_AREA_PROTOTYPE = "\"<project_area>\"";

	// Sample
	public static final String CMD_SAMPLE = "sampleCommand";

	public static final String PARAMETER_SAMPLE_OPTION = "mandatorySampleOption";
	public static final String PARAMETER_SAMPLE_OPTION_DESCRIPTION = "Mandatory sample option";
	public static final String PARAMETER_SAMPLE_OPTION_EXAMPLE = "\"mandatory value\"";
	public static final String PARAMETER_SAMPLE_OPTION_PROTOTYPE = "<mandatoryOptionValue>";

	public static final String PARAMETER_SAMPLE_OPTION_OPT = "optionalSampleOption";
	public static final String PARAMETER_SAMPLE_OPTION_OPT_DESCRIPTION = "Optional sample option";
	public static final String PARAMETER_SAMPLE_OPTION_OPT_EXAMPLE = "\"optional value\"";
	public static final String PARAMETER_SAMPLE_OPTION_OPT_PROTOTYPE = "<optionalOptionValue>";

}
