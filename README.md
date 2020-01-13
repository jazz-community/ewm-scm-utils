# jazz-scm-content-obfuscator
Supports special SCM operation to export and obfuscate SCM content, import obfuscated content and analyse SCM Workspaces and sandboxes.

SCMTools Version: 1.8

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
```bash
-command analyzeScmRepository
-url "https://<server>:port/<context>/" 
-user <userId> 
-password <password> 
```
### Description
Analyses a RTC SCM repository streams, the referencecd components and the component substructure to provide metrics information such as number of folders, files, depth, content size and other information.

###	Optional parameter syntax
```bash
-connectionOwnerScope "<processAreaName>{&<processAreaName>}"
-outputFolder <outputFolderPath>
```

###	Optional parameter description
```bash
-connectionOwnerScope	Filter and analyze only the connections owned by the process area in the scope 
-outputFolder	The folder where the resulting data is written.
```
###	Example
```bash
-command analyzeScmRepository -url https://clm.example.com:9443/rm/ -user ADMIN -password ****** -connectionOwnerScope "Project1 (Change Management)&Project2 (Change Management)/SCM Expert Team" -outputFolder "C:\Temp\ScmExport"
```

## analyzeSandbox
```bash
-command analyzeSandbox
-url "https://<server>:port/<context>/" 
-user <userId> 
-password <password> 
-workspaceConnection <workspaceNameOrId> 
```
### Description
Analyses a folder and its substructure to provide metrics information such as number of folders, files, depth, content size and other information.

###	Syntax
```bash
-command analyzeScmRepository -url "https://<server>:port/<context>/" -user <userId> -password <password> -connectionOwnerScope <processarea1_name>[&<processarea_name>] -outputFolder <outputFolderPath>
```

###	Parameter description
```bash 
-command 	The command to execute. 
-url 		The Public URI of the application. 
-user 	 	The user ID of a user. 
-password 	The password of the user. 
```

###	Example
```bash
 -command analyzeSandbox -sandboxFolder="C:\CLM2019\6.0.6.1\workspaces\Sandboxes\Sandbox2"
```
## analyzeScmWorkspace
```bash
-command exportScmWorkspace
-url "https://<server>:port/<context>/" 
-user <userId> 
-password <password> 
-workspaceConnection <workspaceNameOrId>
-outputFolder <outputFolderPath>
```
### Description
Analyses a RTC SCM workspace connection, the referencecd components and the component substructure to provide metrics information such as number of folders, files, depth, content size and other information.

###	Syntax
```bash
-command analyzeScmWorkspace -url "https://<server>:port/<context>/" -user <userId> -password <password> -workspaceConnection <workspaceNameOrId> -outputFolder <outputFolderPath> -outputFolder
```

###	Parameter description
```bash 
-command 	 The command to execute. 
-url 	The Public URI of the application. 
-user 	 The user ID of a user. 
-password 	 The password of the user. 
-workspaceConnection 	 The repository workspace to export 
```
###	Optional parameter syntax
```bash
-outputFolder <outputFolderPath>
```

###	Optional parameter description
```bash
-outputFolder	The folder where the resulting data is written.
```

###	Example
```bash
-command analyzeScmWorkspace -url https://clm.example.com:9443/rm/ -user ADMIN -password ****** -workspaceConnection "Debs JKE Banking Integration Stream Workspace" -outputFolder "C:\Temp\ScmExport"
```

## exportScmWorkspace
```bash
-command exportScmWorkspace
-url "https://<server>:port/<context>/" 
-user <userId> 
-password <password> 
-workspaceConnection <workspaceNameOrId> 
-outputFolder <outputFolderPath>
[-exportmode <exportmode>]
```

### Description
Exports the contents of a repository workspace into a set of zip files. Exports the repository workspace component hierarchy structure into a JSON file.

###	Syntax
```bash
-command exportScmWorkspace -url "https://<server>:port/<context>/" -user <userId> -password <password> -workspaceConnection <workspaceNameOrId> -outputFolder <outputFolderPath>
```

###	Parameter description
```bash 
-command 	 The command to execute. 
-url 	The Public URI of the application. 
-user 	 The user ID of a user. 
-password 	 The password of the user. 
-workspaceConnection 	 The repository workspace to export 
-outputFolder 	 The folder where the resulting data is written.
```

###	Optional parameter syntax
```bash
-exportmode <exportmode>
```

###	Optional parameter description
```bash
-exportmode 	 The mode to export the data. Available modes are: randomize, obfuscate, preserve. Default mode if parameter is omitted is: randomize.
```

###	Example
```bash
-command exportScmWorkspace -url https://clm.example.com:9443/rm/ -user ADMIN -password ****** -workspaceConnection "Debs JKE Banking Integration Stream Workspace" -outputFolder "C:\Temp\ScmExport"
```bash

### Example optional parameter
```bash
-exportmode obfuscate
```

## importScmWorkspace
```bash
-command importScmWorkspace
-url "https://<server>:port/<context>/" 
-user <userId> 
-password <password> 
-workspaceConnection <workspaceNameOrId> 
-inputFolder <inputFolderPath>
[-componentNameModifier <modifier>]
[-reuseExistingWorkspace]
```

### Description
Creates a repository workspace and its components from a JSON file describing the workspace component hierarchy structure. Imports the folder and file content for each component from a zip file representing the component. 

### Syntax 
```bash
-command importScmWorkspace -url "https://<server>:port/<context>/" -user <userId> -password <password> -projectarea "<project_area>" -workspaceConnection <workspaceNameOrId> -inputFolder <inputFolderPath>
```

### Parameter description
```bash
-command The command to execute. 
-url The Public URI of the application. 
-user The user ID of a user. 
-password The password of the user. 
-workspaceConnection The repository workspace to export 
-outputFolder The folder where the input information is expected to be. This is the folder and content created in the command exportScmWorkspace.
```

###	Optional parameter syntax
```bash
-componentNameModifier <modifier> -reuseExistingWorkspace
 ```

### Optional parameter description 
```bash
-componentNameModifier A prefix to be added to component names to force creation of new components and support component name uniqueness. 
-reuseExistingWorkspace When providing this flag, the import operation continues if the workspace already exists. It strips the workspace from its components and adds the imported components.
```

###	Example
```bash
-command importScmWorkspace -url https://clm.example.com:9443/rm/ -user ADMIN -password ****** -projectarea "JKE Banking (Requirements Management)" -workspaceConnection "Debs JKE Banking Integration Stream Workspace" -inputFolder "C:\Temp\ScmExport"
```

### Example optional parameter
```bash
-componentNameModifier "TestImport_" -reuseExistingWorkspace
```

## convertLoadrule
```bash
-command convertLoadrule 
-inputFolder <inputFolderPath> 
-sourceLoadruleFile <sourceLoadRule> 
-targetLoadruleFile <targetLoadRule>
```

### Description
Convertes the component ID's in an existing Load Rule File based on the mapping created for an import using the command importScmWorkspace.

### Syntax

```bash
-command convertLoadrule -inputFolder <inputFolderPath> -sourceLoadruleFile <sourceLoadRule> -targetLoadruleFile <targetLoadRule> 
```

###	Parameter Description
```bash 
-inputFolder The folder where the input information is expected to be. This is the folder and content created in the command exportScmWorkspace. In addtion the command importScmWorkspace must have been ecxecuted using this folder creating the UUID mapping required. 
-sourceLoadruleFile 	Full path and filename to an existing loadrule file that needs the source UUID's to be converted to the target UUID's. 
-targetLoadruleFile Full path and filename of the resulting loadrule of the conversion.
```

### Example
```bash
-command convertLoadrule -inputFolder "C:\Temp\ScmExport" -sourceLoadruleFile "C:\Temp\example.loadrule" -targetLoadruleFile "C:\Temp\converted.loadrule"
```
## flattenLoadrule
```bash
-command flattenLoadrule 
-sourceLoadruleFile <sourceLoadRule> 
-targetLoadruleFile <targetLoadRule> 
```

### Description
Iterates a loadrule and modifies pathPrefix entries for sandboxRelativePath. The modification replaces all / by _ except for the first /. This creates a flat loadrule from a loadrule that has hierarcy.

### Syntax

```bash
-command flattenLoadrule -sourceLoadruleFile <sourceLoadRule> -targetLoadruleFile <targetLoadRule> 
```

###	Parameter Description
```bash 
-sourceLoadruleFile 	Full path and filename to an existing loadrule file that needs the source UUID's to be converted to the target UUID's. 
-targetLoadruleFile 	Full path and filename of the resulting loadrule of the conversion. 
```

### Example
```bash
-command sourceLoadruleFile -"C:\Temp\example.loadrule" targetLoadruleFile -"C:\Temp\converted.loadrule"
```
