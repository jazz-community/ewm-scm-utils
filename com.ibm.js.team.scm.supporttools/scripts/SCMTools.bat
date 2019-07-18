@echo off
rem set JAVA_HOME=C:\PROGRA~1\Java\jre1.8.0_181
rem set JAVA_HOME=C:\Program Files (x86)\Java\jre1.8.0_191
set PLAIN_JAVA=C:\RTC606Dev\Installs\PlainJavaAPI

"%JAVA_HOME%\bin\java" -Djava.ext.dirs="%JAVA_HOME%/lib/ext;%JAVA_HOME%/jre/lib/ext;%PLAIN_JAVA%;" -cp "./scmtools_lib;%PLAIN_JAVA%;" -jar scmtools.jar %*