#ifndef __IMAGE_CONVERTER_H__
#define __IMAGE_CONVERTER_H__

#include <cvtypes.h>
#include <cv.h>

IplImage * getIplImage(int* buff, int w, int h);
int* getImagePointer(IplImage *aImage);

#endif //__IMAGE_CONVERTER_H__
