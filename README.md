# ewm-scm-utils

Engineering Workflow Management (EWM) SCM Utils / Rational Team Concert (RTC) SCM Utils - A collection of utility commands providing custom operations on EWM/RTC data related to a file system and EWM SCM.

- A group of SCM operations support exporting and SCM content. The export operation stores the current component content as zip files and the workspace and compoennt structure as a JSON file. The exported component data can be randomized, obfuscated or left unchanged. The data can be imported into the same or another repository using related commands.
- Another group of operations helps analyzing SCM Workspaces, Streams and sandboxes and generate sizing statistics. Includdes a framework that allows to implement own commands.
- A third group allows to share and update data in components in a stream as well as download component data into the local file system. A temporary repository workspace will be used for upload. The local data will not be a sandbox connected to Jazz SCM but remain diconnected. 

SCMUtils Version: 2.6

## Usage 
`-command commandName {[-parameter] [parameterValue]}`

The source code ships start scripts, a batch file and a sell script, that can be used to launch the tool. Please note that long running operations on large sets of data can cause out of memory exceptions. The scripts have a built in option to set the heap size, consider increasing the value if you run into out of memory errors.

The tool requires the EWM/RTC Plain Java Client Libraries. https://github.ibm.com/ralph-schoon/ewm-scm-utils/blob/master/com.ibm.js.team.scm.supporttools/ReadMe%20-%20HowToRelease.txt explains how to release the tool.

## Available commands 

### analyzeScmRepository
### analyzeSandbox
### analyzeScmWorkspace
### exportScmWorkspace
### importScmWorkspace
### convertLoadrule
### flattenLoadrule
### uploadToStream
### downloadComponentBaseline

# Command Descriptions

## analyzeScmRepository

Analyzes a RTC SCM workspace (a repository workspace or stream), the referenced components and the component substructure to provide metrics information such as number of folders, files, depth, content size and other information. The analysis data is stored in a set of Excel shets.

###	Required parameter
```bash
-command 	analyzeScmRepository
-url 		"https://<server>:port/<context>/" 
-user 	 	<userId> 
-password 	<password> 
```

###	Required parameter description
```bash 
-command 	The command to execute. 
-url 		The Public URI of the application. 
-user 	 	The user ID of a user. 
-password 	The password of the user. 
```

###	Optional parameter
```bash
-connectionOwnerScope  "<processAreaName>{&<processAreaName>}"
-outputFolder    <outputFolderPath>
```

###	Optional parameter description
```bash
-connectionOwnerScope  Filter and analyze only the connections owned by the process areas in the scope 
-outputFolder   The folder where the resulting data is written.
```
###	Example
```bash
-command analyzeScmRepository -url https://elm.example.com:9443/rm/ -user myadmin -password ****** -connectionOwnerScope "Project1 (Change Management)&Project2 (Change Management)/SCM Expert Team" -outputFolder "C:\Temp\ScmExport"
```

## analyzeSandbox
Analyses a folder and its substructure to provide metrics information such as number of folders, files, depth, content size and other information. The analysis data is stored in a set of Excel shets.

###	Required parameter
```bash
-command analyzeSandbox
-sandboxFolder <sandboxFolderPath> 
```

###	Required parameter description
```bash 
-command 	The command to execute. 
-sandboxFolder 	The folder to be analyzed.
```

##	Optional parameter
```bash
-outputFolder <outputFolderPath>
```

###	Optional parameter description
```bash
-outputFolder	The folder where the resulting data is written.
```

###	Example
```bash
-command=analyzeSandbox -sandboxFolder="C:\Temp\ExampleSandbox" -outputFolder="C:\temp\AnalyzeSandboxReport"
```
## analyzeScmWorkspace

Analyses a RTC SCM workspace connection, the referencecd components and the component substructure to provide metrics information such as number of folders, files, depth, content size and other information. The analysis data is stored in a set of Excel shets.

###	Required parameter
```bash
-command analyzeScmWorkspace
-url  "https://<server>:port/<context>/" 
-user    <userId> 
-password  <password>
-workspaceConnection  <workspaceNameOrId>
```

###	Required parameter description
```bash 
-command 	The command to execute. 
-url 		The Public URI of the application. 
-user 	 	The user ID of a user. 
-password 	  The password of the user. 
-workspaceConnection  The repository workspace to export.
```

##	Optional parameter
```bash
-outputFolder  <outputFolderPath>
```

###	Optional parameter description
```bash
-outputFolder  The folder where the resulting data is written.
```

###	Example
```bash
-command analyzeScmWorkspace -url "https://elm.example.com:9443/ccm/" -user myadmin -password ******* -workspaceConnection "JKE Banking Integration Stream Workspace" -outputFolder "C:\Temp\ScmAnalyzeWorkspace"
```

## exportScmWorkspace

Exports the contents of a workspace (a repository workspace or stream) into a set of zip files. Exports the component hierarchy structure of the workspace into a JSON file. The structure, file- and folder names are preserved, the file content can be randomized.

###	Required parameter
```bash
-command exportScmWorkspace
-url   "https://<server>:port/<context>/" 
-user   <userId> 
-password  <password>
-workspaceConnection  <workspaceNameOrId> 
```

###	Required parameter description
```bash 
-command 	The command to execute. 
-url 		The Public URI of the application. 
-user 	 	The user ID of a user. 
-password 	The password of the user. 
```

###	Optional parameter
```bash
-outputFolder  <outputFolderPath>
-exportmode  <exportmode>
```

###	Optional parameter description
```bash
-outputFolder	 The folder where the resulting data is written.
-exportmode  The mode to export the data. Available modes are: randomize, obfuscate, preserve. Default mode if parameter is omitted is: randomize
 				- Export mode randomize changes all bytes of the filecontent with random bytes.
 				- Export mode obfuscate replaces all lines in the files with lines of sample text of similar length, taken from the file CodeSampleInput.txt.
 				- Export mode preserve keeps the file content as it is.
```

###	Example
```bash
-command exportScmWorkspace -url https://elm.example.com:9443/ccm/ -user ADMIN -password ****** -workspaceConnection "Debs JKE Banking Integration Stream Workspace" -outputFolder "C:\Temp\ScmExport"
```

## importScmWorkspace

Imports a repository workspace from export data conforming to the result of the command exportScmWorkspace. Creates a repository workspace and its components from a JSON file describing the workspace component hierarchy structure. Imports the folder and file content for each component from a zip file representing the component. 

###	Required parameter

```bash
-command importScmWorkspace
-url  "https://<server>:port/<context>/" 
-user  <userId> 
-password  <password>
-projectarea  <project_area> 
-workspaceConnection  <workspaceNameOrId> 
-inputFolder  <inputFolderPath>
```
###	Required parameter description
```bash 
-command 	The command to execute. 
-url 		The Public URI of the application. 
-user 	 	The user ID of a user. 
-password 	The password of the user. 
-projectarea  A project Area name, for the project area to import into. 
-workspaceConnection  The repository workspace to import.. 
-inputFolder   The folder where the input information is expected to be. This is the folder and content created in the command exportScmWorkspace.
```

###	Optional parameter
```bash
-componentNameModifier  <modifier>
-reuseExistingWorkspace
-skipUploadingExistingComponents
```

###	Optional parameter description
```bash
-componentNameModifier  A prefix to be added to component names to force creation of new components and support component name uniqueness.
-reuseExistingWorkspace  If provided, an existing workspace is used and the configuration is overwritten by the import.
-skipUploadingExistingComponents  If provided, components that already exist are not recreated and and no data is uploaded. This allows to recover and restart after a problem. e.g. rename the component that failed and restart the import to continue.
```
###	Example
```bash
-command importScmWorkspace -url "https://elm.example.com:9443/ccm/" -user myadmin -password ******* -projectarea "JKE Banking (Change Management)" -workspaceConnection "Imported JKE Banking Stream Workspace" -inputFolder "C:\temp\ScmExport" -componentNameModifier="TestImport1_" -reuseExistingWorkspace -skipUploadingExistingComponents
```

## convertLoadrule
Convertes the component ID's in an existing Load Rule File based on the mapping created for an import using the command importScmWorkspace.

###	Required parameter
```bash
-command  convertLoadrule 
-inputFolder  <inputFolderPath> 
-sourceLoadruleFile  <sourceLoadRule> 
-targetLoadruleFile  <targetLoadRule>
```

###	Required parameter description
```bash
-command  The command to execute. 
-inputFolder  The folder where the input information is expected to be. This is the folder and content created in the command exportScmWorkspace. In addtion the command importScmWorkspace must have been ecxecuted using this folder creating the UUID mapping required. 
-sourceLoadruleFile  Full path and filename to an existing loadrule file that needs the source UUID's to be converted to the target UUID's.  
-targetLoadruleFile  Full path and filename of the resulting loadrule of the conversion.
```

### Example
```bash
-command convertLoadrule -inputFolder "C:\Temp\ScmExport" -sourceLoadruleFile "C:\Temp\example.loadrule" -targetLoadruleFile "C:\Temp\converted.loadrule"
```

## flattenLoadrule
Iterates a loadrule and modifies pathPrefix entries for sandboxRelativePath. The modification replaces all / by _ except for the first /. This creates a flat loadrule from a loadrule that has hierarcy.

###	Required parameter
```bash
-command  flattenLoadrule 
-sourceLoadruleFile  <sourceLoadRule> 
-targetLoadruleFile  <targetLoadRule> 
```
###	Required parameter description
```bash 
-sourceLoadruleFile  Full path and filename to an existing loadrule file that needs the source UUID's to be converted to the target UUID's. 
-targetLoadruleFile 	Full path and filename of the resulting loadrule of the conversion. 
```

### Example
```bash
-command sourceLoadruleFile -"C:\Temp\example.loadrule" targetLoadruleFile -"C:\Temp\converted.loadrule"
```

## uploadToStream
Uploads a folder and its content as component to a stream and baselines the content. The folder name is used as the component name. The component is created if it does not yet exists. Ownership and visibility of the component is the project area. The component is added to the stream if it is not yet in it. All changes are contained in one change set.

When a build result UUID is provided as optional parameter the command will publish the URIs for the stream, the baseline and the component as external links to the build result.

###	Required parameter
```bash
-command  uploadToStream 
-url  "https://<server>:port/<context>/" 
-user  <userId> 
-password  <password>
-projectarea  <project_area>
-streamName  <streame_name> 
-inputFolder  <inputFolder>
```

###	Required parameter description
```bash 
-command 	The command to execute. 
-url 		The Public URI of the application. 
-user 	 	The user ID of a user. 
-password 	The password of the user. 
-projectarea The name of the project area 
-streamName The name of the stream to add the component if it is not already in it.
-inputFolder The path to the folder that is the source to upload.
```

##	Optional parameter
```bash
-buildResultId  <buildResultUUID>
```

###	Optional parameter description
```bash
-buildResultId  The UUID of an existing build result.
```

### Example
```bash
-command uploadToStream -url https://clm.example.com:9443/ccm/ -user ADMIN -password ****** -projectarea "JKE Banking (Requirements Management)" -streamName "JKE Banking Integration Stream" -inputFolder "C:\Temp\ScmExport" -buildResultId _oS2f0A1iEeuxvceFG0DbZg
```

## downloadComponentBaseline
Downloads the content of a component selected by a baseline into a local file system folder. The component name is created as folder and the content of the component is loaded into that folder.

###	Required parameter
```bash
-command downloadComponentBaseline 
-url  "https://<server>:port/<context>/" 
-user    <userId> 
-password  <password>
-component  <component_name> 
-baseline  <baseline_name> 
-outputFolder  <outputFolderPath>
```

###	Required parameter description
```bash 
-command 	The command to execute. 
-url 		The Public URI of the application. 
-user 	 	The user ID of a user. 
-password 	The password of the user. 
-component  The name of the component.
-baseline  The name of the baseline on the component.
-outputFolder  The path to the folder that is the target of the download.
```

### Example
```bash
-command downloadComponentBaseline -url https://elm.example.com:9443/ccm/ -user ADMIN -password ****** -component "Test Component" -baseline "Baseline V2" -outputFolder "C:\Temp\ScmExport"
```

