#ifndef __OCV_BRIDGE_H__
#define __OCV_BRIDGE_H__

#include <cv.h>
#include <cvtypes.h>
#include <cvaux.h>
#include "ImageConverter.h"


void ocvBsetEtalon(double*);
CV_IMPL void cvStartAppendToSeq(CvSeq*, CvSeqWriter*);

#endif //__OCV_BRIDGE_H__
