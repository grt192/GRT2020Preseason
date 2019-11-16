/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class RPLidarDriver */

#ifndef _Included_RPLidarDriver
#define _Included_RPLidarDriver
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     RPLidarDriver
 * Method:    nativeCreateDriver
 * Signature: (LRPLidarDriver;)Z
 */
JNIEXPORT jboolean JNICALL Java_RPLidarDriver_nativeCreateDriver
  (JNIEnv *, jclass, jobject);

/*
 * Class:     RPLidarDriver
 * Method:    nativeConnect
 * Signature: (Ljava/lang/String;I)Z
 */
JNIEXPORT jboolean JNICALL Java_RPLidarDriver_nativeConnect
  (JNIEnv *, jobject, jstring, jint);

/*
 * Class:     RPLidarDriver
 * Method:    nativeGetDeviceInfo
 * Signature: (LDeviceInfo;)Z
 */
JNIEXPORT jboolean JNICALL Java_RPLidarDriver_nativeGetDeviceInfo
  (JNIEnv *, jobject, jobject);

/*
 * Class:     RPLidarDriver
 * Method:    nativeGetDeviceHealth
 * Signature: (LDeviceHealth;)Z
 */
JNIEXPORT jboolean JNICALL Java_RPLidarDriver_nativeGetDeviceHealth
  (JNIEnv *, jobject, jobject);

/*
 * Class:     RPLidarDriver
 * Method:    nativeStartMotor
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_RPLidarDriver_nativeStartMotor
  (JNIEnv *, jobject);

/*
 * Class:     RPLidarDriver
 * Method:    nativeStopMotor
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_RPLidarDriver_nativeStopMotor
  (JNIEnv *, jobject);

/*
 * Class:     RPLidarDriver
 * Method:    nativeReset
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_RPLidarDriver_nativeReset
  (JNIEnv *, jobject);

/*
 * Class:     RPLidarDriver
 * Method:    nativeStartScan
 * Signature: (ZZ)Z
 */
JNIEXPORT jboolean JNICALL Java_RPLidarDriver_nativeStartScan
  (JNIEnv *, jobject, jboolean, jboolean);

/*
 * Class:     RPLidarDriver
 * Method:    nativeGrabScanDataHq
 * Signature: ([LMeasurement;)Z
 */
JNIEXPORT jboolean JNICALL Java_RPLidarDriver_nativeGrabScanDataHq
  (JNIEnv *, jobject, jobjectArray);

/*
 * Class:     RPLidarDriver
 * Method:    nativeStop
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_RPLidarDriver_nativeStop
  (JNIEnv *, jobject);

/*
 * Class:     RPLidarDriver
 * Method:    close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_RPLidarDriver_close
  (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif
#endif