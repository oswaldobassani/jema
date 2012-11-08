#include <cv.h>
#include <cvtypes.h>
#include <cvaux.h>

#include "ImageConverter.h"

IplImage * getIplImage(int* buff, int w, int h)
{
	IplImage * iplImage = cvCreateImage(cvSize(w, h), IPL_DEPTH_8U, 4);
	memcpy(iplImage->imageData, buff, iplImage->imageSize );
	IplImage * result = cvCreateImage(cvSize(w, h), IPL_DEPTH_8U, 3);
	cvCvtColor(iplImage, result, CV_RGBA2RGB);
	cvReleaseImage(&iplImage);
	return result;
}


int* getImagePointer(IplImage *aImage)
{
	IplImage * iplImage = cvCreateImage(cvSize(aImage->width, aImage->height), IPL_DEPTH_8U, 4);
	cvCvtColor(aImage, iplImage, CV_RGB2RGBA);
	int *img = new int[iplImage->width*aImage->height];
	memcpy(img, iplImage->imageData, iplImage->imageSize);
	cvReleaseImage(&iplImage);
	return img;
}

