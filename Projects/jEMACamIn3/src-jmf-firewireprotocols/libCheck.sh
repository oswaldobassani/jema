#!/bin/bash

cd native/lib
nm -Ca libFireWireCamera.so | grep Java_br
nm -Ca libStereoFireWireCamera.so | grep Java_br
cd ../..

