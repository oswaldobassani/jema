#!/bin/bash

# - 8.10 -
# JAVA_HOME=/usr/lib/jvm/java-6-sun-1.6.0.07
# - 9.04 -
# JAVA_HOME=/usr/lib/jvm/java-6-sun-1.6.0.14
# - Java 6 generico - (Erro no swing/awt de repaint/substituicao de componente)
# JAVA_HOME=/usr/lib/jvm/java-6-sun
# - Java 5 generico -
JAVA_HOME=/usr/lib/jvm/java-1.5.0-sun

MESTRADO_PROJ_DIR=/home/bassani/MeusJava/Mestrado_UFABC/Mestrado_UFABC

cd $MESTRADO_PROJ_DIR/DataDir

CUDA_LIB=/usr/local/cuda/lib/

# CUDA_SDK_LIB=/opt-bassani/nVidia/CUDA/2.3/NVIDIA_GPU_Computing_SDK/C/lib
# CUDA_SDK_COMMON_LIB=/opt-bassani/nVidia/CUDA/2.3/NVIDIA_GPU_Computing_SDK/C/common/lib/linux

CUDA_SDK_LIB=/opt/extrapart/oswaldo.bassani/nVidia/NVIDIA_GPU_Computing_SDK/C/lib
CUDA_SDK_COMMON_LIB=/opt/extrapart/oswaldo.bassani/nVidia/NVIDIA_GPU_Computing_SDK/C/common/lib/linux

JAVA_NATIVE_PATH=$MESTRADO_PROJ_DIR/LIBs/JMF-2.1.1e/linux32:$MESTRADO_PROJ_DIR/jEMACamIn2/src-jcamcalib/release:$MESTRADO_PROJ_DIR/jEMACamIn2/src-jmf-firewireprotocols/native/lib:/usr/lib:$MESTRADO_PROJ_DIR/jEMACamIn2/src-gpusurf:

export LD_LIBRARY_PATH=$JAVA_NATIVE_PATH:$LD_LIBRARY_PATH:$CUDA_LIB:$CUDA_SDK_LIB:$CUDA_SDK_COMMON_LIB

$JAVA_HOME/bin/java -Djava.library.path=$JAVA_NATIVE_PATH -classpath $MESTRADO_PROJ_DIR/jEMACamIn2/bin:$MESTRADO_PROJ_DIR/NyARToolkit-2.4.0/bin:$MESTRADO_PROJ_DIR/LIBs/lwjgl/2.1.0/jar/lwjgl.jar:$MESTRADO_PROJ_DIR/LIBs/jPCT-1.19/lib/jpct.jar:$MESTRADO_PROJ_DIR/LIBs/JMF/2.1.1e/linux32/jmf.jar:$MESTRADO_PROJ_DIR/LIBs/JMF/2.1.1e/linux32/mediaplayer.jar:$MESTRADO_PROJ_DIR/LIBs/JMF/2.1.1e/linux32/multiplayer.jar:$MESTRADO_PROJ_DIR/LIBs/Java3D/1.5.1/linux/i386/j3dcore.jar:$MESTRADO_PROJ_DIR/LIBs/Java3D/1.5.1/linux/i386/j3dutils.jar:$MESTRADO_PROJ_DIR/LIBs/Java3D/1.5.1/linux/i386/vecmath.jar br.ufabc.bassani.gpusurf.test.JavaGPUSurfTest

cd $MESTRADO_PROJ_DIR/jEMACamIn2

