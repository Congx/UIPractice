//
// Created by Luffy on 26/5/2021.
//

#include "jni.h"

extern "C"{
#include "libavcodec/avcodec.h"
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_xc_ffplayer_ffplayer_FFPlayer_ffmpegInfo(JNIEnv *env, jobject thiz) {
    char *info = const_cast<char *>(av_version_info());
    return env->NewStringUTF(info);
}