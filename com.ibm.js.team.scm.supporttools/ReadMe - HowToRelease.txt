See https://github.com/jazz-community/ewm-scm-utils#readme

To start developing
1. Import the projects of the local GIT repository into Eclipse. You should see the Eclipse projects 
	- com.ibm.js.team.supporttools.scmutils
	- com.ibm.js.team.supporttools.framework
	- ewm-scm-utils 
2. In order to be able to build the project in Eclipse, make sure to install and activate a current Java 8 SDK. A JRE is not sufficient. 
	- Use Windows Preferences>Java>Installed JREs and add the JDK folder.
	- Use Windows Preferences>Java>Installed JREs>Execution Environments and configure a Java 1.8 execution environment with the JDK.
 
3. Provide the EWM/RTC plain Java client libraries. 
	- Download them from https://jazz.net.
	- Unpack the plain Java client libraries into a folder e.g. C:\ewm6.0.6.1\PlainJavaClientLibraries
	- Rightclick the project com.ibm.js.team.supporttools.scmutils and open Build Path>Configure Build path
	- Open the tab Libraries, the library PlainJavaApi is missing.
	- Click Add Library, select user library. Click the button User Library.
	- Click New name the library PlainJavaApi.
	- With the new library selected click Add External Jars. Browse to the folder C:\ewm6.0.6.1\PlainJavaClientLibraries and select all files.
	- Click Open, click OK.
	 
4. Right click the project com.ibm.js.team.supporttools.scmutils
   - Select 'Project>Clean'
   - Right click on the file pom.xml in the project folder and select 'Run as>Maven clean' 
   - Right click on the file pom.xml in the project folder and select 'Run as>Maven install' 
   - If the compiler complains about missing folders, create these folders and repeat.


 
To Build for usage

1. Open the project com.ibm.js.team.supporttools.scmutils in Eclipse. See details above. If not done already follow the steps above. 
2. Right click the project com.ibm.js.team.supporttools.framework
   - Select 'Project>Clean'
   - Right click on the file pom.xml in the project folder and select 'Run as>Maven clean' 
   - Right click on the file pom.xml in the project folder and select 'Run as>Maven install' 
3. Right click the project com.ibm.js.team.supporttools.scmutils
4. Select Export
5. Select Java>Runnable Jar File 
6. In the wizard 
   - For 'Launch Configuration' select 'SCMUtils - runnable jar'
   - In 'Export Location' select " a path e.g. 'C:\temp\SCMUtils\SCMUtils.jar' 
     You can change the root for the export if needed but keep the top folder name 
     SCMUtils and don't modify the name of the JAR file; 
   - In 'Library handling' select 'Copy required libraries into a sub-folder next to the generated JAR'
   - In the last section you can choose to save the export as an ANT script.
Click Finish and allow to create the folder.
7. Copy the content of the projects sub-folder scripts into the export location 
   folder 'C:\temp\SCMUtils\'. The files copied are script files, SCMUtils.bat and SCMUtils.sh, 
   the license file LICENSE.html and the log configuration file log4j.properties. In addition the file 
   CodeSampleInput.txt is needed.
8. Check the script files and provide a dedicated JRE 1.8 if needed
9. On Unix make the script file SCMUtils.sh you just copied executable

The application is now usable. 

To ship it 
1. Select the folder 'C:\temp\SCMUtils\' and compress the file
2. Rename the archive file to SCMUtils-Vx-YYYYMMDD.zip, 
   where x is the version, YYYY is the year, MM is the month and DD is the day
3. The file is now ready for shipping. It can basically just be uncompressed 
   on a different machine in some folder and used from there.
  