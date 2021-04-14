//
// Created by xucong on 2020/10/6.
//

#include "jni.h"
//#include <cstdlib>
#include <string>
#include <iostream>


using namespace std;

class Student {

};

void getS(Student ** stu) {
    // 这里改变了入参的值
    Student *tmp = static_cast<Student *>(malloc(sizeof(Student)));
    (*stu) = tmp;
}

void getS2(Student *& stu) {
    // 这里改变了入参的值
    Student *tmp = static_cast<Student *>(malloc(sizeof(Student)));
    stu = tmp;
}

Student getStudent() {
    Student s;
    return s;
}

Student getStudent1(Student student) {
    return student;
}

void copyConstruct() {
    Student s1;
    Student s2 = s1;
    Student s3 = getStudent();
    Student s4 = getStudent1(s3);
}


