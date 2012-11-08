#!/bin/bash

JAVA_HOME=/usr/lib/jvm/java-6-sun
JAVA_SO_DIR=$JAVA_HOME/jre/lib/i386

MESTRADO_LIB_HOME=/home/bassani/MeusJava/Mestrado_UFABC/Mestrado_UFABC/LIBs
JMF_HOME=$MESTRADO_LIB_HOME/JMF-2.1.1e/linux32

CLASS_PATH=$CLASS_PATH:$JMF_HOME:$JMF_HOME/jmf.jar:$JMF_HOME/mediaplayer.jar:$JMF_HOME/multiplayer.jar
CLASS_PATH=$CLASS_PATH:dist/fwc.jar

LD_PRELOAD_LIBRARY=$LD_PRELOAD_LIBRARY:$JAVA_SO_DIR:native/lib/.:/usr/lib:$JMF_HOME
LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$JAVA_SO_DIR:native/lib/.:/usr/lib:$JMF_HOME

export LD_PRELOAD_LIBRARY
export LD_LIBRARY_PATH

EXTRA_PARAM=-Dawt.toolkit=sun.awt.motif.MToolkit

# $EXTRA_PARAM

# /usr/lib/libraw1394.so.8

echo "CLASS_PATH utilizado $CLASS_PATH"

#java -classpath $CLASS_PATH -Djava.library.path=$LD_LIBRARY_PATH JMFInit
#java -classpath $CLASS_PATH -Djava.library.path=$LD_LIBRARY_PATH JMFRegistry

#java -classpath $CLASS_PATH -Djava.library.path=$LD_LIBRARY_PATH JMStudio fwc://

java -classpath $CLASS_PATH:../bin/ -Djava.library.path=$LD_LIBRARY_PATH br.bassani.jmf.JMFBassaniEffects

#java -classpath $CLASS_PATH -Djava.library.path=$LD_LIBRARY_PATH br.ufabc.bassani.jmf.firewire.util.FWCUtilReg

#
# http://java.sun.com/javase/technologies/desktop/media/jmf/2.1.1/apidocs/
#
