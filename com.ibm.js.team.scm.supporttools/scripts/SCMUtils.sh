#!/bin/sh
#JAVA_HOME=/usr/java/jre1.8.0_181
#export JAVA_HOME

PLAIN_JAVA=/usr/RTC606Dev/Installs/PlainJavaAPI
export PLAIN_JAVA

# In the -Djava.ext.dirs always keep the Java distribution in the front
$JAVA_HOME/bin/java -Djava.ext.dirs=$JAVA_HOME/lib/ext:$JAVA_HOME/jre/lib/ext:$PLAIN_JAVA -cp ./SCMUtils_lib:$PLAIN_JAVA%:./lib: -jar SCMUtils.jar "$@"