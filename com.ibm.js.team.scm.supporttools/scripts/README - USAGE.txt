IMPORTANT

Edit the SCMUtils batch/shell script and provide a valid JDK 8 and a valid RTC Plain Java Client Libraries folder

Use Case: Exporting and persist randomized data from a repository workspace.

The use case flow is as follows:

Step 1: Export a repository workspace.
Use the command exportScmWorkspace to export a repository workspace and its data into a folder.

Example run: 
SCMUtils -command exportScmWorkspace -url "https://clm.example.com:9443/ccm/" -user myadmin -password ****** -workspaceConnection "Debs JKE Banking Integration Stream Workspace" -outputFolder "C:\Temp\ScmExport"

The command connects to the repository workspace and writes the structural information and the component content to the output folder. 
The output folder will contain
The file hierarchy.json containing information about the Repository Workspace components and their hierarchy.
For each component there is a zip file that contains the current file content. The content will be randomized.


Step 2: Import a repository workspace
Use the command importSCMWorkspace to import the exported data of a repository workspace into a new repository workspace. 

The command uses the exported information to recreate the repository workspace component structure and to load the component data into the component. A new Repository workspace will be created. Components will be created if no component with the same name exists.
The importFolder must be the outputFolder of an exportScmWorkspace command.

It is possible to run against an existing repository workspace using -reuseExistingWorkspace.
It is possible to run against the same repository and force creation of new components by using the -componentNameModifier and provide a prefix such as "ImportTest_" name prefix 

	Example: -command importScmWorkspace -url https://clm.example.com:9443/ccm/ -user ADMIN -password ****** -projectarea "JKE Banking (Requirements Management) Copy" -workspaceConnection "Debs New JKE Banking Integration Stream Workspace" -inputFolder "C:\Temp\ScmExport" -componentNameModifier "Test_Import_" -reuseExistingWorkspace

Step 3 (optional): Convert a loadrule 
If using load rules with loading a repository workspace, the component UUID needs to be replaced to work with the newly created components. Note, only component UUID's can currently be replaced. Item ID's are not yet supported.
Uses the mapping information created during the import operation to convert a loadrule. It replaces the component UUID's in loadrule by the new component UUID and saves the mofification as a new loadrule.
The converted loadrule can now be used to load the imported repository workspace. Note, Folder UUID's are not converted. Only Loadrules that use a target file path will work.

	Example: -command convertLoadrule -inputFolder "C:\Temp\ScmExport" -sourceLoadruleFile "C:\Temp\example.loadrule" -targetLoadruleFile "C:\Temp\converted.loadrule"

	