@echo off
rem set JAVA_HOME=C:\PROGRA~1\Java\jre1.8.0_181
rem set JAVA_HOME=C:\Program Files (x86)\Java\jre1.8.0_191
set PLAIN_JAVA=C:\RTC606Dev\Installs\PlainJavaAPI

"%JAVA_HOME%\bin\java" -Djava.ext.dirs="./lib;%PLAIN_JAVA%;%JAVA_HOME%/jre/lib/ext" -cp "%PLAIN_JAVA%;./lib;./scmtools_lib" -jar scmtools.jar %*