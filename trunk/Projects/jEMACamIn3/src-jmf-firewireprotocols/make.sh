#!/bin/bash

cd native

cd source
./compile.sh
cd ..

cd ..

JAVA_HOME=/usr/lib/jvm/java-6-sun
JAVA_JAR_DIR=$JAVA_HOME/jre/lib

# MESTRADO_LIB_HOME=/home/bassani/MeusJava/Mestrado_UFABC/Mestrado_UFABC/LIBs
MESTRADO_LIB_HOME=/home/oswaldo.bassani/MeusJava/Mestrado_UFABC/LIBs

# JMF_HOME=$MESTRADO_LIB_HOME/JMF-2.1.1e/linux32
JMF_HOME=$MESTRADO_LIB_HOME/JMF/2.1.1e/linux32

CLASS_PATH=$CLASS_PATH:$JMF_HOME:$JMF_HOME/jmf.jar:$JMF_HOME/mediaplayer.jar:$JMF_HOME/multiplayer.jar
CLASS_PATH=$CLASS_PATH:$JAVA_JAR_DIR/rt.jar

mkdir -p bin
javac -sourcepath java -classpath bin:$CLASS_PATH -d bin java/br/ufabc/bassani/jmf/firewire/media/protocol/fwc/*.java
javac -sourcepath java -classpath bin:$CLASS_PATH -d bin java/br/ufabc/bassani/jmf/firewire/util/*.java

cd bin
jar -cvf fwc.jar br/ufabc/bassani/jmf/firewire/**/*
cd ..

mkdir -p dist
rm dist/fwc.jar
mv bin/fwc.jar dist/.

cp native/source/libFireWireCamera.so native/lib/.
cp native/source/libStereoFireWireCamera.so native/lib/.

