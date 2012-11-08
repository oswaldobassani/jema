#!/bin/bash

StartFolder=`pwd`
cd LIBs/JMF-2.1.1e/linux32

MESTRADO_PROJ_DIR=.

java -cp jmf.jar:$MESTRADO_PROJ_DIR/jEMACamIn3/bin/ JMFRegistry

cd $StartFolder
