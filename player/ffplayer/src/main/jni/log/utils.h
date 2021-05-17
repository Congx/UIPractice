//
// Created by Luffy on 17/5/2021.
//

#ifndef UIPRACTICE_UTILS_H
#define UIPRACTICE_UTILS_H

#include "jni.h"

const int TYPE_SHEFT = 0x1f;
const int SPS_TYPE = 7;
const int PPS_TYPE = 8;
const int DIR_TYPE = 5;
const int NOIDIR_TYPE = 1;

bool isDIR(uint8_t data) {
    return (data & 0x1f) == DIR_TYPE;
}

bool isSPS(uint8_t data) {
    return (data & 0x1f) == SPS_TYPE;
}

bool isPPS(uint8_t data) {
    return (data & 0x1f) == PPS_TYPE;
}

#endif //UIPRACTICE_UTILS_H
