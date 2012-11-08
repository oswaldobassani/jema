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

pacotes="libcv-dev libcvaux-dev libgtk2.0-dev libgsl0-dev"
for pacote in $pacotes; do
	check_package $pacote
done

# -- Variaveis do Projeto -- 

source_folder="native"

prog="native/javaopensurf/javaopensurf.cpp"
prog_name="javaopensurf"

# -- Variaveis de Dependencias -- 

#
# JAVA Vars
#
JAVA_HOME=/usr/lib/jvm/java-1.5.0-sun
JAVA_INCLUDE_DIR=$JAVA_HOME/include
JAVA_INCLUDE_LINUX_DIR=$JAVA_HOME/include/linux

# OpenCV
OPENCV_INCLUDE=/usr/include/opencv
# libraw1394, libdc1394 (DEFAULT_INCLUDE)
DEFAULT_INCLUDE=/usr/include
OPENCV_LIB=/usr/lib

# -- -- 

echo "Compilando..."

#  - SURF - 

CFILES_SURF="fasthessian.cpp integral.cpp ipoint.cpp utils.cpp surf.cpp"
for arquivoC in $CFILES_SURF; do
	g++ -c -I$source_folder/opensurf -I$OPENCV_INCLUDE -I$DEFAULT_INCLUDE -Wall -g -DLINUX $source_folder/opensurf/$arquivoC
done
OBJS_SURF="fasthessian.o integral.o ipoint.o utils.o surf.o"

echo "Linkando com libs para surf ..."
# Construindo lib surf
echo "... criando surf ..."
ar ruv libsurf.a $OBJS_SURF
rm -f $OBJS_SURF

echo "Linkando..."
#  - ($prog) - 

g++ -Wall -o libjavaopensurf.so -shared -Wl,-soname,libjavaopensurf.so -L. -I$JAVA_INCLUDE_DIR  -I$JAVA_INCLUDE_LINUX_DIR -I$source_folder/javaopensurf -I$source_folder/header -I$source_folder/opensurf -I$OPENCV_INCLUDE -I$DEFAULT_INCLUDE -I../src-jcamcalib/native/source $prog ../src-jcamcalib/native/source/ImageConverter.cpp -static -lc -lstdc++ -lraw1394 -ldc1394_control -lsurf -lcxcore -lcv -lcvaux -lml -lhighgui -pthread

if [[ $? > 0 ]] ; then
	echo "Return da compilacao de '$prog' = $?"
	exit $ERROR_COMPILACAO
else
	echo "Compilado '$prog'"
fi

echo "Limpando..."

rm *.o
rm lib*.a

echo "Concluido com sucesso."

exit $EXEC_OK

# clear
# exit $ERROR_99

