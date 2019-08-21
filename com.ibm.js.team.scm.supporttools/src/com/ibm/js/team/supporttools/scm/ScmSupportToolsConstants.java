package com.ibm.js.team.supporttools.scm;

/**
 * Constants to be used in the SCM Tools
 *
 */
public interface ScmSupportToolsConstants {

	public static final String SCMTOOLS_VERSION = "1.6";

	public static final String CMD_EXPORTWORKSPACE = "exportScmWorkspace";
	public static final String CMD_EXPORTWORKSPACE_DESCRIPTION = "\n\tExports the contents of a repository workspace into a set of zip files. Exports the repository workspace component hierarchy structure into a JSON file.";
	public static final String CMD_IMPORTWORKSPACE = "importScmWorkspace";
	public static final String CMD_IMPORTWORKSPACE_DESCRIPTION = "\n\tCreates a repository workspace and its components from a JSON file describing the workspace component hierarchy structure. Imports the folder and file content for each component from a zip file representing the component. ";
	public static final String CMD_CONVERT_LOADRULE = "convertLoadrule";
	public static final String CMD_CONVERT_LOADRULE_DESCRIPTION = "\n\tConvertes the component ID's in an existing Load Rule File based on the mapping created for an import using the command "
			+ CMD_IMPORTWORKSPACE + ".";
	public static final String CMD_FLATTEN_LOADRULE = "flattenLoadrule";
	public static final String CMD_FLATTEN_LOADRULE_DESCRIPTION = "\n\tIterates a loadrule and modifies pathPrefix entries for sandboxRelativePath. The modification replaces all / by _ except for the first /. Thiscreates a flat loadrule from a loadrule that has hierarcy.";

	public static final String CMD_ANYLYZEWORKSPACECONNECTION = "analyzeScmWorkspace";
	public static final String CMD_ANALYSE_WORKSPACECONNECTION_DESCRIPTION = "\n\tAnalyses a RTC SCM workspace connection, the referencecd components and the component substructure to provide metrics information such as number of folders, files, depth, content size and other information.";

	public static final String CMD_ANYLYZESANDBOX = "analyzeSandbox";
	public static final String CMD_ANALYZESANDBOX_DESCRIPTION = "\n\tAnalyses a folder and its substructure to provide metrics information such as number of folders, files, depth, content size and other information.";;

	public static final String PARAMETER_WORKSPACE_NAME_OR_ID = "workspaceConnection";
	public static final String PARAMETER_WORKSPACE_PROTOTYPE = "<workspaceNameOrId>";
	public static final String PARAMETER_WORKSPACE_EXAMPLE = "\"Debs JKE Banking Integration Stream Workspace\"";
	public static final String PARAMETER_WORKSPACE_DESCRIPTION = "The repository workspace to export";
	public static final String PARAMETER_OUTPUTFOLDER = "outputFolder";
	public static final String PARAMETER_OUTPUTFOLDER_PROTOTYPE = "<outputFolderPath>";
	public static final String PARAMETER_OUTPUTFOLDER_EXAMPLE = "\"C:\\Temp\\ScmExport\"";
	public static final String PARAMETER_OUTPUTFOLDER_DESCRIPTION = "The folder where the resulting data is written.";
	
	
	public static final String PARAMETER_SANDBOXFOLDER = "sandboxFolder";
	public static final Object PARAMETER_SANDBOXFOLDER_PROTOTYPE = "<sandboxFolderPath>";
	public static final Object PARAMETER_SANDBOXFOLDER_EXAMPLE = "\"C:\\Temp\\sandbox\\sandboxFolder\"";
	public static final String PARAMETER_SANDBOXFOLDER_DESCRIPTION = "The folder to be analyzed.";

	public static final String PARAMETER_COMPONENT_NAME_MODIFIER = "componentNameModifier";
	public static final String PARAMETER_COMPONENT_NAME_MODIFIER_PROTOTYPE = "<modifier>";
	public static final String PARAMETER_COMPONENT_NAME_MODIFIER_EXAMPLE = "\"TestImport_\"";
	public static final String PARAMETER_COMPONENT_NAME_MODIFIER_DESCRIPTION = "A prefix to be added to component names to force creation of new components and support component name uniqueness.";
	public static final String PARAMETER_INPUTFOLDER = "inputFolder";

	public static final String PARAMETER_INPUTFOLDER_PROTOTYPE = "<inputFolderPath>";
	public static final String PARAMETER_INPUTFOLDER_DESCRIPTION = "The folder where the input information is expected to be. This is the folder and content created in the command "
			+ CMD_EXPORTWORKSPACE + ".";
	public static final String PARAMETER_INPUTFOLDER_CONVERT_DESCRIPTION = " In addtion the command "
			+ CMD_IMPORTWORKSPACE + " must have been ecxecuted using this folder creating the UUID mapping required.";
	public static final String PARAMETER_INPUTFOLDER_EXAMPLE = "\"C:\\Temp\\ScmExport\"";

	public static final String PARAMETER_SOURCE_LOADRULE_FILE_PATH = "sourceLoadruleFile";
	public static final String PARAMETER_SOURCE_LOADRULE_FILE_PATH_DESCRIPTION = "Full path and filename to an existing loadrule file that needs the source UUID's to be converted to the target UUID's.";
	public static final String PARAMETER_SOURCE_LOADRULE_FILE_PATH_PROTOTYPE = "<sourceLoadRule>";
	public static final String PARAMETER_SOURCE_LOADRULE_FILE_PATH_EXAMPLE = "\"C:\\Temp\\example.loadrule\"";

	public static final String PARAMETER_TARGET_LOADRULE_FILE_PATH = "targetLoadruleFile";
	public static final String PARAMETER_TARGET_LOADRULE_FILE_PATH_DESCRIPTION = "Full path and filename of the resulting loadrule of the conversion.";
	public static final String PARAMETER_TARGET_LOADRULE_FILE_PATH_PROTOTYPE = "<targetLoadRule>";
	public static final String PARAMETER_TARGET_LOADRULE_FILE_PATH_EXAMPLE = "\"C:\\Temp\\converted.loadrule\"";

	public static final String PARAMETER_REUSE_EXISTING_WORKSPACE_FLAG = "reuseExistingWorkspace";
	public static final String PARAMETER_REUSE_EXISTING_WORKSPACE_FLAG_DESCRIPTION = "When providing this flag, the import operation continues if the workspace already exists. It strips the workspace from its components and adds the imported components.";

	public static final String EXPORT_MODE_OBFUSCATE = "obfuscate";
	public static final String EXPORT_MODE_RANDOMIZE = "randomize";
	public static final String EXPORT_MODE_PRESERVE = "preserve";

	public static final String PARAMETER_EXPORT_MODE = "exportmode";
	public static final String PARAMETER_EXPORT_MODE_DESCRIPTION = "The mode to export the data. Available modes are: "
			+ EXPORT_MODE_RANDOMIZE + ", " + EXPORT_MODE_OBFUSCATE + ", " + EXPORT_MODE_PRESERVE
			+ ". Default mode if parameter is omitted is: " + EXPORT_MODE_RANDOMIZE;
	public static final String PARAMETER_EXPORT_MODE_PROTOTYPE = "<exportmode>";
	public static final String PARAMETER_EXPORT_MODE_EXAMPLE = EXPORT_MODE_OBFUSCATE;

	public static final String CODE_SAMPLE_INPUT_FILE_NAME = "./CodeSampleInput.txt";

	// JSON format for hierarchy export/import
	public static final String JSON_COMPONENT_CHILDREN = "Children";
	public static final String JSON_COMPONENT_UUID = "UUID";
	public static final String JSON_COMPONENT_NAME = "Name";
	public static final String HIERARCHY_JSON_FILE = "hierarchy.json";

	// JSON format for name/UUID mapping
	public static final Object JSON_SOURCE_COMPONENT_UUID = "SourceUUID";
	public static final Object JSON_TARGET_COMPONENT_NAME = "TargetName";
	public static final Object JSON_TARGET_COMPONENT_UUID = "TargetUUID";
	public static final String COMPONENT_MAPPING_JSON_FILE = "UUIDMapping.json";

}
