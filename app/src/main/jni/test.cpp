//
// Created by xucong on 2020/10/6.
//

#include "jni.h"
//#include <cstdlib>
#include <string>
#include <iostream>

using namespace std;

void add(JNIEnv env) {
    string str = "xxx";
    string str1 = "xxx";
    string sss = str + str1;
    cout << sss << endl;
};