//----------------------------------------------------------------------------------
/**
 * Calibration rutines for JNI.
 * With code from Vasquez Dizan (INRIA Rhone-Alpes)
 * JNI Version by Hugo Ortega H.
 */
//----------------------------------------------------------------------------------

#include "itesm_gvision_apps_calibrator_CalibratorOCV.h"

#include <cv.h>
#include <cvtypes.h>
#include <cvaux.h>
#include <cxmisc.h>
#include <map>
#include <deque>
#include "ImageConverter.h"

typedef std::deque<IplImage*> ImageList;

CvStatus icvFindHomography( int numPoints,
                            CvSize imageSize,
                            CvPoint2D64d * imagePoints,
                            CvPoint2D64d * objectPoints, CvMatr64d Homography );

CvCalibFilter filter;
ImageList calibrationImages;
bool ready = false;
int cimg = 0;

//----------------------------------------------------------------------------------
/*
 * Class:     itesm_gvision_apps_calibrator_CalibratorOCV
 * Method:    initOCVCalibrator
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_itesm_gvision_apps_calibrator_CalibratorOCV_initOCVCalibrator
  (JNIEnv *env, jobject jobj)
{
	double etalonParams[] = {7, 9, 3};
	filter.SetEtalon( CV_CALIB_ETALON_CHESSBOARD, etalonParams );
}

//----------------------------------------------------------------------------------
/*
 * Class:     itesm_gvision_apps_calibrator_CalibratorOCV
 * Method:    findCorners
 * Signature: ([I)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_itesm_gvision_apps_calibrator_CalibratorOCV_findCorners
  (JNIEnv *env, jobject jobj, jintArray image, jint w, jint h)
{
	jdoubleArray arreglo;
	jdouble *jptr;

	jint *pixels = env->GetIntArrayElements(image, 0);
	IplImage *img = getIplImage((int*)pixels, w, h);
	filter.FindEtalon(&img);
	env->ReleaseIntArrayElements(image, pixels, 0);
	
	bool result = true;
	CvPoint2D32f * tempPoints = 0;
	int tempPointCount = 0;
	result = filter.GetLatestPoints(0, &tempPoints, &tempPointCount, &result);

	if (tempPointCount<2) return NULL;

	arreglo = env->NewDoubleArray(tempPointCount*2);
	jptr = env->GetDoubleArrayElements(arreglo, 0);
	
	//printf("# %d\n", tempPointCount);

	for (int j = 0, i = 0; j < tempPointCount; j++, i+=2){
		//printf("(%f, %f)\n", tempPoints[j].x, tempPoints[j].y);
		jptr[i] = tempPoints[j].x;
		jptr[i+1] = tempPoints[j].y;
	}

	env->ReleaseDoubleArrayElements(arreglo, jptr, 0);

	return arreglo;

}

//----------------------------------------------------------------------------------
/*
 * Class:     itesm_gvision_apps_calibrator_CalibratorOCV
 * Method:    addCalibrationImage
 * Signature: ([III)V
 */
JNIEXPORT void JNICALL Java_itesm_gvision_apps_calibrator_CalibratorOCV_addCalibrationImage
  (JNIEnv *env, jobject jobj, jintArray image, jint w, jint h)
{
	jint *pixels = env->GetIntArrayElements(image, 0);
	IplImage *iplimg = getIplImage((int*)pixels, w, h);
	calibrationImages.push_back(iplimg);
	if (++cimg >=3) ready = true;
}

//----------------------------------------------------------------------------------
/*
 * Class:     itesm_gvision_apps_calibrator_CalibratorOCV
 * Method:    calibrate
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_itesm_gvision_apps_calibrator_CalibratorOCV_calibrate
  (JNIEnv *env, jobject jobj)
{
	if (!ready) return false;
	bool result = true;
	filter.SetFrames(calibrationImages.size());
	for (unsigned int i = 0; (i < calibrationImages.size()) && result; i++){
		result = result && filter.FindEtalon(&(calibrationImages[i]));
		CvPoint2D32f * tempPoints = 0;
		int tempPointCount = 0;
		if (result) {
			result = filter.GetLatestPoints(0, &tempPoints, &tempPointCount, &result);
			if ( result ){
				filter.Push((const CvPoint2D32f **)&tempPoints);
			}
		}
	}
	filter.Stop(true);
	return result;
}

//----------------------------------------------------------------------------------
/*
 * Class:     itesm_gvision_apps_calibrator_CalibratorOCV
 * Method:    getResults
 * Signature: ()[D
 */
JNIEXPORT jdoubleArray JNICALL Java_itesm_gvision_apps_calibrator_CalibratorOCV_getResults
  (JNIEnv *env, jobject obj)
{
	jdoubleArray arreglo;
	jdouble *jptr;
	int i = 0;

	CvCamera *params = (CvCamera *)filter.GetCameraParams(0);
	arreglo = env->NewDoubleArray(13);
	jptr = env->GetDoubleArrayElements(arreglo, 0);
	
	for(i=0; i<9; i++) jptr[i] = params->matrix[i];
	for(i=9; i<13; i++) jptr[i] = params->distortion[i];

	env->ReleaseDoubleArrayElements(arreglo, jptr, 0);

	return arreglo;
}


//----------------------------------------------------------------------------------
/*
 * Class:     itesm_gvision_apps_calibrator_CalibratorOCV
 * Method:    getUndistortedImage
 * Signature: ([III)[I
 */
JNIEXPORT jintArray JNICALL Java_itesm_gvision_apps_calibrator_CalibratorOCV_getUndistortedImage
  (JNIEnv *env, jobject jobj, jintArray image, jint w, jint h)
{
	bool result = true;
	jintArray arreglo;
	jint *jptr;
	jint *pixels = env->GetIntArrayElements(image, 0);

	IplImage *iplImgOriginal = getIplImage((int*)pixels, w, h);

	IplImage *iplImgUndistortedImage = cvCreateImage(cvSize(w, h), IPL_DEPTH_8U, 3);
	
	result = filter.Undistort(&iplImgOriginal, &iplImgUndistortedImage);

	env->ReleaseIntArrayElements(image, pixels, 0);

	arreglo = env->NewIntArray(w*h);
	jptr = env->GetIntArrayElements(arreglo, 0);
	
	memcpy(jptr, getImagePointer(iplImgUndistortedImage), w*h*sizeof(int));

	cvReleaseImage(&iplImgUndistortedImage);

	env->ReleaseIntArrayElements(arreglo, jptr, 0);

	return arreglo;

}

//----------------------------------------------------------------------------------
/*
 * Class:     itesm_gvision_apps_calibrator_CalibratorOCV
 * Method:    getHomographyMatrix
 * Signature: ([DI[DI)[D
 */
JNIEXPORT jdoubleArray JNICALL Java_itesm_gvision_apps_calibrator_CalibratorOCV_getHomographyMatrix
  (JNIEnv *env, jobject jobj, jdoubleArray imgPoints, jdoubleArray wrldPoints, jint numPoints, jint w, jint h)
{
	jdoubleArray arreglo;
	jdouble *jptr;
	int i = 0;
	int j = 0;

	jdouble *pointsImg, *pointsWrld;

	CvPoint2D64d *imgPnts = new CvPoint2D64d[numPoints];
	CvPoint2D64d *wrldPnts = new CvPoint2D64d[numPoints];
	double homo[9];

	pointsImg = env->GetDoubleArrayElements(imgPoints, 0);
	for(i=0, j=0; i<numPoints*2; i+=2, j++){
		imgPnts[j].x = pointsImg[i];
		imgPnts[j].y = pointsImg[i+1];
	}

	pointsWrld = env->GetDoubleArrayElements(wrldPoints, 0);
	for(i=0, j=0; i<numPoints*2; i+=2, j++){
		wrldPnts[j].x = pointsWrld[i];
		wrldPnts[j].y = pointsWrld[i+1];
	}

	icvFindHomography(numPoints, cvSize(w, h), wrldPnts, imgPnts, homo);

	arreglo = env->NewDoubleArray(9);
	jptr = env->GetDoubleArrayElements(arreglo, 0);
	
	for(i=0; i<9; i++) jptr[i] = homo[i];

	env->ReleaseDoubleArrayElements(arreglo, jptr, 0);
	delete imgPnts;
	delete wrldPnts;

	return arreglo;
}

