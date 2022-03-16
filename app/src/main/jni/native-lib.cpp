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
   env->NewStringUTF("c string");
   return math_add(a,b);
//   return 0;
}

//每个native函数，都至少有两个参数（JNIEnv*,jclass或者jobject)
//1）当native方法为静态方法时：
//jclass 代表native方法所属类的class对象(JniTest.class)
//2）当native方法为非静态方法时：
//jobject 代表native方法所属的对象

//基本数据
//Java基本数据类型与JNI数据类型的映射关系
//Java类型->JNI类型->C类型

/*
boolean jboolean
byte jbyte;
char jchar;
short jshort;
int jint;
long jlong;
float jfloat;
double jdouble;
void void
*/

//引用类型(对象)
//String jstring
//object jobject
//数组,基本数据类型的数组
//byte[] jByteArray
//对象数组
//object[](String[]) jobjectArray

//C/C++访问Java的成员

//1.访问属性
//修改属性key
extern "C"
JNIEXPORT jstring JNICALL Java_com_dongnaoedu_jni_JniTest_accessField
        (JNIEnv *env, jobject jobj){
   //jobj是t对象，JniTest.class
   jclass cls = env->GetObjectClass(jobj);
   //jfieldID
   //属性名称，属性签名
   jfieldID fid = env->GetFieldID(cls, "key", "Ljava/lang/String;");

   //jason >> super jason
   //获取key属性的值
   //Get<Type>Field
   jstring jstr = static_cast<jstring>(env->GetObjectField(jobj, fid));
   printf("jstr:%#x\n",&jstr);

   //jstring -> c字符串
   //isCopy 是否复制（true代表赋值，false不复制）
   char *c_str = const_cast<char *>(env->GetStringUTFChars(jstr, NULL));
   //拼接得到新的字符串
   char text[20] = "super ";
   strcat(text,c_str);

   //c字符串 ->jstring
   jstring new_jstr = env->NewStringUTF(text);

   //修改key
   //Set<Type>Field
   env->SetObjectField(jobj, fid, new_jstr);

   printf("new_jstr:%#x\n", &new_jstr);

   return new_jstr;
}

//访问静态属性
extern "C" JNIEXPORT void JNICALL Java_com_dongnaoedu_jni_JniTest_accessStaticField
        (JNIEnv *env, jobject jobj){
   //jclass
   jclass cls = env->GetObjectClass(jobj);
   //jfieldID
   jfieldID fid = env->GetStaticFieldID(cls, "count", "I");
   //GetStatic<Type>Field
   jint count = env->GetStaticIntField(cls, fid);
   count++;
   //修改
   //SetStatic<Type>Field
   env->SetStaticIntField(cls,fid,count);
}

//2.访问java方法
extern "C" JNIEXPORT void JNICALL Java_com_dongnaoedu_jni_JniTest_accessMethod
        (JNIEnv *env, jobject jobj){
   //jclass
   jclass cls = env->GetObjectClass(jobj);
   //jmethodID
   jmethodID mid = env->GetMethodID(cls, "genRandomInt", "(I)I");
   //调用
   //Call<Type>Method
   jint random = env->CallIntMethod(jobj, mid, 200);
   printf("random num:%ld",random);

   //.....
}

//静态方法
extern "C" JNIEXPORT void JNICALL Java_com_dongnaoedu_jni_JniTest_accessStaticMethod
        (JNIEnv *env, jobject jobj){
   //jclass
   jclass cls = env->GetObjectClass(jobj);
   //jmethodID
   jmethodID mid = env->GetStaticMethodID(cls, "getUUID", "()Ljava/lang/String;");

   //调用
   //CallStatic<Type>Method
   jstring uuid = static_cast<jstring>(env->CallStaticObjectMethod(cls, mid));

   //随机文件名称 uuid.txt
   //jstring -> char*
   //isCopy JNI_FALSE，代表java和c操作的是同一个字符串
   char *uuid_str = const_cast<char *>(env->GetStringUTFChars(uuid, JNI_FALSE));
   //拼接
   char filename[100];
   sprintf(filename, "D://%s.txt",uuid_str);
   FILE *fp = fopen(filename,"w");
   fputs("i love jason", fp);
   fclose(fp);
}

//中文问题
extern "C" JNIEXPORT jstring JNICALL Java_com_dongnaoedu_jni_JniTest_chineseChars
        (JNIEnv *env, jobject jobj, jstring in){
   //输出
   //char *c_str = env->GetStringUTFChars(env, in, JNI_FALSE);
   //printf("%s\n",c_str);

   //c -> jstring
   char *c_str = "马蓉与宋江";
   //char c_str[] = "马蓉与宋喆";
   //jstring jstr = env->NewStringUTF(env, c_str);
   //执行String(byte bytes[], String charsetName)构造方法需要的条件
   //1.jmethodID
   //2.byte数组
   //3.字符编码jstring

   jclass str_cls = env->FindClass("java/lang/String");
   jmethodID constructor_mid = env->GetMethodID(str_cls, "<init>", "([BLjava/lang/String;)V");

   //jbyte -> char
   //jbyteArray -> char[]
   jbyteArray bytes = env->NewByteArray(strlen(c_str));
   //byte数组赋值
   //0->strlen(c_str)，从头到尾
   //对等于，从c_str这个字符数组，复制到bytes这个字符数组
   env->SetByteArrayRegion(bytes, 0, strlen(c_str), reinterpret_cast<const jbyte *>(c_str));

   //字符编码jstring
   jstring charsetName = env->NewStringUTF("GB2312");

   //调用构造函数，返回编码之后的jstring
   return static_cast<jstring>(env->NewObject(str_cls, constructor_mid, bytes, charsetName));
}

int compare(int *a,int *b){
   return (*a) - (*b);
}

//传入
extern "C" JNIEXPORT void JNICALL Java_com_dongnaoedu_jni_JniTest_giveArray
        (JNIEnv *env, jobject jobj, jintArray arr){
   //jintArray -> jint指针 -> c int 数组
   jint *elems = env->GetIntArrayElements( arr, NULL);
   //printf("%#x,%#x\n", &elems, &arr);
//   env->arr
   //数组的长度
   int len = env->GetArrayLength(arr);
   //排序
   qsort(elems, len, sizeof(jint), reinterpret_cast<int (*)(const void *, const void *)>(compare));

   //同步
   //mode
   //0, Java数组进行更新，并且释放C/C++数组
   //JNI_ABORT, Java数组不进行更新，但是释放C/C++数组
   //JNI_COMMIT，Java数组进行更新，不释放C/C++数组（函数执行完，数组还是会释放）
   env->ReleaseIntArrayElements(arr, elems, JNI_COMMIT);
}

//返回数组
extern "C" JNIEXPORT jintArray JNICALL Java_com_dongnaoedu_jni_JniTest_getArray(JNIEnv *env, jobject jobj, jint len){
   //创建一个指定大小的数组
   jintArray jint_arr = env->NewIntArray(len);
   jint *elems = env->GetIntArrayElements(jint_arr, NULL);
   int i = 0;
   for (; i < len; i++){
      elems[i] = i;
   }

   //同步
   env->ReleaseIntArrayElements(jint_arr, elems, 0);

   return jint_arr;
}