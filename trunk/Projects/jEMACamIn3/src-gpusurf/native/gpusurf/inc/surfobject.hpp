#ifndef _SURFOBJECT_
#define _SURFOBJECT_

#include <math.h>
#include "ncgl.hpp"
#include "window.hpp"
#include "framebuffer.hpp"
#include "effect.hpp"
#include "image.hpp"
#include "timer.hpp"
#include "string.hpp"
#include "featuredata.hpp"
#include "keypointlist.hpp"
#include "gaussians.hpp"

#ifdef USE_CUDA
#include <cutil.h>
#include <cuda_runtime_api.h>
#include <vector_types.h>
extern "C" void 		initGPUnms(int imw_,int imh_, unsigned int oct_, unsigned int sc_, GLuint &pbo, unsigned int*& nmsdata);
extern "C" unsigned int runGPUnms(int imw_,int imh_);
extern "C" void 		destroyGPUnms();
extern "C" void 		initGPUrot(int maxnrf, GLuint &g_pbo, float*& adata);
extern "C" void 		runGPUrot(int msf,int nrf,float window);
extern "C" void 		destroyGPUrot();
#endif

class SurfObject {
public:
    SurfObject();
    ~SurfObject();
    void 			drawQuad2D();
    void 			drawQuadi2D();
    void 			createCircle();
    void 			view(ncglImage* im, int miplevel, int comp, float intensity_);
    void 			writeFeatures(FeatureData * fdats);
    void 			runCPUrot(int msf,int nrf,float window);
    unsigned int 	runCPUnms(int imw_,int imh_);
    void 			destroyCPUnms();
    void 			initCPUnms(int imw_,int imh_, unsigned int oct_, unsigned int sc_, GLuint &pbo, unsigned int*& nmsdata);
    void 			extractFeatures();
    void 			extractDescriptors();
    void 			copyKeyPoints();
    void 			loadKeyPoints(const char* filename);
    void 			run();
    void 			run(FeatureData * fd_);
    void 			display();
    void 			createShaders();
    void 			init();
    void 			loadImage(const char* new_imagefile);
    void 			loadMemoryImage(const char* format, unsigned int w, unsigned int h, unsigned char* data);
    void 			setThreshold(float thresh);
    void 			setUpSurf(bool us);
    void 			key(unsigned char key);
    void 			setVerbose(bool v);
    void 			setWindow(ncglWindow* glw_);
    void 			useFixedKeypoints(bool flag);
	void			initStorageSize(unsigned int w, unsigned int h);

    unsigned int 	nroctaves;
    unsigned int 	nrscales;
    float 			hthresh;
    unsigned int 	maxsquarefeatures;
    unsigned int 	maxfeatures;

    unsigned int 	imw,imh;
    const char* 	im_format;
    unsigned char* 	im_data;
	unsigned char* 	im_pointer;
	unsigned int   	im_data_size;


    ncglWindow* 	glw;

    unsigned int* 	nmsdata;

    KeyPointList* 	keypoints;
    KeyPointList* 	fixedkeypoints;
    GLuint 			circle;
    GLuint 			nms_pbo;
    GLuint 			rot_pbo;

    ncglEffect      *gshader,*hshader,*nshader,*pshader,*rshader,*dshader,*viewer,*normshader;

    Gaussians* 		gauss;
    unsigned int 	regionsupsamp;
    unsigned int 	windowsupsamp;
    unsigned int 	gtexsubregions;
    unsigned int 	gtexwindow;
    float 			gtex1sigma;
    float 			gtex2sigma;
    float 			gfact;

    char 			imagefile[500];

    ncglImage 		*image,*FBO1,*FBO2,*NMS,*ftex1,*gtex1,*ftex2,*gtex2,*ftex3,*ftex4;
    ncglFrameBuffer	*FBO;

    ncTimer* 		loaderTimer;
    ncTimer* 		extractionTimer;
    ncTimer* 		rotationTimer;
    ncTimer* 		descriptorTimer;

    bool 			dispgauss;
    bool 			disphess;
    float 			intensity;
    int 			displevel;
    bool 			showfeatures;
	bool 			showdescriptors;
    bool 			showmenu;
	bool 			showhelp;
    bool 			verbose;

    bool 			upsurf;
    bool 			usefixedkeypoints;

    bool 			timeractive;
};

#endif
