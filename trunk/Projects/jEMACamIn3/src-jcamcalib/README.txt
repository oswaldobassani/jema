JCamCalib: A Camera Calibration Utility

Info.txt

JCamCalib

JCamCalib: A Camera Calibration Utility

JCamCalib is a Java front-end for Intel's OCV library, specifically to compute camera's intrinsic parameters (focal lenght, optical center), lens distortion coefficients and homography matrix computation. http://dali.mty.itesm.mx/~hugo/thesis/JCamCalib 

0.7 (May 16, 2005)
  	chessboard-pattern.pdf
  	JCamCalib-v0_7-bin.tar.gz
  	JCamCalib-v0_7-ocv-libs.tar.gz
  	JCamCalib-v0_7-src.tar.gz
  	JCamCalib-v0_7-test-images.tar.gz

-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

How To

-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

Instalar: (Ubuntu 8.04 tem libs novas / em relacao ao linux usado para compilacao das libs)
 libcv1
 libcvaux1

$
$ ln -s libocvcalib.so.1.0.0 libocvcalib.so
$ ln -s /usr/lib/libcv.so.1 libcv.so.0
$ ln -s /usr/lib/libcvaux.so.1.0.0 libcvaux.so.0
$ ln -s /usr/lib/libcxcore.so.1.0.0  libcxcore.so.0
$ java -cp JCamCalib.jar -Djava.library.path=. itesm.gvision.apps.calibrator.JCamCalib
$

-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

Instalar: (Ubuntu 9.04)
 qt3-dev-tools

Modificar: o Makefile, incluir fontes do opencv e jvm path

> JAVA_HOME= /usr/lib/jvm/java-6-sun-1.6.0.14
> # CV_SOURCE= /usr/include/opencv/
> CV_SOURCE= /home/bassani/Aplicativos/opencv-1.0.0/cv/src - COMPILOU no 9.04
> # CV_SOURCE= /home/bassani/Aplicativos/opencv-0.9.6/cv/src -Nao compilou no 9.04
> INCPATH  = -I/usr/share/qt3/mkspecs/default -I. -I. -I/usr/include/opencv -I$(JAVA_HOME)/include/ -I$(JAVA_HOME)/include/linux/ -I$(CV_SOURCE) -I/usr/include/qt3

Aproveitar o arquivo 'cvcalibration.cpp':

 $ cp <opencv-1.0.0>/cv/src/cvcalibration.cpp .

Testando, em 'jEMACamIn/src-jcamcalib/release':
 $ export LD_LIBRARY_PATH=`pwd`
 $ ./run.sh

