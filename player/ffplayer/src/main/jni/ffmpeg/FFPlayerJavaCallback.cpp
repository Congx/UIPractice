//
// Created by Luffy on 28/5/2021.
//

#include "FFPlayerJavaCallback.h"

FFPlayerJavaCallback::FFPlayerJavaCallback(JavaVM *javaVM, JNIEnv *env, jobject *j_obj):javaVM(javaVM),env(env){
    jobj = env->NewGlobalRef(*j_obj);
    jclazz = env->GetObjectClass(jobj);
    jmid_onPrepared = env->GetMethodID(jclazz,"onPrepared","()V");
    jmid_playAudio = env->GetMethodID(jclazz,"playAudio","([BI)V");
    jmid_createAudioTrack = env->GetMethodID(jclazz,"createAudioTrack","(III)V");
}

FFPlayerJavaCallback::~FFPlayerJavaCallback() {
    JNIEnv *jniEnv;
    if(javaVM->AttachCurrentThread(&jniEnv, 0) != JNI_OK){
        return;
    }
    jniEnv->DeleteGlobalRef(jobj);
}

void FFPlayerJavaCallback::onPrepared(int type) {
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
        jniEnv->CallVoidMethod(jobj, jmid_onPrepared);
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
