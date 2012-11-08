#!/bin/bash

StartFolder=`pwd`
cd LIBs/JMF-2.1.1e/linux32

java -cp jmf.jar JMFInit

cd $StartFolder
