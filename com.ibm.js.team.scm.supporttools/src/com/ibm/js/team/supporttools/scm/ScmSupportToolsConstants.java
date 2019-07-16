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

}
