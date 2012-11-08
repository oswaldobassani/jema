/****************************************************
 * Autor:  Oswaldo                                  *
 * Versao: 0.1                                      *
 ****************************************************/

// C++ Libs
#include <ctime>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <string.h>
#include <math.h>

// OpenCV include
#include <cv.h>
#include <cvaux.h>
#include <cxcore.h>
#include <highgui.h>

#include "br_ufabc_bassani_opensurf_OpenSurf.h"

// SURF include
#include "surflib.h"
#include "kmeans.h"

// Auxiliary programs include
// #include "conversions.h"

#include "ImageConverter.h"

/*
 * Class:     br_ufabc_bassani_opensurf_OpenSurf
 * Method:    getCorrespondencePoints
 * Signature: ([I[III)[F
 */
JNIEXPORT jfloatArray JNICALL Java_br_ufabc_bassani_opensurf_OpenSurf_getCorrespondencePoints
  (JNIEnv *env, jobject jobj, jintArray image1, jintArray image2, jint w, jint h)
{
	jfloatArray arreglo;
	jfloat *jptr;

	jint *pixels1 = env->GetIntArrayElements(image1, 0);
	IplImage *img1 = getIplImage((int*)pixels1, w, h);
	env->ReleaseIntArrayElements(image1, pixels1, 0);
	
	jint *pixels2 = env->GetIntArrayElements(image2, 0);
	IplImage *img2 = getIplImage((int*)pixels2, w, h);
	env->ReleaseIntArrayElements(image2, pixels2, 0);

	/* declarations Variables */
	IpVec ipts1, ipts2; // vector points 1,2
	IpPairVec matches; // vector matches

	/* SURF Detector Features	*/
	surfDetDes(img1, ipts1, false, 4, 4, 2, 0.0006f); // image 1
	surfDetDes(img2, ipts2, false, 4, 4, 2, 0.0006f); // image 2

	/* Matches points between image 1,2	*/
	getMatches(ipts1, ipts2, matches);
	
	arreglo = env->NewFloatArray(matches.size()*4);
	jptr = env->GetFloatArrayElements(arreglo, 0);

	/* Draw line between matches points	*/
	for (unsigned int i = 0, j = 0; i < matches.size(); ++i, j+=4) {
		jptr[j+0] = matches[i].first.x;
		jptr[j+1] = matches[i].first.y;
		jptr[j+2] = matches[i].second.x;
		jptr[j+3] = matches[i].second.y;
	}

	env->ReleaseFloatArrayElements(arreglo, jptr, 0);

	return arreglo;
}

//-------------------------------------------------------

/*
 * Class:     br_ufabc_bassani_opensurf_OpenSurf
 * Method:    getCorrespondencePointsWithConfig
 * Signature: ([I[IIIZIIIF)[F
 */
JNIEXPORT jfloatArray JNICALL Java_br_ufabc_bassani_opensurf_OpenSurf_getCorrespondencePointsWithConfig
  (JNIEnv *env, jobject jobj, jintArray image1, jintArray image2, jint w, jint h, jboolean upright, jint octaves, jint intervals, jint init_sample, jfloat thres)
{
	jfloatArray arreglo;
	jfloat *jptr;

	jint *pixels1 = env->GetIntArrayElements(image1, 0);
	IplImage *img1 = getIplImage((int*)pixels1, w, h);
	env->ReleaseIntArrayElements(image1, pixels1, 0);
	
	jint *pixels2 = env->GetIntArrayElements(image2, 0);
	IplImage *img2 = getIplImage((int*)pixels2, w, h);
	env->ReleaseIntArrayElements(image2, pixels2, 0);

	/* declarations Variables */
	IpVec ipts1, ipts2; // vector points 1,2
	IpPairVec matches; // vector matches

	/* SURF Detector Features	*/
	surfDetDes(img1, ipts1, upright, octaves, intervals, init_sample, thres); // image 1
	surfDetDes(img2, ipts2, upright, octaves, intervals, init_sample, thres); // image 2

	/* Matches points between image 1,2	*/
	getMatches(ipts1, ipts2, matches);
	
	arreglo = env->NewFloatArray(matches.size()*4);
	jptr = env->GetFloatArrayElements(arreglo, 0);

	/* Draw line between matches points	*/
	for (unsigned int i = 0, j = 0; i < matches.size(); ++i, j+=4) {
		jptr[j+0] = matches[i].first.x;
		jptr[j+1] = matches[i].first.y;
		jptr[j+2] = matches[i].second.x;
		jptr[j+3] = matches[i].second.y;
	}

	env->ReleaseFloatArrayElements(arreglo, jptr, 0);

	return arreglo;
}

//-------------------------------------------------------

int count;
int width, height;
IpVec iptsLast1, iptsLast2;
bool upright;
int octaves, intervals, init_sample;
float thres;

/*
 * Class:     br_ufabc_bassani_opensurf_OpenSurf
 * Method:    reset
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_br_ufabc_bassani_opensurf_OpenSurf_reset
  (JNIEnv *env, jobject jobj, jint w, jint h)
{
	width = w;
	height = h;
}

/*
 * Class:     br_ufabc_bassani_opensurf_OpenSurf
 * Method:    configure
 * Signature: (ZIIIF)V
 */
JNIEXPORT void JNICALL Java_br_ufabc_bassani_opensurf_OpenSurf_configure
  (JNIEnv *env, jobject jobj, jboolean jupright, jint joctaves, jint jintervals, jint jinit_sample, jfloat jthres)
{
	upright = jupright;
	octaves = joctaves;
	intervals = jintervals;
	init_sample = jinit_sample;
	thres = jthres;
}

/*
 * Class:     br_ufabc_bassani_opensurf_OpenSurf
 * Method:    getCorrespondencePointsFromLoop
 * Signature: ([I)[F
 */
JNIEXPORT jfloatArray JNICALL Java_br_ufabc_bassani_opensurf_OpenSurf_getCorrespondencePointsFromLoop
  (JNIEnv *env, jobject jobj, jintArray image)
{
	jfloatArray arreglo;
	jfloat *jptr;
	
	jint *pixels = env->GetIntArrayElements(image, 0);
	IplImage *img = getIplImage((int*)pixels, width, height);
	env->ReleaseIntArrayElements(image, pixels, 0);

	/* declarations Variables */
	IpPairVec matches; // vector matches

	/* SURF Detector Features	*/
	if(count%2==0){
		surfDetDes(img, iptsLast1, upright, octaves, intervals, init_sample, thres); // image
	}else{
		surfDetDes(img, iptsLast2, upright, octaves, intervals, init_sample, thres); // image
	}
	count++;

	/* Matches points between image 1,2	*/
	getMatches(iptsLast1, iptsLast2, matches);
	
	arreglo = env->NewFloatArray(matches.size()*4);
	jptr = env->GetFloatArrayElements(arreglo, 0);

	/* Draw line between matches points	*/
	for (unsigned int i = 0, j = 0; i < matches.size(); ++i, j+=4) {
		jptr[j+0] = matches[i].first.x;
		jptr[j+1] = matches[i].first.y;
		jptr[j+2] = matches[i].second.x;
		jptr[j+3] = matches[i].second.y;
	}

	env->ReleaseFloatArrayElements(arreglo, jptr, 0);

	return arreglo;
}

//-------------------------------------------------------

IplImage* calc_dense_disparity_gray(IplImage* gray1, IplImage* gray2);

IplImage* vis_disp = NULL;
int maxdisp = 255;//100;//255;
int dense_inited = 0;
/* int* */
IplImage* calc_dense_disparity_color(IplImage* image1, IplImage* image2)
{

	cvSaveImage("image1.png", image1);
	cvSaveImage("image2.png", image2);

	IplImage* gray1 = 0;
	IplImage* gray2 = 0;

	gray1 = cvCreateImage(cvSize( image1->width,image1->height), IPL_DEPTH_8U, 1 );   
	gray2 = cvCreateImage(cvSize( image2->width,image2->height), IPL_DEPTH_8U, 1 );   
        gray1->origin = image1->origin;
        gray2->origin = image2->origin;
        cvCvtColor( image1, gray1, CV_BGR2GRAY );
        cvCvtColor( image2, gray2, CV_BGR2GRAY );

	cvSaveImage("gray1.png", gray1);
	cvSaveImage("gray2.png", gray2);
	
	return calc_dense_disparity_gray(gray1, gray2);
}

/* int* */
IplImage* calc_dense_disparity_gray(IplImage* gray1, IplImage* gray2)
{
	dense_inited = 0;
    if(!dense_inited)
    {
        //cvNamedWindow("disparity", 1);
        //cvCreateTrackbar( "maxdisp", "disparity", &maxdisp, 255, 0 );
        dense_inited = 1;
        vis_disp = cvCreateImage(cvSize(gray1->width, gray1->height), IPL_DEPTH_8U, 1);
		
		// Erro com vis_disp, forcei reinicializar para poder acessar os arrays e etc normalmente!
		// Verificar return do vis_disp e outro desalocacoes
    }

    vis_disp->origin = gray1->origin;
    cvFindStereoCorrespondence( 
                   gray1, gray2,
                   CV_DISPARITY_BIRCHFIELD,
                   vis_disp,
                   maxdisp );
    cvConvertScale(vis_disp, vis_disp, 255.f/maxdisp);
    //cvShowImage("disparity", vis_disp);

    char* outFileName = "disparity.png";
    if(!cvSaveImage(outFileName,vis_disp)) printf("Could not save: %s\n",outFileName);

    //int* img = getImagePointer(vis_disp);
    //int *img = new int[vis_disp->width*vis_disp->height];
    //memcpy(img, vis_disp->imageData, vis_disp->imageSize);
    //return img;
	//return getImagePointer(vis_disp);
	return vis_disp;
}

/*
 * Class:     br_ufabc_bassani_opensurf_OpenSurf
 * Method:    getDisparityImageFromColorImages
 * Signature: ([I[III)[I
 */
JNIEXPORT jintArray JNICALL Java_br_ufabc_bassani_opensurf_OpenSurf_getDisparityImageFromColorImages
  (JNIEnv *env, jobject jobj, jintArray image1, jintArray image2, jint w, jint h)
{
	jintArray arreglo;
	jint *jptr;

	jint *pixels1 = env->GetIntArrayElements(image1, 0);
	IplImage *img1 = getIplImage((int*)pixels1, w, h);
	env->ReleaseIntArrayElements(image1, pixels1, 0);
	
	jint *pixels2 = env->GetIntArrayElements(image2, 0);
	IplImage *img2 = getIplImage((int*)pixels2, w, h);
	env->ReleaseIntArrayElements(image2, pixels2, 0);

	/* Stereo Open CV */
	//int* disparity = calc_dense_disparity_color(img1, img2);
	/*
	arreglo = env->NewIntArray(disparity.length);
	jptr = env->GetIntArrayElements(arreglo, 0);
	for (unsigned int i = 0; i<disparity.length; i++) {
		jptr[i] = i;
	}
	env->ReleaseIntArrayElements(arreglo, jptr, 0);

	return arreglo;
*/

	IplImage *disparity_gray = calc_dense_disparity_color(img1, img2);
	//int* img = getImagePointer(disparity);

	IplImage * disparity = cvCreateImage(cvSize(w, h), IPL_DEPTH_8U, 4);
	cvCvtColor(disparity_gray, disparity, CV_GRAY2RGBA);
	cvReleaseImage(&disparity_gray);// AKI TAH O ERRO,release da variavel local!

	int *img = new int[disparity->width*disparity->height];
	memcpy(img, disparity->imageData, disparity->imageSize);
	cvReleaseImage(&disparity);

	arreglo = env->NewIntArray(w*h);
	jptr = env->GetIntArrayElements(arreglo, 0);
	
	//memcpy(jptr, getImagePointer(disparity), w*h*sizeof(int));
	memcpy(jptr, img, w*h*sizeof(int));

	cvReleaseImage(&disparity);

	env->ReleaseIntArrayElements(arreglo, jptr, 0);

	return arreglo;

}

/*
 * Class:     br_ufabc_bassani_opensurf_OpenSurf
 * Method:    getDisparityImageFromGrayScaleImages
 * Signature: ([I[III)[I
 *//*
JNIEXPORT jintArray JNICALL Java_br_ufabc_bassani_opensurf_OpenSurf_getDisparityImageFromGrayScaleImages
  (JNIEnv *env, jobject jobj, jintArray image1, jintArray image2, jint w, jint h)
{

}*/

