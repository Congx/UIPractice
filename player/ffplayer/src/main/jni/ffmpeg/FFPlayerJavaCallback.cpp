//
// Created by Luffy on 28/5/2021.
//

#include <log/log.h>
#include "FFPlayerJavaCallback.h"

FFPlayerJavaCallback::FFPlayerJavaCallback(JavaVM *javaVM, JNIEnv *env, jobject *j_obj):javaVM(javaVM),env(env){
    jobj = env->NewGlobalRef(*j_obj);
    jclazz = env->GetObjectClass(jobj);
    jmid_onPrepared = env->GetMethodID(jclazz,"onPrepared","(III)V");
    jmid_playAudio = env->GetMethodID(jclazz,"playAudio","([BI)V");
    jmid_createAudioTrack = env->GetMethodID(jclazz,"createAudioTrack","(III)V");
    jmid_onCurrentTime = env->GetMethodID(jclazz,"onCurrentTime","(II)V");
    jmid_onLoading = env->GetMethodID(jclazz,"onLoading","(Z)V");
    jmid_renderyuv = env->GetMethodID(jclazz, "onCallRenderYUV", "(II[B[B[B)V");
}

FFPlayerJavaCallback::~FFPlayerJavaCallback() {

}

void FFPlayerJavaCallback::onPrepared(int width,int height,int fps,int type) {
    if(type == MAIN_THREAD)
    {
        env->CallVoidMethod(jobj, jmid_onPrepared);
    }
    else if(type == CHILD_THREAD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_onPrepared,width,height,fps);
        javaVM->DetachCurrentThread();
    }
}

void FFPlayerJavaCallback::createAudioTrack(int channels, int sampleRate, int sampleBitCount,int type,bool needDetach) {
    if(type == MAIN_THREAD)
    {
        env->CallVoidMethod(jobj, jmid_createAudioTrack,channels,sampleRate,sampleBitCount);
    }
    else if(type == CHILD_THREAD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_createAudioTrack,channels,sampleRate,sampleBitCount);
        // 这里需要注意的是如果是java 线程中就不需要detatch,java 线程本来就attach了否则会报错
//        if (needDetach) {
            javaVM->DetachCurrentThread();
//        }
    }
}

void FFPlayerJavaCallback::playAudio(uint8_t *bytes, int len,int type,bool needDetach) {
    if(type == MAIN_THREAD)
    {
        jbyteArray arr = env->NewByteArray(len);
        env->CallVoidMethod(jobj, jmid_playAudio,arr,len);
        env->DeleteLocalRef(arr);
    }
    else if(type == CHILD_THREAD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
            return;
        }
        jbyteArray arr = jniEnv->NewByteArray(len);
        jniEnv->SetByteArrayRegion(arr, 0, len, reinterpret_cast<const jbyte *>(bytes));
        jniEnv->CallVoidMethod(jobj, jmid_playAudio,arr,len);
        jniEnv->DeleteLocalRef(arr);
//        if (needDetach) {
            javaVM->DetachCurrentThread();
//        }
    }
}

void FFPlayerJavaCallback::onCurrentTime(int currentTime, int totalTime,int type) {
    if(type == MAIN_THREAD)
    {
        env->CallVoidMethod(jobj, jmid_onCurrentTime,currentTime,totalTime);
    }
    else if(type == CHILD_THREAD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_onCurrentTime,currentTime,totalTime);
//        if (needDetach) {
        javaVM->DetachCurrentThread();
//        }
    }
}

void FFPlayerJavaCallback::onCallLoad(int type, bool isLoading) {
    if(type == MAIN_THREAD)
    {
        env->CallVoidMethod(jobj, jmid_onLoading,isLoading);
    }
    else if(type == CHILD_THREAD)
    {
        JNIEnv *jniEnv;
        if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
        {
            return;
        }
        jniEnv->CallVoidMethod(jobj, jmid_onLoading,isLoading);
//        if (needDetach) {
        javaVM->DetachCurrentThread();
//        }
    }
}

void FFPlayerJavaCallback::onCallRenderYUV(int width, int height, uint8_t *fy, uint8_t *fu, uint8_t *fv) {

    JNIEnv *jniEnv;
    if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK)
    {
        LOGE("call onCallComplete worng");
        return;
    }

    jbyteArray y = jniEnv->NewByteArray(width * height);
    jniEnv->SetByteArrayRegion(y, 0, width * height, reinterpret_cast<const jbyte *>(fy));

    jbyteArray u = jniEnv->NewByteArray(width * height / 4);
    jniEnv->SetByteArrayRegion(u, 0, width * height / 4, reinterpret_cast<const jbyte *>(fu));

    jbyteArray v = jniEnv->NewByteArray(width * height / 4);
    jniEnv->SetByteArrayRegion(v, 0, width * height / 4, reinterpret_cast<const jbyte *>(fv));

    jniEnv->CallVoidMethod(jobj, jmid_renderyuv, width, height, y, u, v);

    jniEnv->DeleteLocalRef(y);
    jniEnv->DeleteLocalRef(u);
    jniEnv->DeleteLocalRef(v);
    javaVM->DetachCurrentThread();
}

void FFPlayerJavaCallback::release() {
//    JNIEnv *jniEnv;
//    if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK){
//        return;
//    }
//    jniEnv->DeleteGlobalRef(jobj);
//    javaVM->DetachCurrentThread();
}
