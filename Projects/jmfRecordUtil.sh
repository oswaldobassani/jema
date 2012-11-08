#!/bin/bash

StartFolder=`pwd`
cd LIBs/JMF-2.1.1e/linux32

UtilRecordFolder=UtilRecord/bin/

java -cp jmf.jar:$UtilRecordFolder -Dawt.toolkit=sun.awt.motif.MToolkit JMStudio screen://50,50,640,480/5

cd $StartFolder
