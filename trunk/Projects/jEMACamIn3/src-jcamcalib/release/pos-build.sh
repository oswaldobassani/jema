#!/bin/bash

# sudo apt-get install libcv1 libcvaux1

ln -s libocvcalib.so.1.0.0 libocvcalib.so
ln -s /usr/lib/libcv.so.1 libcv.so.0
ln -s /usr/lib/libcvaux.so.1.0.0 libcvaux.so.0
ln -s /usr/lib/libcxcore.so.1.0.0  libcxcore.so.0

