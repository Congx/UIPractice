//
// Created by Luffy on 10/5/2021.
//
#include "jni.h"
#include "../log/log.h"
#include "rtmp.h"

extern "C"
JNIEXPORT void JNICALL
Java_com_xc_ffplayer_live_LivePush_connect(JNIEnv *env, jobject thiz,jstring url) {

    char *str_url = const_cast<char *>(env->GetStringUTFChars(url, NULL));
    RTMP *rtmp = RTMP_Alloc();


}
