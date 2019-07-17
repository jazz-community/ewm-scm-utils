package com.ibm.js.team.supporttools.scm;

public interface ScmSupportToolsConstants {

	public static final String CMD_EXPORTWORKSPACE = "exportScmWorkspace";
	public static final String PARAMETER_WORKSPACE_NAME_OR_ID = "workspaceConnection";
	public static final String PARAMETER_WORKSPACE_PROTOTYPE = "<workspaceNameOrId>";
	public static final String PARAMETER_WORKSPACE_EXAMPLE = "\"Debs JKE Banking Integration Stream Workspace\"";
	public static final String PARAMETER_WORKSPACE_DESCRIPTION = "The repository workspace to export";
	public static final String PARAMETER_OUTPUTFOLDER = "outputFolder";
	public static final String PARAMETER_OUTPUTFOLDER_PROTOTYPE = "<outputFolderPath>";
	public static final String PARAMETER_OUTPUTFOLDER_EXAMPLE = "\"C:\\Temp\\ScmExport\"";
	public static final String PARAMETER_OUTPUTFOLDER_DESCRIPTION = "The folder where the resulting data is written.";
	public static final String CMD_IMPORTWORKSPACE = "importScmWorkspace";
	public static final String COMPONENT_CHILDREN = "Children";
	public static final String COMPONENT_UUID = "UUID";
	public static final String COMPONENT_NAME = "Name";
	public static final String HIERARCHY_JSON_FILE = "hierarchy.json";
	public static final String PARAMETER_COMPONENT_NAME_MODIFIER = "componentNameModifier";
	public static final String PARAMETER_COMPONENT_NAME_MODIFIER_PROTOTYPE = "<modifier>";
	public static final String PARAMETER_COMPONENT_NAME_MODIFIER_EXAMPLE = " Test";
	public static final String PARAMETER_COMPONENT_NAME_MODIFIER_DESCRIPTION = "A suffix to be added to component names to force creation of new components.";
	public static final String PARAMETER_INPUTFOLDER = "inputFolder";

	public static final String PARAMETER_INPUTFOLDER_PROTOTYPE = "<inputFolderPath>";
	public static final String PARAMETER_INPUTFOLDER_DESCRIPTION = "The folder where the input information is expected to be.";
	public static final String PARAMETER_INPUTFOLDER_EXAMPLE = "\"C:\\Temp\\ScmExport\"";

	public static final String PARAMETER_SOURCE_LOADRULE_FILE_PATH = "sourceLoadruleFile";
	public static final String PARAMETER_SOURCE_LOADRULE_FILE_PATH_DESCRIPTION = "Full path and filename to an existing loadrule file that needs the source UUID's to be converted to the target UUID's.";
	public static final String PARAMETER_SOURCE_LOADRULE_FILE_PATH_PROTOTYPE = "<sourceLoadRule>";
	public static final String PARAMETER_SOURCE_LOADRULE_FILE_PATH_EXAMPLE = "\"C:\\Temp\\example.loadrule\"";

	public static final String PARAMETER_TARGET_LOADRULE_FILE_PATH = "targetLoadruleFile";
	public static final String PARAMETER_TARGET_LOADRULE_FILE_PATH_DESCRIPTION = "Full path and filename of the resulting loadrule of the conversion.";
	public static final String PARAMETER_TARGET_LOADRULE_FILE_PATH_PROTOTYPE = "<targetLoadRule>";
	public static final String PARAMETER_TARGET_LOADRULE_FILE_PATH_EXAMPLE = "\"C:\\Temp\\converted.loadrule\"";

	public static final String CMD_CONVERT_LOADRULE = "convertLoadrule";
	public static final Object SOURCE_COMPONENT_UUID = "SourceUUID";
	public static final Object TARGET_COMPONENT_NAME = "TargetName";
	public static final Object TARGET_COMPONENT_UUID = "TargetUUID";
	public static final String COMPONENT_MAPPING_JSON_FILE = "UUIDMapping.json";
	public static final String PARAMETER_REUSE_EXISTING_WORKSPACE_FLAG = "reuseExistingWorkspace";
	public static final String PARAMETER_REUSE_EXISTING_WORKSPACE_FLAG_DESCRIPTION = "Full path and filename of the resulting loadrule of the conversion.";

	String DEFAULT_STORAGE_MODE_RANDOMIZE = "randomize";
	String STORAGE_MODE_OBFUSCATE = "obfuscate";
	String LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum. Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";

}
