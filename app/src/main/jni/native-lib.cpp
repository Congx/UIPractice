//
// Created by xucong on 2020/10/6.
//

#include "jni.h"
//#include <cstdlib>
#include <string>
#include <iostream>
#include "cmaketest/math/include/math.h"

#include "android/bitmap.h"

using namespace std;

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_uipractice_ndk_NDKActivity_add(JNIEnv *env, jobject thiz, jint a, jint b) {

   return math_add(a,b);
//   return 0;
}

