#include "glue.h"
#include <jni.h>
#include <memory>
#include <wooting-analog-wrapper.h>
#include <iostream>
#include <vector>

std::vector<jobject> globalRefs;

class ScopedGlobal {
public:
    ScopedGlobal() : obj(nullptr){}
    ScopedGlobal(jobject obj,JNIEnv * env) {
        this->obj = env->NewGlobalRef(obj);
        globalRefs.push_back(this->obj);
    }
    jobject obj;
};

class ScopedGlobalClass {
public:
    ScopedGlobalClass() : cls(nullptr){}
    ScopedGlobalClass(jobject obj,JNIEnv * env) {
            this->cls = (jclass)env->NewGlobalRef((jobject)obj);
            globalRefs.push_back(this->cls);
    }
    jclass cls;
};

void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
    JNIEnv* env;
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) == JNI_OK) {
        for (auto jobject : globalRefs) {
            if (jobject != nullptr) {
                std::cout<<"delete ref\n";
                env->DeleteGlobalRef(jobject);
            }
        }
    }
}

//region enum handling
#define cache_field_result(fieldName) \
{                              \
    static jfieldID field = env->GetStaticFieldID(cls.cls,#fieldName,"Lcom/github/botn365/main/WootingAnalogWrapper$WootingAnalogResult;");\
    static ScopedGlobal obj(env->GetStaticObjectField(cls.cls,field),env);\
    return obj.obj;\
}

#define cache_field_EventType(fieldName) \
{                              \
    static jfieldID field = env->GetStaticFieldID(cls.cls,#fieldName,"Lcom/github/botn365/main/WootingAnalogWrapper$WootingAnalogDeviceEventType;");\
    static ScopedGlobal obj(env->GetStaticObjectField(cls.cls,field),env);\
    return obj.obj;\
}

#define cache_field_DeviceType(fieldName) \
{                              \
    static jfieldID field = env->GetStaticFieldID(cls.cls,#fieldName,"Lcom/github/botn365/main/WootingAnalogWrapper$WootingAnalogDeviceType;");\
    static ScopedGlobal obj(env->GetStaticObjectField(cls.cls,field),env);\
    return obj.obj;\
}

jobject translateWootingResult(WootingAnalogResult result,JNIEnv * env) {
    static ScopedGlobalClass cls(env->FindClass("com/github/botn365/main/WootingAnalogWrapper$WootingAnalogResult"),env);
    switch (result) {
        case WootingAnalogResult::WootingAnalogResult_Ok:
            cache_field_result(WootingAnalogResult_Ok)
        case WootingAnalogResult::WootingAnalogResult_UnInitialized:
            cache_field_result(WootingAnalogResult_UnInitialized)
        case WootingAnalogResult::WootingAnalogResult_NoDevices:
            cache_field_result(WootingAnalogResult_NoDevices)
        case WootingAnalogResult::WootingAnalogResult_DeviceDisconnected:
            cache_field_result(WootingAnalogResult_DeviceDisconnected)
        case WootingAnalogResult::WootingAnalogResult_Failure:
            cache_field_result(WootingAnalogResult_Failure)
        case WootingAnalogResult::WootingAnalogResult_InvalidArgument:
            cache_field_result(WootingAnalogResult_InvalidArgument)
        case WootingAnalogResult::WootingAnalogResult_NoPlugins:
            cache_field_result(WootingAnalogResult_NoPlugins)
        case WootingAnalogResult::WootingAnalogResult_FunctionNotFound:
            cache_field_result(WootingAnalogResult_FunctionNotFound)
        case WootingAnalogResult::WootingAnalogResult_NoMapping:
            cache_field_result(WootingAnalogResult_NoMapping)
        case WootingAnalogResult::WootingAnalogResult_NotAvailable:
            cache_field_result(WootingAnalogResult_NotAvailable)
        case WootingAnalogResult::WootingAnalogResult_IncompatibleVersion:
            cache_field_result(WootingAnalogResult_IncompatibleVersion)
        case WootingAnalogResult::WootingAnalogResult_DLLNotFound:
            cache_field_result(WootingAnalogResult_DLLNotFound)
    }
    return nullptr;
}


jobject translateDeviceEventType(WootingAnalog_DeviceEventType event,JNIEnv * env) {
    static ScopedGlobalClass cls(env->FindClass("com/github/botn365/main/WootingAnalogWrapper$WootingAnalogDeviceEventType"),env);
    switch (event) {
        case WootingAnalog_DeviceEventType::WootingAnalog_DeviceEventType_Connected:
            cache_field_EventType(WootingAnalog_DeviceEventType_Connected)
        case WootingAnalog_DeviceEventType::WootingAnalog_DeviceEventType_Disconnected:
        cache_field_EventType(WootingAnalog_DeviceEventType_Disconnected)
    }
    return nullptr;
}

jobject translateDeviceType(WootingAnalog_DeviceType event,JNIEnv * env) {
    static ScopedGlobalClass cls(env->FindClass("com/github/botn365/main/WootingAnalogWrapper$WootingAnalogDeviceType"),env);
    switch (event) {
        case WootingAnalog_DeviceType::WootingAnalog_DeviceType_Keyboard:
            cache_field_DeviceType(WootingAnalog_DeviceType_Keyboard)
        case WootingAnalog_DeviceType::WootingAnalog_DeviceType_Keypad:
            cache_field_DeviceType(WootingAnalog_DeviceType_Keypad)
        case WootingAnalog_DeviceType::WootingAnalog_DeviceType_Other:
            cache_field_DeviceType(WootingAnalog_DeviceType_Other)
    }
    return nullptr;
}
//endregion
//region JNI Calls
jint JNICALL Java_com_github_botn365_main_WootingAnalogWrapper_wootingAnalogInitialise (JNIEnv *, jclass) {
    return wooting_analog_initialise();
}

jboolean JNICALL Java_com_github_botn365_main_WootingAnalogWrapper_wootingAnalogIsInitialised (JNIEnv *, jclass) {
    return wooting_analog_is_initialised();
}

jobject JNICALL Java_com_github_botn365_main_WootingAnalogWrapper_wootingAnalogUnisialise (JNIEnv * env, jclass) {
    return translateWootingResult(wooting_analog_uninitialise(),env);
}

jobject JNICALL Java_com_github_botn365_main_WootingAnalogWrapper_wootingAnalogSetKeycode (JNIEnv * env, jclass cls, jint mode) {
    return translateWootingResult(wooting_analog_set_keycode_mode((WootingAnalog_KeycodeType)mode),env);
}

jfloat JNICALL Java_com_github_botn365_main_WootingAnalogWrapper_wootingAnalogReadAnalog (JNIEnv * env, jclass, jint keyCode) {
    return wooting_analog_read_analog(keyCode);
}

jfloat JNICALL Java_com_github_botn365_main_WootingAnalogWrapper_wootingAnalogReadAnalogDevice (JNIEnv * env, jclass, jint keyCode, jlong deviceId){
    return wooting_analog_read_analog_device(keyCode,(unsigned long)deviceId);
}

jint JNICALL Java_com_github_botn365_main_WootingAnalogWrapper_wootingAnalogReadFullBuffer (JNIEnv * env, jclass, jshortArray keyCodes, jfloatArray floatValues) {
    jsize keyCodeLen = env->GetArrayLength(keyCodes);
    jsize floatLen =  env->GetArrayLength( floatValues);
    jsize min;
    if (keyCodeLen > floatLen) {
        min = floatLen;
    } else {
        min = keyCodeLen;
    }
    printf("min=%i",min);
    jshort * keyCodesArray = env->GetShortArrayElements(keyCodes,NULL);
    jfloat * floatArray = env->GetFloatArrayElements(floatValues,NULL);
    int len = wooting_analog_read_full_buffer((unsigned short *)keyCodesArray,floatArray,min);
    env->ReleaseShortArrayElements(keyCodes,keyCodesArray,0);
    env->ReleaseFloatArrayElements(floatValues,floatArray,0);
    return len;
}

jint JNICALL Java_com_github_botn365_main_WootingAnalogWrapper_wootingAnalogReadFullBufferDevice (JNIEnv * env, jclass, jshortArray keyCodes, jfloatArray floatValues, jlong deviceId) {
    jsize keyCodeLen = env->GetArrayLength(keyCodes);
    jsize floatLen = env->GetArrayLength(floatValues);
    jsize min;
    if (keyCodeLen > floatLen) {
        min = floatLen;
    } else {
        min = keyCodeLen;
    }
    printf("min=%i",min);
    jshort * keyCodesArray = env->GetShortArrayElements(keyCodes,NULL);
    jfloat * floatArray = env->GetFloatArrayElements(floatValues,NULL);
    int len = wooting_analog_read_full_buffer_device((unsigned short *)keyCodesArray,floatArray,min,deviceId);
    env->ReleaseShortArrayElements(keyCodes,keyCodesArray,0);
    env->ReleaseFloatArrayElements(floatValues,floatArray,0);
    return len;
}

jobject newDeviceEFI(WootingAnalog_DeviceInfo_FFI * device,JNIEnv * env) {
    static ScopedGlobalClass cls(env->FindClass("com/github/botn365/main/WootingAnalogWrapper$WootingAnalogDeviceInfoFFI"),env);
    static jmethodID method = env->GetMethodID(cls.cls,"<init>","(IILjava/lang/String;Ljava/lang/String;JLcom/github/botn365/main/WootingAnalogWrapper$WootingAnalogDeviceType;)V");
    auto &d = *device;
    jstring manufacturer = env->NewStringUTF(d.manufacturer_name);
    jstring deviceName = env->NewStringUTF(d.device_name);
    jobject deviceType = translateDeviceType(d.device_type,env);
    return env->NewObject(cls.cls,method,d.vendor_id,d.product_id,manufacturer,deviceName,d.device_id,deviceType);
}

jint JNICALL Java_com_github_botn365_main_WootingAnalogWrapper_wootingAnalogGetConnectedDevicesInfo (JNIEnv * env, jclass, jobjectArray deviceArray) {
    int len = env->GetArrayLength(deviceArray);
    if (len > 0) {
        std::unique_ptr<WootingAnalog_DeviceInfo_FFI*[]> devices =  std::make_unique<WootingAnalog_DeviceInfo_FFI*[]>(len);
        for (int i = 0; i < len; ++i) {
            devices[i] = nullptr;
        }
        int devicesCount = wooting_analog_get_connected_devices_info(devices.get(),len);
        for (int i = 0; i < devicesCount; ++i) {
            if (devices[i] == nullptr) {
                env->SetObjectArrayElement(deviceArray,i, nullptr);
            } else {
                jobject obj = newDeviceEFI(devices[i],env);
                env->SetObjectArrayElement(deviceArray,i,obj);
            }
        }
        return devicesCount;
    }
    return 0;
}

jmethodID callbackMethod = nullptr;
jobject callbackObj = nullptr;
JavaVM * vm;

JNIEXPORT jobject JNICALL Java_com_github_botn365_main_WootingAnalogWrapper_wootingAnalogSetDeviceEventCb (JNIEnv * env, jclass, jobject obj) {
    if (callbackObj != nullptr) {
        env->DeleteGlobalRef(callbackObj);
    }
    callbackObj = env->NewGlobalRef(obj);
    jclass objcls = env->GetObjectClass(callbackObj);
    callbackMethod = env->GetMethodID(objcls,"event","(Lcom/github/botn365/main/WootingAnalogWrapper$WootingAnalogDeviceEventType;Lcom/github/botn365/main/WootingAnalogWrapper$WootingAnalogDeviceInfoFFI;)V");
    env->GetJavaVM(&vm);
    WootingAnalogResult result = wooting_analog_set_device_event_cb([](WootingAnalog_DeviceEventType type,WootingAnalog_DeviceInfo_FFI * device){
        JNIEnv* env;
        if (vm->AttachCurrentThread((void **)&env,NULL) == JNI_OK) {
            jobject deviceEFI = newDeviceEFI(device,env);
            jobject deviceEvent = translateDeviceEventType(type,env);
            env->CallVoidMethod(callbackObj,callbackMethod,deviceEvent,deviceEFI);
            vm->DetachCurrentThread();
        }
    });
    return translateWootingResult(result,env);
}

JNIEXPORT jobject JNICALL Java_com_github_botn365_main_WootingAnalogWrapper_wootingAnalogClearDeviceEventCb (JNIEnv * env, jclass) {
    if (callbackObj != nullptr) {
        callbackMethod = nullptr;
        env->DeleteGlobalRef(callbackObj);
        callbackObj = nullptr;
    }
    return translateWootingResult(wooting_analog_clear_device_event_cb(),env);
}
//endregion