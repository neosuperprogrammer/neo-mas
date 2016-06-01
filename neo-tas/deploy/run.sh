#!/bin/bash

export JAVA_HOME=""
export PATH=$JAVA_HOME/bin:$PATH
export MAS_HOME=/Users/neox/PMAS
export JAVA_OPTS="-server -classpath . -Dlog4j.configurationFile=log4j.xml -Dfile.encoding=utf-8 -Djava.net.preferIPv4Stack=true"
export MAS_OPTS="-Dmas.tas.enable=true -Dmas.was.enable=false -Dmas.tas.logging.hexdump=true -Dmas.push.debug=true"
export MAS_LAUNCHER="neo-tas-1.0-SNAPSHOT.jar"

java $JAVA_OPTS $MAS_OPTS -jar $MAS_LAUNCHER
