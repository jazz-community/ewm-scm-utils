/*******************************************************************************
 * Copyright (c) 2015-2020 IBM Corporation
 *
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 * 
 *******************************************************************************/
package com.ibm.js.team.supporttools.scmutils;

/**
 * Constants to be used in the SCM Tools
 *
 */
public interface ScmSupportToolsConstants {

	public static final Object SCMTOOLS = "SCMUtils";
	public static final String SCMTOOLS_VERSION = "2.8";

	public static final String EXPENSIVESCENARIO_SCMTOOLS = SCMTOOLS + "_" + SCMTOOLS_VERSION + "_";

	public static final String CMD_EXPORT_WORKSPACE = "exportScmWorkspace";
	public static final String CMD_EXPORT_WORKSPACE_DESCRIPTION = "\n\tExports the contents of a workspace (a repository workspace or stream) into a set of zip files. Exports the component hierarchy structure of the workspace into a JSON file. The structure, file- and folder names are preserved, the file content can be randomized.";

	public static final String CMD_IMPORT_WORKSPACE = "importScmWorkspace";
	public static final String CMD_IMPORT_WORKSPACE_DESCRIPTION = "\n\tImports a repository workspace from export data conforming to the result of the command "+ CMD_EXPORT_WORKSPACE +". Creates a repository workspace and its components from a JSON file describing the workspace component hierarchy structure. Imports the folder and file content for each component from a zip file representing the component. ";

	public static final String CMD_CONVERT_LOADRULE = "convertLoadrule";
	public static final String CMD_CONVERT_LOADRULE_DESCRIPTION = "\n\tConvertes the component ID's in an existing Load Rule File based on the mapping created for an import using the command "
			+ CMD_IMPORT_WORKSPACE + ".";

	public static final String CMD_FLATTEN_LOADRULE = "flattenLoadrule";
	public static final String CMD_FLATTEN_LOADRULE_DESCRIPTION = "\n\tIterates a loadrule and modifies pathPrefix entries for sandboxRelativePath. The modification replaces all '/' characters by '_' except for the first '/'. This creates a flat loadrule from a loadrule that has hierarcy.";

	public static final String CMD_ANYLYZE_WORKSPACECONNECTION = "analyzeScmWorkspace";
	public static final String CMD_ANALYSE_WORKSPACECONNECTION_DESCRIPTION = "\n\tAnalyses a RTC SCM workspace (a repository workspace or stream), the referencecd components and the component substructure to provide metrics information such as number of folders, files, depth, content size and other information. The analysis data is stored in a set of Excel shets.";

	public static final String CMD_ANYLYZE_REPOSITORY = "analyzeScmRepository";
	public static final String CMD_ANYLYZE_REPOSITORY_DESCRIPTION = "\n\tAnalyzes streams in a RTC SCM repository. Each stream is analyzed, the referenced components and the component substructure to provide metrics information such as number of folders, files, depth, content size and other information. The analysis data is stored in a set of Excel shets.";

	public static final String CMD_ANYLYZE_SANDBOX = "analyzeSandbox";
	public static final String CMD_ANALYZE_SANDBOX_DESCRIPTION = "\n\tAnalyses a folder and its substructure to provide metrics information such as number of folders, files, depth, content size and other information. The analysis data is stored in a set of Excel shets.";

	public static final String CMD_UPLOAD_TO_STREAM = "uploadToStream";
	public static final String CMD_UPLOAD_TO_STREAM_DESCRIPTION = "\n\tUploads a folder and its content as component to a stream and baselines the content. The folder name is used as the component name. The component is created if it does not yet exists. Ownership and visibility of the component is the project area. The component is added to the stream if it is not yet in it. All changes are contained in one change set.";

	public static final String CMD_DOWNLOAD_COMPONENT_BASELINE = "downloadComponentBaseline";
	public static final String CMD_DOWNLOAD_COMPONENT_BASELINE_DESCRIPTION = "\n\tDownloads the content of a component selected by a baseline into a local file system folder. The component name is created as folder and the content of the component is loaded into that folder.";
	
	public static final String PARAMETER_WORKSPACE_NAME_OR_ID = "workspaceConnection";
	public static final String PARAMETER_WORKSPACE_PROTOTYPE = "<workspaceNameOrId>";
	public static final String PARAMETER_WORKSPACE_EXAMPLE = "\"Debs JKE Banking Integration Stream Workspace\"";
	public static final String PARAMETER_WORKSPACE_DESCRIPTION = "The repository workspace to export/import.";

	public static final String PARAMETER_STREAM_NAME = "streamName";
	public static final String PARAMETER_STREAM_NAME_PROTOTYPE = "<stream name>";
	public static final String PARAMETER_STREAM_NAME_EXAMPLE = "\"JKE Banking Integration Stream\"";
	public static final String PARAMETER_STREAM_NAME_DESCRIPTION = "The stream to deliver the changes.";

	
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
			+ CMD_EXPORT_WORKSPACE + ".";
	public static final String PARAMETER_INPUTFOLDER_CONVERT_DESCRIPTION = " In addtion the command "
			+ CMD_IMPORT_WORKSPACE + " must have been ecxecuted using this folder creating the required UUID mapping file.";
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
			+ ". Default mode if parameter is omitted is: " + EXPORT_MODE_RANDOMIZE
			+"\n \t\t\t\t- Export mode " + EXPORT_MODE_RANDOMIZE + " changes all bytes of the filecontent with random bytes." 
			+"\n \t\t\t\t- Export mode " + EXPORT_MODE_OBFUSCATE + " replaces all lines in the files with lines of sample text of similar length, taken from the file CodeSampleInput.txt." 
			+"\n \t\t\t\t- Export mode " + EXPORT_MODE_PRESERVE + " keeps the file content as it is." ;
	public static final String PARAMETER_EXPORT_MODE_PROTOTYPE = "<exportmode>";
	public static final String PARAMETER_EXPORT_MODE_EXAMPLE = EXPORT_MODE_OBFUSCATE;

	public static final String PARAMETER_BASELINENAME = "baseline";
	public static final String PARAMETER_BASELINENAME_DESCRIPTION = "The name of a baseline of a component.";
	public static final Object PARAMETER_BASELINENAME_PROTOTYPE = "<component name>";
	public static final Object PARAMETER_BASELINENAME_EXAMPLE = "\"Baseline V2\"";
	
	public static final String PARAMETER_COMPONENTNAME = "component";
	public static final String PARAMETER_COMPONENTNAME_DESCRIPTION = "The name of a component.";
	public static final Object PARAMETER_COMPONENTNAME_PROTOTYPE = "<component name>";
	public static final Object PARAMETER_COMPONENTNAME_EXAMPLE = "\"Test Component\"";
	
	public static final String CODE_SAMPLE_INPUT_FILE_NAME = "./CodeSampleInput.txt";

	public static final String PARAMETER_SCM_SCOPE = "connectionOwnerScope";
	public static final String PARAMETER_SCM_SCOPE_PROTOTYPE = "<processarea1_name>[&<processarea_name>]";
	public static final String PARAMETER_SCM_SCOPE_EXAMPLE = "\"Project1 (Change Management)&Project2 (Change Management)/SCM Expert Team\"";
	public static final String PARAMETER_SCM_SCOPE_DESCRIPTION = "Filter and analyze only the connections owned by the process area in the scope";

	// Flag for import workspace
	public static final String PARAMETER_SKIP_UPLOADING_EXISTING_COMPONENT_FLAG = "skipUploadingExistingComponents";
	public static final String PARAMETER_SKIP_UPLOADING_EXISTING_COMPONENT_FLAG_DESCRIPTION = "Don't reupload content to existing components. This can be used when imports of large numbers of components or folders fail to skip data that is already successfully imported.";

	// UploadToStream
	public static final String PARAMETER_BUILD_RESULT_UUID = "buildResultId";	
	public static final String PARAMETER_BUILD_RESULT_UUID_DESCRIPTION = "The UUID of an existing build result";
	public static final String PARAMETER_BUILD_RESULT_UUID_PROTOTYPE = "<buildResultUUID>";
	public static final String PARAMETER_BUILD_RESULT_UUID_EXAMPLE = "_oS2f0A1iEeuxvceFG0DbZg";

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
