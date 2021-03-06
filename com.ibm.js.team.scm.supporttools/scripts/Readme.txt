SCMUtils Version: 2.7

Prerequisite

- The EWM SCM Utils require an up to date working Java 8.  
- The EWM SCM Utils require the RTC/EWM Plain Java Client Libraries. 

Download the Plain Java Client Libraries from the 'All Downloads' tab of the download page for your version of EWM.
Unzip the Plain Java Client Libraries into a folder on your local disc for example C:\ELM702\PlainJavaAPI.  
The JAR files are supposed to be in this specific folder.  

To run the EWM SCM Utils, edit the SCMUtils.bat or SCMUtils.sh and change the environment variables JAVA_HOME and PLAIN_JAVA to your Java JRE 
and the Plain Java Client Libraries you installed e.g. our values e.g.

PLAIN_JAVA=C:\ELM702\PlainJavaAPI


See https://github.com/jazz-community/ewm-scm-utils#readme

Syntax: -command commandName {[-parameter] [parameterValue]}
Available commands: 

analyzeScmWorkspace

	Analyzes a RTC SCM workspace (a repository workspace or stream), the referenced components and the component substructure to provide metrics information such as number of folders, files, depth, content size and other information. The analysis data is stored in a set of Excel shets.

	Syntax: -command analyzeScmWorkspace -url "https://<server>:port/<context>/" -user <userId> -password <password> -workspaceConnection <workspaceNameOrId> -outputFolder <outputFolderPath>

	Parameter description: 
	 -command 	 The command to execute. 
	 -url 	The Public URI of the application. 
	 -user 	 The user ID of a user. 
	 -password 	 The password of the user. 
	 -workspaceConnection 	 The repository workspace to export/import. 
	 -outputFolder 	 The folder where the resulting data is written.

	Example: -command analyzeScmWorkspace -url https://clm.example.com:9443/ccm/ -user ADMIN -password ****** -workspaceConnection "Debs JKE Banking Integration Stream Workspace" -outputFolder "C:\Temp\ScmExport"


exportScmWorkspace

	Exports the contents of a workspace (a repository workspace or stream) into a set of zip files. Exports the component hierarchy structure of the workspace into a JSON file. The structure, file- and folder names are preserved, the file content can be randomized.

	Syntax: -command exportScmWorkspace -url "https://<server>:port/<context>/" -user <userId> -password <password> -workspaceConnection <workspaceNameOrId> -outputFolder <outputFolderPath>

	Parameter description: 
	 -command 	 The command to execute. 
	 -url 	The Public URI of the application. 
	 -user 	 The user ID of a user. 
	 -password 	 The password of the user. 
	 -workspaceConnection 	 The repository workspace to export/import. 
	 -outputFolder 	 The folder where the resulting data is written.

	Optional parameter syntax: -exportmode <exportmode>

	Optional parameter description: 
	 -exportmode 	 The mode to export the data. Available modes are: randomize, obfuscate, preserve. Default mode if parameter is omitted is: randomize
 				- Export mode randomize changes all bytes of the filecontent with random bytes.
 				- Export mode obfuscate replaces all lines in the files with lines of sample text of similar length, taken from the file CodeSampleInput.txt.
 				- Export mode preserve keeps the file content as it is.

	Example: -command exportScmWorkspace -url https://clm.example.com:9443/ccm/ -user ADMIN -password ****** -workspaceConnection "Debs JKE Banking Integration Stream Workspace" -outputFolder "C:\Temp\ScmExport"

	Example optional parameter: -exportmode obfuscate


importScmWorkspace

	Imports a repository workspace from export data conforming to the result of the command exportScmWorkspace. Creates a repository workspace and its components from a JSON file describing the workspace component hierarchy structure. Imports the folder and file content for each component from a zip file representing the component. 

	Syntax : -command importScmWorkspace -url "https://<server>:port/<context>/" -user <userId> -password <password> -projectarea "<project_area>" -workspaceConnection <workspaceNameOrId> -inputFolder <inputFolderPath>

	Parameter description: 
	 -command 	 The command to execute. 
	 -url 	The Public URI of the application. 
	 -user 	 The user ID of a user. 
	 -password 	 The password of the user. 
	 -projectarea 	 "<project_area>" 
	 -workspaceConnection 	 The repository workspace to export/import. 
	 -inputFolder 	 The folder where the input information is expected to be. This is the folder and content created in the command exportScmWorkspace.

	Optional parameter syntax: -componentNameModifier <modifier> -reuseExistingWorkspace -skipUploadingExistingComponents

	Optional parameter description: 
	 -componentNameModifier 	A prefix to be added to component names to force creation of new components and support component name uniqueness. 
	 -reuseExistingWorkspace 	 When providing this flag, the import operation continues if the workspace already exists. It strips the workspace from its components and adds the imported components.
	 -skipUploadingExistingComponents 	 Don't reupload content to existing components. This can be used when imports of large numbers of components or folders fail to skip data that is already successfully imported.

	Example: -command importScmWorkspace -url https://clm.example.com:9443/ccm/ -user ADMIN -password ****** -projectarea "JKE Banking (Requirements Management)" -workspaceConnection "Debs JKE Banking Integration Stream Workspace" -inputFolder "C:\Temp\ScmExport"

	Example optional parameter: -componentNameModifier "TestImport_" -reuseExistingWorkspace -skipUploadingExistingComponents


analyzeSandbox

	Analyses a folder and its substructure to provide metrics information such as number of folders, files, depth, content size and other information. The analysis data is stored in a set of Excel shets.

	Syntax: -command analyzeSandbox -sandboxFolder <sandboxFolderPath>

	Parameter description: 
	 -command 	 The command to execute. 
	 -sandboxFolder 	The folder to be analyzed.

	Example: -command analyzeSandbox -sandboxFolder "C:\Temp\sandbox\sandboxFolder"


analyzeScmRepository

	Analyses a RTC SCM repository streams, the referencecd components and the component substructure to provide metrics information such as number of folders, files, depth, content size and other information. The analysis data is stored in a set of Excel shets.

	Syntax: -command analyzeScmRepository -url "https://<server>:port/<context>/" -user <userId> -password <password> -connectionOwnerScope <processarea1_name>[&<processarea_name>] -outputFolder <outputFolderPath>

	Parameter description: 
	 -command 	 The command to execute. 
	 -url 	The Public URI of the application. 
	 -user 	 The user ID of a user. 
	 -password 	 The password of the user. 
	 -connectionOwnerScope 	 Filter and analyze only the connections owned by the process area in the scope 
	 -outputFolder 	 The folder where the resulting data is written.

	Example: -command analyzeScmRepository -url https://clm.example.com:9443/ccm/ -user ADMIN -password ****** -connectionOwnerScope "Project1 (Change Management)&Project2 (Change Management)/SCM Expert Team" -outputFolder "C:\Temp\ScmExport"


convertLoadrule

	Convertes the component ID's in an existing Load Rule File based on the mapping created for an import using the command importScmWorkspace.

	Syntax: -command convertLoadrule -inputFolder <inputFolderPath> -sourceLoadruleFile <sourceLoadRule> -targetLoadruleFile <targetLoadRule> 

	Parameter Description: 
	 -inputFolder 	 The folder where the input information is expected to be. This is the folder and content created in the command exportScmWorkspace. In addtion the command importScmWorkspace must have been ecxecuted using this folder creating the UUID mapping required. 
	 -sourceLoadruleFile 	Full path and filename to an existing loadrule file that needs the source UUID's to be converted to the target UUID's. 
	 -targetLoadruleFile 	Full path and filename of the resulting loadrule of the conversion.

	Example: -command convertLoadrule -inputFolder "C:\Temp\ScmExport" -sourceLoadruleFile "C:\Temp\example.loadrule" -targetLoadruleFile "C:\Temp\converted.loadrule"


flattenLoadrule

	Iterates a loadrule and modifies pathPrefix entries for sandboxRelativePath. The modification replaces all / by _ except for the first /. This creates a flat loadrule from a loadrule that has hierarcy.

	Syntax: -command flattenLoadrule -sourceLoadruleFile <sourceLoadRule> -targetLoadruleFile <targetLoadRule> 

	Parameter Description: 
	 -sourceLoadruleFile 	Full path and filename to an existing loadrule file that needs the source UUID's to be converted to the target UUID's. 
	 -targetLoadruleFile 	Full path and filename of the resulting loadrule of the conversion. 


	Example: -command flattenLoadrule -sourceLoadruleFile "C:\Temp\example.loadrule" -targetLoadruleFile "C:\Temp\converted.loadrule"
	