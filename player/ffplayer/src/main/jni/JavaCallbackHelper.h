//
// Created by Luffy on 17/5/2021.
//

#ifndef UIPRACTICE_JAVACALLBACKHELPER_H
#define UIPRACTICE_JAVACALLBACKHELPER_H

#include "jni.h"

class JavaCallbackHelper {
public:
    JavaCallbackHelper(JavaVM *javaVM,JNIEnv *env,jobject thiz);
    ~JavaCallbackHelper();
    void rtmpConnected();
    void rtmpFailure();

private:
    JavaVM *javaVM;
    JNIEnv *env;
    jobject jobj;
};


#endif //UIPRACTICE_JAVACALLBACKHELPER_H
