/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class br_ufabc_bassani_opensurf_OpenSurf */

#ifndef _Included_br_ufabc_bassani_opensurf_OpenSurf
#define _Included_br_ufabc_bassani_opensurf_OpenSurf
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     br_ufabc_bassani_opensurf_OpenSurf
 * Method:    getCorrespondencePoints
 * Signature: ([I[III)[F
 */
JNIEXPORT jfloatArray JNICALL Java_br_ufabc_bassani_opensurf_OpenSurf_getCorrespondencePoints
  (JNIEnv *, jobject, jintArray, jintArray, jint, jint);

/*
 * Class:     br_ufabc_bassani_opensurf_OpenSurf
 * Method:    getCorrespondencePointsWithConfig
 * Signature: ([I[IIIZIIIF)[F
 */
JNIEXPORT jfloatArray JNICALL Java_br_ufabc_bassani_opensurf_OpenSurf_getCorrespondencePointsWithConfig
  (JNIEnv *, jobject, jintArray, jintArray, jint, jint, jboolean, jint, jint, jint, jfloat);

/*
 * Class:     br_ufabc_bassani_opensurf_OpenSurf
 * Method:    reset
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_br_ufabc_bassani_opensurf_OpenSurf_reset
  (JNIEnv *, jobject, jint, jint);

/*
 * Class:     br_ufabc_bassani_opensurf_OpenSurf
 * Method:    configure
 * Signature: (ZIIIF)V
 */
JNIEXPORT void JNICALL Java_br_ufabc_bassani_opensurf_OpenSurf_configure
  (JNIEnv *, jobject, jboolean, jint, jint, jint, jfloat);

/*
 * Class:     br_ufabc_bassani_opensurf_OpenSurf
 * Method:    getCorrespondencePointsFromLoop
 * Signature: ([I)[F
 */
JNIEXPORT jfloatArray JNICALL Java_br_ufabc_bassani_opensurf_OpenSurf_getCorrespondencePointsFromLoop
  (JNIEnv *, jobject, jintArray);

/*
 * Class:     br_ufabc_bassani_opensurf_OpenSurf
 * Method:    getDisparityImageFromColorImages
 * Signature: ([I[III)[I
 */
JNIEXPORT jintArray JNICALL Java_br_ufabc_bassani_opensurf_OpenSurf_getDisparityImageFromColorImages
  (JNIEnv *, jobject, jintArray, jintArray, jint, jint);

/*
 * Class:     br_ufabc_bassani_opensurf_OpenSurf
 * Method:    getDisparityImageFromGrayScaleImages
 * Signature: ([I[III)[I
 *//*
JNIEXPORT jintArray JNICALL Java_br_ufabc_bassani_opensurf_OpenSurf_getDisparityImageFromGrayScaleImages
  (JNIEnv *, jobject, jintArray, jintArray, jint, jint);*/

#ifdef __cplusplus
}
#endif
#endif
