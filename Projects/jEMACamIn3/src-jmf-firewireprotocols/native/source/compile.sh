#!/bin/bash

#
# Autor: Oswaldo Bassani
#

# -- Variaveis de Erro -- 

ERROR_PACOTE_FALTANDO=1
ERROR_LINUX_VERSION=2
# - Outros Erros -
ERROR_COMPILACAO=3
ERROR_LINK=4
ERROR_99=99
EXEC_OK=0

# -- Funcoes Utilitarias -- 

check_linux_distribution()
{
	# Function.
	cat /etc/*-release | grep -s "Ubuntu" >/dev/null
	if [[ $? > 0 ]] ; then
		echo "Favor instalar o Ubuntu como sistema operacional"
		exit $ERROR_LINUX_VERSION
	else
		echo "Detectado 'Ubuntu'"
		cat /etc/*-release | grep -s "DISTRIB_RELEASE" | awk -F = '{print "Ubuntu version: " $2}'
		# Detalhes podem ser obtidos pelo comando: 'lsb_release -a'
		return
	fi
}

check_package()
{
	# Function.
	# Parameter 1 is the package name
	dpkg --get-selections | grep -s ${1} >/dev/null
	if [[ $? > 0 ]] ; then
		echo "Favor instalar o pacote: '${1}'"
		exit $ERROR_PACOTE_FALTANDO
	else
		echo "Pacote '${1}' instalado."
		return
	fi
}

# -- Verificando sistema operacional -- 

check_linux_distribution

# -- Pacotes do Ubuntu a serem verificados -- 

# No Ubuntu 9.04
# libdc1394-22-dev (new/default)
# libdc1394-13-dev (old)

# Java 5
# Evita erros de swing/awt.

pacotes="libraw1394-dev libdc1394-13-dev sun-java5-jdk"
for pacote in $pacotes; do
	check_package $pacote
done

#
# JAVA Vars
#
JAVA_HOME=/usr/lib/jvm/java-1.5.0-sun
JAVA_INCLUDE_DIR=$JAVA_HOME/include
JAVA_INCLUDE_LINUX_DIR=$JAVA_HOME/include/linux

# Clean
echo "Removendo binarios e libs antigos"
rm -f libFireWireCamera.so
rm -f stereoApp
rm -f libStereoFireWireCamera.so

# Build
echo "Compilando projetos"

# Codigo em C++
echo " ... libFireWireCamera ..."
g++ -Wall -o libFireWireCamera.so -shared -Wl,-soname,libFireWireCamera.so -I$JAVA_INCLUDE_DIR  -I$JAVA_INCLUDE_LINUX_DIR FireWireCamera.cpp -static -lc -lstdc++ -lraw1394 -ldc1394_control
echo " ... libFireWireCamera ... OK"

echo " ... stereoApp ..."
g++ -Wall -o stereoApp -I$JAVA_INCLUDE_DIR  -I$JAVA_INCLUDE_LINUX_DIR -Iconversions StereoFireWireCamera_App.cpp conversions/conversions.cpp -static -lc -lstdc++ -lraw1394 -ldc1394_control
echo " ... stereoApp ... OK"

echo " ... libStereoFireWireCamera ..."
g++ -Wall -o libStereoFireWireCamera.so -shared -Wl,-soname,libStereoFireWireCamera.so -I$JAVA_INCLUDE_DIR  -I$JAVA_INCLUDE_LINUX_DIR -Iconversions  StereoFireWireCamera.cpp StereoFireWireCamera_App.cpp conversions/conversions.cpp -static -lc -lstdc++ -lraw1394 -ldc1394_control
echo " ... libStereoFireWireCamera ... OK"

echo " ... libRealStereoFireWireCamera ..."
g++ -Wall -o libRealStereoFireWireCamera.so -shared -Wl,-soname,libRealStereoFireWireCamera.so -I$JAVA_INCLUDE_DIR  -I$JAVA_INCLUDE_LINUX_DIR -Iconversions  RealStereoFireWireCamera.cpp StereoFireWireCamera_App.cpp conversions/conversions.cpp -static -lc -lstdc++ -lraw1394 -ldc1394_control
echo " ... libRealStereoFireWireCamera ... OK"

# FIM

