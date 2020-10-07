# ewm-scm-utils
A collection of utility commands providing custom SCM operations. A group of SCM operatios support exporting and obfuscating SCM content. The exported data can be imported using related commands. Another group of operations helps analyzing SCM Workspaces, Streams and sandboxes and generate sizing statistics.

SCMTools Version: 2.5

## Usage 
`-command commandName {[-parameter] [parameterValue]}`

## Available commands 

### analyzeScmRepository
### analyzeSandbox
### analyzeScmWorkspace
### exportScmWorkspace
### importScmWorkspace
### convertLoadrule
### flattenLoadrule

# Command Description

## analyzeScmRepository

Analyzes a RTC SCM workspace (a repository workspace or stream), the referenced components and the component substructure to provide metrics information such as number of folders, files, depth, content size and other information. The analysis data is stored in a set of Excel shets.

###	Required parameter
```bash
-command analyzeScmRepository
-url "https://<server>:port/<context>/" 
-user <userId> 
-password <password> 
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
-connectionOwnerScope "<processAreaName>{&<processAreaName>}"
-outputFolder <outputFolderPath>
```

###	Optional parameter description
```bash
-connectionOwnerScope	Filter and analyze only the connections owned by the process areas in the scope 
-outputFolder	The folder where the resulting data is written.
```
###	Example
```bash
-command analyzeScmRepository -url https://clm.example.com:9443/rm/ -user myadmin -password ****** -connectionOwnerScope "Project1 (Change Management)&Project2 (Change Management)/SCM Expert Team" -outputFolder "C:\Temp\ScmExport"
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
-url "https://<server>:port/<context>/" 
-user <userId> 
-password <password>
-workspaceConnection <workspaceNameOrId>
```

###	Required parameter description
```bash 
-command 	The command to execute. 
-url 		The Public URI of the application. 
-user 	 	The user ID of a user. 
-password 	The password of the user. 
-workspaceConnection  The repository workspace to export.
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
-command analyzeScmWorkspace -url "https://localhost:9443/ccm/" -user myadmin -password ******* -workspaceConnection "JKE Banking Integration Stream Workspace" -outputFolder "C:\Temp\ScmAnalyzeWorkspace"
```

## exportScmWorkspace

Exports the contents of a workspace (a repository workspace or stream) into a set of zip files. Exports the component hierarchy structure of the workspace into a JSON file. The structure, file- and folder names are preserved, the file content can be randomized.

###	Required parameter
```bash
-command exportScmWorkspace
-url "https://<server>:port/<context>/" 
-user <userId> 
-password <password>
-workspaceConnection <workspaceNameOrId> 
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
-outputFolder <outputFolderPath>
-exportmode <exportmode>
```

###	Optional parameter description
```bash
-outputFolder	 The folder where the resulting data is written.
-exportmode   The mode to export the data. Available modes are: randomize, obfuscate, preserve. Default mode if parameter is omitted is: randomize
 				- Export mode randomize changes all bytes of the filecontent with random bytes.
 				- Export mode obfuscate replaces all lines in the files with lines of sample text of similar length, taken from the file CodeSampleInput.txt.
 				- Export mode preserve keeps the file content as it is.
```

###	Example
```bash
-command exportScmWorkspace -url https://clm.example.com:9443/ccm/ -user ADMIN -password ****** -workspaceConnection "Debs JKE Banking Integration Stream Workspace" -outputFolder "C:\Temp\ScmExport"
```

## importScmWorkspace

Imports a repository workspace from export data conforming to the result of the command exportScmWorkspace. Creates a repository workspace and its components from a JSON file describing the workspace component hierarchy structure. Imports the folder and file content for each component from a zip file representing the component. 

-command importScmWorkspace -url "https://localhost:9443/ccm/" -user myadmin -password myadmin -projectarea "JKE Banking (Change Management)" -workspaceConnection "New default  Hierarchical JKE Banking Stream Workspace" -inputFolder "C:\temp\ScmExport" -componentNameModifier="IBMTestDefault_" -reuseExistingWorkspace

###	Required parameter

```bash
-command importScmWorkspace
-url "https://<server>:port/<context>/" 
-user <userId> 
-password <password>
-projectarea "<project_area>" 
-workspaceConnection <workspaceNameOrId> 
-inputFolder <inputFolderPath>
```
###	Required parameter description
```bash 
-command 	The command to execute. 
-url 		The Public URI of the application. 
-user 	 	The user ID of a user. 
-password 	The password of the user. 
-projectarea  A project Area name, for the project area to import into. 
-workspaceConnection  The repository workspace to import.. 
-inputFolder  The folder where the input information is expected to be. This is the folder and content created in the command exportScmWorkspace.
```

###	Optional parameter
```bash
-componentNameModifier <modifier>
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
-command importScmWorkspace -url "https://localhost:9443/ccm/" -user myadmin -password ******* -projectarea "JKE Banking (Change Management)" -workspaceConnection "Imported JKE Banking Stream Workspace" -inputFolder "C:\temp\ScmExport" -componentNameModifier="TestImport1_" -reuseExistingWorkspace -skipUploadingExistingComponents
```

## convertLoadrule
Convertes the component ID's in an existing Load Rule File based on the mapping created for an import using the command importScmWorkspace.

###	Required parameter
```bash
-command convertLoadrule 
-inputFolder <inputFolderPath> 
-sourceLoadruleFile <sourceLoadRule> 
-targetLoadruleFile <targetLoadRule>
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
-command flattenLoadrule 
-sourceLoadruleFile <sourceLoadRule> 
-targetLoadruleFile <targetLoadRule> 
```
###	Required parameter description
```bash 
-sourceLoadruleFile 	Full path and filename to an existing loadrule file that needs the source UUID's to be converted to the target UUID's. 
-targetLoadruleFile 	Full path and filename of the resulting loadrule of the conversion. 
```

### Example
```bash
-command sourceLoadruleFile -"C:\Temp\example.loadrule" targetLoadruleFile -"C:\Temp\converted.loadrule"
```
