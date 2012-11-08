/****************************************************
 * Autor:  Oswaldo                                  *
 * Versao: 0.1                                      *
 ****************************************************/

// C++ Libs
#include <cstring>
#include <vector>

// OpenCV include

// SURF include
#include "surfobject.hpp"

// Auxiliary programs include

// Java
#include "br_ufabc_bassani_gpusurf_JavaGPUSurf.h"

#include "window.hpp"

SurfObject* surfer = NULL;
ncglWindow glw;

int width, height;

void display() {
    surfer->display();
    glw.swapBuffers();
}

void key(unsigned char key, int x, int y)  {
    surfer->key(key);

    switch (key) {
    case 'q':
    case '\033':
        if (surfer) {
            delete surfer;
            surfer=0;
        }
        exit(0);
    }
    glw.postRedisplay();
}

/*
 * Class:     br_ufabc_bassani_gpusurf_JavaGPUSurf
 * Method:    init
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_br_ufabc_bassani_gpusurf_JavaGPUSurf_init
  (JNIEnv *env, jobject jobj, jint w, jint h)
{
    width = w;
    height = h;

    if(surfer == NULL){
        glw.init("GPU-surf", width, height, false);

        bool quiet = false;
        bool upsurf = false;

        surfer = new SurfObject;
        surfer->setVerbose(!quiet);
        surfer->setUpSurf(upsurf);

        // glw.setDisplayFunc(display);
        // glw.setKeyboardFunc(key);

//  - Ativar para o DEBUG visual
//        glw.show();
//        glw.start();
    }

}

/*
 * Class:     br_ufabc_bassani_gpusurf_JavaGPUSurf
 * Method:    getFeatures
 * Signature: (Ljava/lang/String;[CF)[F
 */
JNIEXPORT jfloatArray JNICALL Java_br_ufabc_bassani_gpusurf_JavaGPUSurf_getFeatures
  (JNIEnv *env, jobject jobj, jstring format, jcharArray imageData, jfloat t)
{

    const char *formatType   = env->GetStringUTFChars(format,0);

    jfloatArray arreglo;
    jfloat *jptr;

    float threshold;

    threshold = t;

    jchar *pixels1 = env->GetCharArrayElements(imageData, 0);

    surfer->setThreshold(threshold);

    FeatureData* tmp = new FeatureData;

    // printf("Preparando para carregar imagem: \n");
    // printf("imagem format: %s \n", formatType);
    // printf("imagem width: %d \n", width);
    // printf("imagem height: %d \n", height);
    // printf("imagem pixels1: %s", pixels1);
//    for (unsigned int j = 0; j < 4*width*height; j++) {
//    for (int j = 0; j < width; j++) {
//        printf(" %d", pixels1[j]);
//    }

    surfer->loadMemoryImage(formatType, width, height, (unsigned char*)pixels1);

    env->ReleaseStringUTFChars(format, formatType);

    // printf("Run: \n");

    surfer->run(tmp);

// - Write data
//    tmp->write("out.surf", !quiet);
	
    env->ReleaseCharArrayElements(imageData, pixels1, 0);

    // int desclength = tmp->nrsubregions*tmp->nrsubregions*4;

    // printf("Segundo desclength: %d \n", desclength);
    // printf("Segundo nrfeatures: %d \n", surfer->fixedkeypoints->nrfeatures);
    // printf("Segundo nrf: %d \n", tmp->nrf);

	arreglo = env->NewFloatArray(tmp->nrf*4);
	jptr = env->GetFloatArrayElements(arreglo, 0);

	/* Draw line between matches points	*/
	for (unsigned int j = 0; j < tmp->nrf; j+=4) {
		jptr[j+0] = tmp->posdata[j+0];
		jptr[j+1] = tmp->posdata[j+1];
		jptr[j+2] = tmp->posdata[j+2];
		jptr[j+3] = tmp->posdata[j+3];
	}

	env->ReleaseFloatArrayElements(arreglo, jptr, 0);

	return arreglo;
}

//-------------------------------------------------------
