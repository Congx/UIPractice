//
// Created by Luffy on 28/5/2021.
//

#ifndef UIPRACTICE_FFPLAYERJAVACALLBACK_H
#define UIPRACTICE_FFPLAYERJAVACALLBACK_H

#include "jni.h"

#define MAIN_THREAD 0
#define CHILD_THREAD 1

class FFPlayerJavaCallback {
public:
    JavaVM *javaVM;
    jobject jobj;
    jclass jclazz;
    JNIEnv *env;
    jmethodID jmid_onPrepared;
    jmethodID jmid_playAudio;
    jmethodID jmid_createAudioTrack;
    jmethodID jmid_onCurrentTime;

public:
    FFPlayerJavaCallback(JavaVM *javaVM, JNIEnv *env, jobject *jobj);
    ~FFPlayerJavaCallback();
    void onPrepared(int type);

    // 测试audioTrack用
    void createAudioTrack(int channels,int sampleRate,int sampleBitCount,int type,bool needDetach);
    void playAudio(uint8_t *bytes ,int len,int type,bool needDetach);
    void onCurrentTime(int currentTime,int totalTime,int type);
};


#endif //UIPRACTICE_FFPLAYERJAVACALLBACK_H