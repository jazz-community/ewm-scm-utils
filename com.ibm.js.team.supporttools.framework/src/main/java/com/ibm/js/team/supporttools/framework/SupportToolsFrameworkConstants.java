/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.framework;

/**
 * Various constants used in the framework.
 *
 */
public interface SupportToolsFrameworkConstants {

	public static final String SUPPORTTOOLSFRAMEWORK = "SUPPFW";
	public static final String FRAMEWORKVERSIONINFO = "1.0";

	// Commands and parameters
	public static final String PARAMETER_COMMAND = "command";
	public static final String PARAMETER_COMMAND_DESCRIPTION = "The command to execute.";
	public static final String PARAMETER_COMMAND_EXAMPLE = "exportConfigurations";

	public static final String PARAMETER_URL = "url";
	public static final String PARAMETER_URL_DESCRIPTION = "\tThe Public URI of the application.";
	public static final String PARAMETER_URL_EXAMPLE = "https://clm.example.com:9443/ccm/";
	public static final String PARAMETER_URL_PROTOTYPE = "\"https://<server>:port/<context>/\"";

	public static final String PARAMETER_USER = "user";
	public static final String PARAMETER_USER_ID_DESCRIPTION = "\tThe user ID of a user.";
	public static final String PARAMETER_USER_ID_EXAMPLE = "ADMIN";
	public static final String PARAMETER_USER_ID_PROTOTYPE = "<userId>";

	public static final String PARAMETER_PASSWORD = "password";
	public static final String PARAMETER_PASSWORD_DESCRIPTION = "The password of the user.";
	public static final String PARAMETER_PASSWORD_EXAMPLE = "******";
	public static final String PARAMETER_PASSWORD_PROTOTYPE = "<password>";

	public static final String PARAMETER_PROJECT_AREA = "projectarea";
	public static final String PARAMETER_PROJECT_AREA_DESCRIPTION = "A Project Area name.";
	public static final String PARAMETER_PROJECT_AREA_EXAMPLE = "\"JKE Banking (Requirements Management)\"";
	public static final String PARAMETER_PROJECT_AREA_PROTOTYPE = "<project_area>";

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
