#!/bin/bash

StartFolder=`pwd`
cd LIBs/JMF-2.1.1e/linux32

PARAM=-Dawt.toolkit=sun.awt.motif.MToolkit

java $PARAM -cp jmf.jar JMStudio

cd $StartFolder
