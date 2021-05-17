//
// Created by Luffy on 17/5/2021.
//

#include "JavaCallbackHelper.h"

JavaCallbackHelper::JavaCallbackHelper(JavaVM *javaVm, JNIEnv *env,jobject _thiz) {
    this->javaVM = javaVm;
    this->env = env;
    this->jobj = env->NewGlobalRef(_thiz);
}

void JavaCallbackHelper::rtmpConnected() {
    JNIEnv *env = NULL;
    if(javaVM->AttachCurrentThread(&env,NULL) == 0) {
        jclass jclazz = env->GetObjectClass(jobj);
        jmethodID jmethodId = env->GetMethodID(jclazz,"rtmpConnected","()V");
        env->CallVoidMethod(jobj,jmethodId);
        javaVM->DetachCurrentThread();
    }
}

void JavaCallbackHelper::rtmpFailure() {
    JNIEnv *env = NULL;
    if(javaVM->AttachCurrentThread(&env,NULL) == 0) {
        jclass jclazz = env->GetObjectClass(jobj);
        jmethodID jmethodId = env->GetMethodID(jclazz,"rtmpConnectedFailure","()V");
        env->CallVoidMethod(jobj,jmethodId);
        javaVM->DetachCurrentThread();
    }
}

JavaCallbackHelper::~JavaCallbackHelper() {
    env->DeleteGlobalRef(jobj);
    jobj = NULL;
}
