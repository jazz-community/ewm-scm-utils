@echo off
rem set JAVA_HOME=C:\PROGRA~1\Java\jre1.8.0_181
rem set JAVA_HOME=C:\Program Files (x86)\Java\jre1.8.0_191
set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_231
set PLAIN_JAVA=C:\RTC6061Dev\installs\PlainJavaAPI

rem In the -Djava.ext.dirs always keep the Java distribution in the front
"%JAVA_HOME%\bin\java" -Djava.ext.dirs="%JAVA_HOME%/lib/ext;%JAVA_HOME%/jre/lib/ext;%PLAIN_JAVA%" -cp "./SCMUtils_lib;%PLAIN_JAVA%;" -jar SCMUtils.jar %*