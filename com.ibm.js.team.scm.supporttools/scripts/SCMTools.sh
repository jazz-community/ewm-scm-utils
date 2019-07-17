#!/bin/sh
#JAVA_HOME=/usr/java/jre1.8.0_181
#export JAVA_HOME

PLAIN_JAVA=/usr/RTC606Dev/Installs/PlainJavaAPI
export PLAIN_JAVA

$JAVA_HOME/bin/java -Djava.ext.dirs=./lib:$PLAIN_JAVA:$JAVA_HOME/jre/lib/ext -cp $PLAIN_JAVA%:./lib:./scmtools_lib -jar scmtools.jar "$@"