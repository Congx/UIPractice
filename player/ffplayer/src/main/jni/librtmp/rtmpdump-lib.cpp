////
//// Created by Luffy on 10/5/2021.
////
//#include "jni.h"
//#include "../log/log.h"
//#include "rtmp.h"
//#include <cstdlib>
//#include <cstring>
//#include <cstdio>
//#include <fstream>
//
//using namespace std;
//
//const int TYPE_SHEFT = 0x1f;
//const int SPS_TYPE = 7;
//const int PPS_TYPE = 8;
//const int DIR_TYPE = 5;
//const int NOIDIR_TYPE = 1;
//
//const int RTMP_PKG_VIDEO = 0; // type
//const int RTMP_PKG_AUDIO = 1;
//const int RTMP_PKG_AUDIO_HEAD = 2;
//
//typedef struct {
//    RTMP *rtmp;
//    int8_t *sps = NULL;
//    int16_t sps_len;
//    int8_t *pps = NULL;
//    int16_t pps_len;
//    bool isConnected;
//} Live;
//
//Live *live = NULL;
//
//void sendVideo(jbyte *data, jint size, jlong stamp);
//void prepareData(jbyte *data, jint size);
//
//bool isDIR(jbyte data) {
//    return (data & 0x1f) == DIR_TYPE;
//}
//
//bool isSPS(jbyte data) {
//    return (data & 0x1f) == SPS_TYPE;
//}
//
//bool isPPS(jbyte data) {
//    return (data & 0x1f) == PPS_TYPE;
//}
//int sendPacket(RTMPPacket *pPacket);
//
//void sendAudio(jbyte *data, jint size, jlong stamp, jint type);
//
//extern "C"
//JNIEXPORT jboolean JNICALL
//Java_com_xc_ffplayer_live_DataPush_connect(JNIEnv *env, jobject thiz, jstring url) {
//
//    char *str_url = const_cast<char *>(env->GetStringUTFChars(url, NULL));
//    RTMP *rtmp = RTMP_Alloc();
//    RTMP_Init(rtmp);
//    rtmp->Link.timeout = 10;
//    LOGI("url:%s",str_url);
//
//    live = static_cast<Live *>(malloc(sizeof(Live)));
//    live->rtmp = rtmp;
//    live->sps = NULL;
//    live->pps = NULL;
//
//    if(!RTMP_SetupURL(rtmp,str_url)) {
//        LOGI("rtmp RTMP_SetupURL failure");
//        return false;
//    }
//
//    RTMP_EnableWrite(rtmp);
////    RTMP_Connect(rtmp,0);
//    if(!RTMP_Connect(rtmp,0)) {
//        LOGI("rtmp RTMP_Connect failure");
//        return false;
//    }
//
////    RTMP_ConnectStream(rtmp,0);
//    if(!RTMP_ConnectStream(rtmp,0)) {
//        LOGI("rtmp RTMP_Connect failure");
//        return false;
//    }
//
//    LOGI("rtmp RTMP_Connect success");
//
//    env->ReleaseStringUTFChars(url,str_url);
//
//    live->isConnected = true;
//    return true;
//}
//
///**
// * sps/pps 数据包
// * @param data
// * @return
// */
//RTMPPacket * createVideoPacket(Live *live) {
////    int body_size = 16 + live->pps_len + live->pps_len;
////    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
////    RTMPPacket_Alloc(packet,body_size);
////
////    int index = 0;
////    packet->m_body[index++] = 0x17;
////    packet->m_body[index++] = 0x00;
////    packet->m_body[index++] = 0x00;
////    packet->m_body[index++] = 0x00;
////    packet->m_body[index++] = 0x01; // 版本
////
////    packet->m_body[index++] = live->sps[1]; // 编码规格 sps[1]+sps[2]+sps[3]
////    packet->m_body[index++] = live->sps[2];
////    packet->m_body[index++] = live->sps[3];
////
////    packet->m_body[index++] = 0xFF;
////    packet->m_body[index++] = 0xE1; // sps个数： 1 ，0xE1 & 0x1F
////
////    packet->m_body[index++] = live->sps_len >> 8 & 0xFF; // sps 长度 2个字节  , 高八位
////    packet->m_body[index++] = live->sps_len & 0xFF; // sps 长度  低八位
////
////    // sps 内容
////    memcpy(&packet->m_body[index],live->sps,live->sps_len);
////    index += live->sps_len;
////
////    packet->m_body[index++] = 0x01; // pps 个数
////    packet->m_body[index++] = live->pps_len >> 8 & 0xFF;; // pps 长度 高八位
////    packet->m_body[index++] = live->pps_len & 0xFF;; // pps 长度 低八位
////
////    // sps 内容
////    memcpy(&packet->m_body[index],live->pps,live->pps_len);
////    // ----- 数据填充完毕
////
////    // 设置属性
////    packet->m_nBodySize = body_size; // 数据总长度
////
////    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
////    packet->m_nChannel = 0x04; // 视频04
////    packet->m_hasAbsTimestamp = 0;
////    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
////    packet->m_nInfoField2 = live->rtmp->m_stream_id;
//
//    int body_size = 13 + live->sps_len + 3 + live->pps_len;
//    RTMPPacket *packet = (RTMPPacket *) malloc(sizeof(RTMPPacket));
//    RTMPPacket_Alloc(packet, body_size);
//    int i = 0;
//    //AVC sequence header 与IDR一样
//    packet->m_body[i++] = 0x17;
//    //AVC sequence header 设置为0x00
//    packet->m_body[i++] = 0x00;
//    //CompositionTime
//    packet->m_body[i++] = 0x00;
//    packet->m_body[i++] = 0x00;
//    packet->m_body[i++] = 0x00;
//    //AVC sequence header
//    packet->m_body[i++] = 0x01;   //configurationVersion 版本号 1
//    packet->m_body[i++] = live->sps[1]; //profile 如baseline、main、 high
//
//    packet->m_body[i++] = live->sps[2]; //profile_compatibility 兼容性
//    packet->m_body[i++] = live->sps[3]; //profile level
//    packet->m_body[i++] = 0xFF; // reserved（111111） + lengthSizeMinusOne（2位 nal 长度） 总是0xff
//    //sps
//    packet->m_body[i++] = 0xE1; //reserved（111） + lengthSizeMinusOne（5位 sps 个数） 总是0xe1
//    //sps length 2字节
//    packet->m_body[i++] = (live->sps_len >> 8) & 0xff; //第0个字节
//    packet->m_body[i++] = live->sps_len & 0xff;        //第1个字节
//    memcpy(&packet->m_body[i], live->sps, live->sps_len);
//    i += live->sps_len;
//
//    /*pps*/
//    packet->m_body[i++] = 0x01; //pps number
//    //pps length
//    packet->m_body[i++] = (live->pps_len >> 8) & 0xff;
//    packet->m_body[i++] = live->pps_len & 0xff;
//    memcpy(&packet->m_body[i], live->pps, live->pps_len);
//
//    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
//    packet->m_nBodySize = body_size;
//    packet->m_nChannel = 0x04;
//    packet->m_nTimeStamp = 0;
//    packet->m_hasAbsTimestamp = 0;
//    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
//    packet->m_nInfoField2 = live->rtmp->m_stream_id;
//
//    return packet;
//}
//
///**
// * 数据包
// * @param data
// * @return
// */
//RTMPPacket * createVideoPacket(jbyte *data, jint size, jlong stamp) {
//    // 这里注意去掉分隔符
//    data += 4;
//    size -= 4;
//
//    int body_size = size + 9;
//    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
//    RTMPPacket_Alloc(packet,body_size);
//    int index = 0;
//    if (isDIR(data[0])) {
//        packet->m_body[index++] = 0x17;
//    }else {
//        packet->m_body[index++] = 0x27;
//    }
//    packet->m_body[index++] = 0x01;
//    packet->m_body[index++] = 0x00;
//    packet->m_body[index++] = 0x00;
//    packet->m_body[index++] = 0x00;
//
//    // 长度  4字节
//    packet->m_body[index++] = size >> 24 & 0xFF;
//    packet->m_body[index++] = size >> 16 & 0xFF;
//    packet->m_body[index++] = size >> 8 & 0xFF;
//    packet->m_body[index++] = size & 0xFF;
//
//    // 数据
//    memcpy(&packet->m_body[index],data,size);
//
//    // 设置其他属性
//    packet->m_nBodySize = body_size; // 数据总长度
//    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
//    packet->m_nChannel = 0x04; // 视频04
//    packet->m_hasAbsTimestamp = 0;
//    packet->m_nTimeStamp = stamp;
//    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
//    packet->m_nInfoField2 = live->rtmp->m_stream_id;
//
//    return packet;
//}
//
//void sendVideo(jbyte *data, jint size, jlong stamp) {
////     ofstream out("/sdcard/Android/data/com.xc.ffplayer/files/output/liveDataSps.h264",ios::out | ios::app);
////     out.write(reinterpret_cast<const char *>(data), size);
//    // 保存sps、pps数据
//    if (isSPS(data[4]) && live != NULL && live->sps == NULL && live->pps == NULL) {
//        LOGI("sps、pps data prepare");
//        prepareData(data, size);
//        return;
//    }
//
//    if(live->pps == nullptr && live->sps == nullptr) {
//        LOGI("sps、pps 帧数据正在准备...");
//        return;
//    }
//
//    // I 帧
//    if (isDIR(data[4])) {
//        LOGI("i 帧数据，发送sps、pps");
//        RTMPPacket *packet = createVideoPacket(live);
//        sendPacket(packet);
//    }
//
//    RTMPPacket *packet = createVideoPacket(data,size,stamp);
//    if(sendPacket(packet)) {
////        LOGI("send success");
//    }else {
////        LOGI("send failure");
//    }
//
////    LOGI("rtmp send video packet finish");
//}
//
//int sendPacket(RTMPPacket *pPacket) {
//    int ret = 0;
//    if (live->isConnected) {
//        ret = RTMP_SendPacket(live->rtmp,pPacket,1);
//    }
//    RTMPPacket_Free(pPacket);
//    free(pPacket);
//    return ret;
//}
//
//void prepareData(jbyte *data, jint size) {
//    // 00000001 6742C01F DA02D028 48078402 15000000 0168CE3C 80
//    for (int i = 0; i < size-4; i++) {
//        if (data[i] == 0x00 && data[i + 1] == 0x00 && data[i + 2] == 0x00 && data[i + 3] == 0x01 && isPPS(data[i + 4])) {
//            live->sps_len = i-4;
//            live->pps_len = size-i-4;
//            live->sps = static_cast<int8_t *>(malloc(live->sps_len));
//            live->pps = static_cast<int8_t *>(malloc(live->pps_len));
//            memcpy(live->sps, data+4, live->sps_len);
//            memcpy(live->pps, data+4+live->sps_len+4, live->pps_len);
////            LOGI("rtmp sps  pps already");
//        }
//    }
//}
//
//extern "C"
//JNIEXPORT void JNICALL
//Java_com_xc_ffplayer_live_DataPush_close(JNIEnv *env, jobject thiz) {
//    if (live != NULL) {
//        live->isConnected = false;
//        RTMP_Close(live->rtmp);
//        RTMP_Free(live->rtmp);
//        LOGI("rtmp close");
//    }
//
//}
//
//RTMPPacket * createAudioPackate(jbyte *data, jint size, jlong stamp, jint type) {
//    int body_size = size + 2;
//
//    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
//    RTMPPacket_Alloc(packet,body_size);
//    packet->m_body[0] = 0xAF;
//    if(type == RTMP_PKG_AUDIO_HEAD) {
//        packet->m_body[1] = 0x00;
//    }else {
//        packet->m_body[1] = 0x01;
//    }
//    memcpy(&packet->m_body[2],data,size);
//    // 设置其他属性
//    packet->m_nBodySize = body_size; // 数据总长度
//    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
//    packet->m_nChannel = 0x05; // 音频05
//    packet->m_hasAbsTimestamp = 0;
//    packet->m_nTimeStamp = stamp;
//    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
//    packet->m_nInfoField2 = live->rtmp->m_stream_id;
//
//    return packet;
//}
//
//void sendAudio(jbyte *data, jint size, jlong stamp, jint type) {
//    RTMPPacket *packet =  createAudioPackate(data,size,stamp,type);
//    sendPacket(packet);
////    LOGI("rtmp send audio packet finish");
//}
//
//extern "C"
//JNIEXPORT void JNICALL
//Java_com_xc_ffplayer_live_DataPush_sendData(JNIEnv *env, jobject thiz, jbyteArray bytes, jint size,
//                                            jlong time_stamp,jint type) {
//    jbyte *data = env->GetByteArrayElements(bytes, NULL);
//
//    switch (type) {
//        case RTMP_PKG_VIDEO:
////            LOGI("rtmp send vidio packet");
//            sendVideo(data, size, time_stamp);
//            break;
//        case RTMP_PKG_AUDIO:
//        case RTMP_PKG_AUDIO_HEAD:
////            LOGI("rtmp send audio packet");
//            sendAudio(data, size, time_stamp,type);
//            break;
//    }
//
//    env->ReleaseByteArrayElements(bytes,data,0);
//}
