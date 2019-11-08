#include <stdio.h>

#include "rplidar.h"
using namespace rp::standalone::rplidar;

#include "RPLidarDriver.h"

static jfieldID drvField;

static jfieldID modelField;
static jfieldID firmwareField;
static jfieldID hardwareField;
static jfieldID serialnumField;

static jfieldID statusField;
static jfieldID error_codeField;

static jfieldID angle_z_q14Field;
static jfieldID dist_mm_q2Field;
static jfieldID qualityField;
static jfieldID flagField;

JNIEXPORT jboolean JNICALL Java_RPLidarDriver_nativeCreateDriver(JNIEnv *env, jclass, jobject o)
{
    printf("Java_RPLidarDriver_nativeCreateDriver\n");

    RPlidarDriver *drv = RPlidarDriver::CreateDriver(DRIVER_TYPE_SERIALPORT);
    if (drv == nullptr)
    {
        return JNI_FALSE;
    }
    env->SetLongField(o, drvField, reinterpret_cast<long>(drv));
    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL Java_RPLidarDriver_nativeConnect(JNIEnv *env, jobject o, jstring jport, jint baud)
{

    RPlidarDriver *drv = reinterpret_cast<RPlidarDriver *>(env->GetLongField(o, drvField));
    const char *cport = env->GetStringUTFChars(jport, nullptr);
    bool result = IS_OK(drv->connect(cport, baud));
    printf("Java_RPLidarDriver_nativeConnect(%s, %d) => %d\n", cport, baud, result);
    env->ReleaseStringUTFChars(jport, cport);
    return result ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_RPLidarDriver_nativeGetDeviceInfo(JNIEnv *env, jobject o, jobject deviceInfo)
{
    printf("Java_RPLidarDriver_nativeGetDeviceInfo 0\n");
    RPlidarDriver *drv = reinterpret_cast<RPlidarDriver *>(env->GetLongField(o, drvField));
    rplidar_response_device_info_t devInfo;
    bool result = IS_OK(drv->getDeviceInfo(devInfo));
    if (!result)
    {
        return JNI_FALSE;
    }
    env->SetByteField(deviceInfo, modelField, devInfo.model);
    env->SetShortField(deviceInfo, firmwareField, devInfo.firmware_version);
    env->SetByteField(deviceInfo, hardwareField, devInfo.hardware_version);
    jbyteArray byteArray = reinterpret_cast<jbyteArray>(env->GetObjectField(deviceInfo, serialnumField));
    jbyte *bytes = env->GetByteArrayElements(byteArray, nullptr);
    for (int i = 0; i < env->GetArrayLength(byteArray); i++)
    {
        bytes[i] = devInfo.serialnum[i];
    }

    env->ReleaseByteArrayElements(byteArray, bytes, 0);

    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL Java_RPLidarDriver_nativeGetDeviceHealth(JNIEnv *env, jobject o, jobject deviceHealth)
{
    printf("Java_RPLidarDriver_nativeGetDeviceHealth 0\n");
    RPlidarDriver *drv = reinterpret_cast<RPlidarDriver *>(env->GetLongField(o, drvField));
    rplidar_response_device_health_t devHealth;
    bool result = IS_OK(drv->getHealth(devHealth));
    env->SetByteField(deviceHealth, statusField, devHealth.status);
    env->SetShortField(deviceHealth, error_codeField, devHealth.error_code);

    return result ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_RPLidarDriver_nativeStartMotor(JNIEnv *env, jobject o)
{
    printf("Java_RPLidarDriver_nativeStartMotor\n");
    RPlidarDriver *drv = reinterpret_cast<RPlidarDriver *>(env->GetLongField(o, drvField));
    bool result = IS_OK(drv->startMotor());
    return result ? JNI_TRUE : JNI_FALSE;
}
JNIEXPORT jboolean JNICALL Java_RPLidarDriver_nativeStopMotor(JNIEnv *env, jobject o)
{
    printf("Java_RPLidarDriver_nativeStopMotor\n");
    RPlidarDriver *drv = reinterpret_cast<RPlidarDriver *>(env->GetLongField(o, drvField));
    bool result = IS_OK(drv->stopMotor());
    return result ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_RPLidarDriver_nativeReset(JNIEnv *env, jobject o)
{
    printf("Java_RPLidarDriver_nativeReset\n");
    RPlidarDriver *drv = reinterpret_cast<RPlidarDriver *>(env->GetLongField(o, drvField));
    bool result = IS_OK(drv->reset());
    return result ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_RPLidarDriver_nativeStartScan(JNIEnv *env, jobject o, jboolean force, jboolean typicalUseScan)
{
    printf("Java_RPLidarDriver_nativeStartScan\n");
    RPlidarDriver *drv = reinterpret_cast<RPlidarDriver *>(env->GetLongField(o, drvField));
    bool result = IS_OK(drv->startScan(force, typicalUseScan));
    return result ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT jboolean JNICALL Java_RPLidarDriver_nativeGrabScanDataHq(JNIEnv *env, jobject o, jobjectArray measurements)
{

    RPlidarDriver *drv = reinterpret_cast<RPlidarDriver *>(env->GetLongField(o, drvField));
    size_t count = env->GetArrayLength(measurements);

    rplidar_response_measurement_node_hq_t nodes[count];
    u_result op_result = drv->grabScanDataHq(nodes, count);

    bool result = IS_OK(op_result);
    if (!result)
    {
        printf("Java_RPLidarDriver_nativeGRabScanDataHq %x\n", op_result);
        return JNI_FALSE;
    }
    drv->ascendScanData(nodes, count);
    for (int i = 0; i < count; i++)
    {
        jobject measurement = env->GetObjectArrayElement(measurements, i);
        env->SetShortField(measurement, angle_z_q14Field, nodes[i].angle_z_q14);
        env->SetIntField(measurement, dist_mm_q2Field, nodes[i].dist_mm_q2);
        env->SetByteField(measurement, qualityField, nodes[i].quality);
        env->SetByteField(measurement, flagField, nodes[i].flag);
    }
    return JNI_TRUE;
}

JNIEXPORT jboolean JNICALL Java_RPLidarDriver_nativeStop(JNIEnv *env, jobject o)
{
    printf("Java_RPLidarDriver_nativeStop\n");
    RPlidarDriver *drv = reinterpret_cast<RPlidarDriver *>(env->GetLongField(o, drvField));
    bool result = IS_OK(drv->stop());
    return result ? JNI_TRUE : JNI_FALSE;
}

JNIEXPORT void JNICALL Java_RPLidarDriver_close(JNIEnv *env, jobject o)
{
    printf("Java_RPLidarDriver_close 0\n");
    RPlidarDriver *drv = reinterpret_cast<RPlidarDriver *>(env->GetLongField(o, drvField));
    RPlidarDriver::DisposeDriver(drv);
    env->SetLongField(o, drvField, 0);
}

JNIEXPORT jint JNI_OnLoad(JavaVM *vm, void *reserved)
{
    setbuf(stdout, NULL); //Disable buffering for printf
    printf("JNI_OnLoad\n");
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_8) != JNI_OK)
    {
        return JNI_ERR;
    }

    // Find your class. JNI_OnLoad is called from the correct class loader context for this to work.
    jclass cDriver = env->FindClass("RPLidarDriver");
    if (cDriver == nullptr)
        return JNI_ERR;

    drvField = env->GetFieldID(cDriver, "drv", "J");
    if (drvField == nullptr)
        return JNI_ERR;

    jclass cInfo = env->FindClass("DeviceInfo");
    if (cInfo == nullptr)
        return JNI_ERR;

    modelField = env->GetFieldID(cInfo, "model", "B");
    if (modelField == nullptr)
        return JNI_ERR;

    firmwareField = env->GetFieldID(cInfo, "firmwareVersion", "S");
    if (firmwareField == nullptr)
        return JNI_ERR;

    hardwareField = env->GetFieldID(cInfo, "hardwareVersion", "B");
    if (hardwareField == nullptr)
        return JNI_ERR;

    serialnumField = env->GetFieldID(cInfo, "serialnum", "[B");
    if (serialnumField == nullptr)
        return JNI_ERR;

    jclass cHealth = env->FindClass("DeviceHealth");
    if (cHealth == nullptr)
        return JNI_ERR;

    statusField = env->GetFieldID(cHealth, "status", "B");
    if (statusField == nullptr)
        return JNI_ERR;

    error_codeField = env->GetFieldID(cHealth, "errorCode", "S");
    if (error_codeField == nullptr)
        return JNI_ERR;

    jclass cMeasurement = env->FindClass("Measurement");
    if (cMeasurement == nullptr)
        return JNI_ERR;

    angle_z_q14Field = env->GetFieldID(cMeasurement, "angle_z_q14", "S");
    if (angle_z_q14Field == nullptr)
        return JNI_ERR;

    dist_mm_q2Field = env->GetFieldID(cMeasurement, "dist_mm_q2", "I");
    if (dist_mm_q2Field == nullptr)
        return JNI_ERR;

    qualityField = env->GetFieldID(cMeasurement, "quality", "B");
    if (qualityField == nullptr)
        return JNI_ERR;

    flagField = env->GetFieldID(cMeasurement, "flag", "B");
    if (flagField == nullptr)
        return JNI_ERR;

    return JNI_VERSION_1_8;
}
