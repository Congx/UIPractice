//
// Created by Luffy on 10/5/2021.
//
#include "jni.h"
#include "log.h"
#include "rtmp.h"
#include <cstdlib>
#include <cstring>
#include <cstdio>
#include <fstream>
#include "safe_queue.h"
#include "VideoChannel.h"
#include "JavaCallbackHelper.h"

using namespace std;

const int TYPE_SHEFT = 0x1f;
const int SPS_TYPE = 7;
const int PPS_TYPE = 8;
const int DIR_TYPE = 5;
const int NOIDIR_TYPE = 1;

const int RTMP_PKG_VIDEO = 0; // type
const int RTMP_PKG_AUDIO = 1;
const int RTMP_PKG_AUDIO_HEAD = 2;

SafeQueue<RTMPPacket *> packetQueue;

typedef struct {
    RTMP *rtmp;
    int8_t *sps = NULL;
    int16_t sps_len;
    int8_t *pps = NULL;
    int16_t pps_len;
    bool isConnected;
} Live;

Live *live = NULL;
VideoChannel *videoChannel;
JavaCallbackHelper *callbackHelper;
JavaVM* javaVM;
uint32_t start_time;

void sendVideo(jbyte *data, jint size, jlong stamp);

void prepareData(jbyte *data, jint size);

bool isDIR(jbyte data) {
    return (data & 0x1f) == DIR_TYPE;
}

bool isSPS(jbyte data) {
    return (data & 0x1f) == SPS_TYPE;
}

bool isPPS(jbyte data) {
    return (data & 0x1f) == PPS_TYPE;
}

int addPacket(RTMPPacket *pPacket);

void sendAudio(jbyte *data, jint size, jlong stamp, jint type);


//回调函数 在这里面注册函数
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv *env = NULL;
    //判断虚拟机状态是否有问题
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    assert(env != NULL);
    javaVM = vm;
    //返回jni 的版本
    return JNI_VERSION_1_6;
}

void releaseRtmpPacket(RTMPPacket * rtmpPacket) {
    if(rtmpPacket) {
        RTMPPacket_Free(rtmpPacket);
        delete rtmpPacket;
        rtmpPacket = 0;
    }
}

void videoCallback(RTMPPacket *packet) {
    if(packet) {
        packet->m_nTimeStamp = RTMP_GetTime() - start_time;
        addPacket(packet);
    }
}

void *rtmpStart(void *arg) {
    char *url = (char *) arg;

    RTMP *rtmp = RTMP_Alloc();
    RTMP_Init(rtmp);
    rtmp->Link.timeout = 10;
    LOGI("url:%s", url);

    live = static_cast<Live *>(malloc(sizeof(Live)));
    live->rtmp = rtmp;
    live->sps = NULL;
    live->pps = NULL;

    if (!RTMP_SetupURL(rtmp, url)) {
        LOGI("rtmp RTMP_SetupURL failure");
        callbackHelper->rtmpFailure();
        return 0;
    }

    RTMP_EnableWrite(rtmp);
//    RTMP_Connect(rtmp,0);
    if (!RTMP_Connect(rtmp, 0)) {
        callbackHelper->rtmpFailure();
        LOGI("rtmp RTMP_Connect failure");
        return 0;
    }

//    RTMP_ConnectStream(rtmp,0);
    if (!RTMP_ConnectStream(rtmp, 0)) {
        callbackHelper->rtmpFailure();
        LOGI("rtmp RTMP_Connect failure");
        return 0;
    }
    live->isConnected = true;
    callbackHelper->rtmpConnected();
    LOGI("rtmp RTMP_Connect success");

    RTMPPacket *rtmpPacket = NULL;

    start_time = RTMP_GetTime();
    packetQueue.setWork(1);
    int ret;
    while (live->isConnected) {
        LOGI("rtmp take...");
        packetQueue.pop(rtmpPacket);
        if(!rtmpPacket) continue;
        rtmpPacket->m_nInfoField2 = live->rtmp->m_stream_id;
        ret = RTMP_SendPacket(live->rtmp,rtmpPacket,1);
        releaseRtmpPacket(rtmpPacket);
        if(!ret) {
            LOGI("rtmp packet 发送失败");
        }
//        LOGI("rtmp packet 发送成功");
    }
    releaseRtmpPacket(rtmpPacket);

    if (!live->rtmp) {
        live->isConnected = false;
        RTMP_Close(live->rtmp);
        RTMP_Free(live->rtmp);
        LOGI("rtmp close");
    }
    return 0;
}

/**
 *  连接RTMP服务器
 */
extern "C"
JNIEXPORT jboolean JNICALL
Java_com_xc_ffplayer_live_DataPush_connect(JNIEnv *env, jobject thiz, jstring jpath) {
//    if (live->isConnected) return true;
    char *path = const_cast<char *>(env->GetStringUTFChars(jpath, NULL));
    pthread_t pthread;
    char *_url = new char[strlen(path) + 1];
    strcpy(_url,path);
//    LOGI("path:%s", path);
//    LOGI("_url:%s", _url);
    pthread_create(&pthread, NULL, rtmpStart, _url);
    env->ReleaseStringUTFChars(jpath, path);
    return true;
}

/**
 * sps/pps 数据包
 * @param data
 * @return
 */
RTMPPacket *createVideoPacket(Live *live) {
    int body_size = 13 + live->sps_len + 3 + live->pps_len;
    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Alloc(packet, body_size);

    int i = 0;
    packet->m_body[i++] = 0x17;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x01; // 版本

    packet->m_body[i++] = live->sps[1]; // 编码规格 sps[1]+sps[2]+sps[3]
    packet->m_body[i++] = live->sps[2];
    packet->m_body[i++] = live->sps[3];

    packet->m_body[i++] = 0xFF;
    packet->m_body[i++] = 0xE1; // sps个数： 1 ，0xE1 & 0x1F

    packet->m_body[i++] = (live->sps_len >> 8) & 0xFF; // sps 长度 2个字节  , 高八位
    packet->m_body[i++] = live->sps_len & 0xFF; // sps 长度  低八位

    // sps 内容
    memcpy(&packet->m_body[i], live->sps, live->sps_len);
    i += live->sps_len;

    packet->m_body[i++] = 0x01; // pps 个数
    packet->m_body[i++] = (live->pps_len >> 8) & 0xFF; // pps 长度 高八位
    packet->m_body[i++] = live->pps_len & 0xFF; // pps 长度 低八位

    // pps 内容
    memcpy(&packet->m_body[i], live->pps, live->pps_len);
    // ----- 数据填充完毕

    // 设置属性
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nBodySize = body_size; // 数据总长度
    packet->m_nChannel = 0x04; // 视频04
    packet->m_nTimeStamp = 0;
    packet->m_hasAbsTimestamp = 0;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_nInfoField2 = live->rtmp->m_stream_id;

    return packet;
}

/**
 * 数据包
 * @param data
 * @return
 */
RTMPPacket *createVideoPacket(jbyte *data, jint size, jlong stamp) {
    // 这里注意去掉分隔符
    data += 4;
    size -= 4;

    int body_size = size + 9;
    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Alloc(packet, body_size);
    int i = 0;
    if (isDIR(data[0])) {
        packet->m_body[i++] = 0x17;
    } else {
        packet->m_body[i++] = 0x27;
    }
    packet->m_body[i++] = 0x01;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;
    packet->m_body[i++] = 0x00;

    // 长度  4字节
    packet->m_body[i++] = size >> 24 & 0xFF;
    packet->m_body[i++] = size >> 16 & 0xFF;
    packet->m_body[i++] = size >> 8 & 0xFF;
    packet->m_body[i++] = size & 0xFF;

    // 数据
    memcpy(&packet->m_body[i], data, size);

    // 设置其他属性
    packet->m_nBodySize = body_size; // 数据总长度
    packet->m_packetType = RTMP_PACKET_TYPE_VIDEO;
    packet->m_nChannel = 0x04; // 视频04
    packet->m_hasAbsTimestamp = 0;
    packet->m_nTimeStamp = stamp;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_nInfoField2 = live->rtmp->m_stream_id;

    return packet;
}

void sendVideo(jbyte *data, jint size, jlong stamp) {
//     ofstream out("/sdcard/Android/data/com.xc.ffplayer/files/output/liveDataSps.h264",ios::out | ios::app);
//     out.write(reinterpret_cast<const char *>(data), size);
    // 保存sps、pps数据
    if (isSPS(data[4]) && live != NULL && live->sps == NULL && live->pps == NULL) {
        LOGI("sps、pps data prepare");
        prepareData(data, size);
        return;
    }

    if (live->pps == nullptr && live->sps == nullptr) {
        LOGI("sps、pps 帧数据正在准备...");
        return;
    }

    // I 帧
    if (isDIR(data[4])) {
        LOGI("i 帧数据，发送sps、pps");
        RTMPPacket *packet = createVideoPacket(live);
        addPacket(packet);
    }

    RTMPPacket *packet = createVideoPacket(data, size, stamp);
    addPacket(packet);
}

int addPacket(RTMPPacket *pPacket) {
    if (packetQueue.size() > 50) {
        packetQueue.clear();
    }
    packetQueue.push(pPacket);
    LOGI("rtmp added...");
    return 0;
}

void prepareData(jbyte *data, jint size) {
    // 00000001 6742C01F DA02D028 48078402 15000000 0168CE3C 80
    for (int i = 0; i < size - 4; i++) {
        if (data[i] == 0x00 && data[i + 1] == 0x00 && data[i + 2] == 0x00 && data[i + 3] == 0x01 &&
            isPPS(data[i + 4])) {
            live->sps_len = i - 4;
            live->pps_len = size - i - 4;
            live->sps = static_cast<int8_t *>(malloc(live->sps_len));
            live->pps = static_cast<int8_t *>(malloc(live->pps_len));
            memcpy(live->sps, data + 4, live->sps_len);
            memcpy(live->pps, data + 4 + live->sps_len + 4, live->pps_len);
//            LOGI("rtmp sps  pps already");
        }
    }
}

RTMPPacket *createAudioPackate(jbyte *data, jint size, jlong stamp, jint type) {
    int body_size = size + 2;

    RTMPPacket *packet = static_cast<RTMPPacket *>(malloc(sizeof(RTMPPacket)));
    RTMPPacket_Alloc(packet, body_size);
    packet->m_body[0] = 0xAF;
    if (type == RTMP_PKG_AUDIO_HEAD) {
        packet->m_body[1] = 0x00;
    } else {
        packet->m_body[1] = 0x01;
    }
    memcpy(&packet->m_body[2], data, size);
    // 设置其他属性
    packet->m_nBodySize = body_size; // 数据总长度
    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_nChannel = 0x05; // 音频05
    packet->m_hasAbsTimestamp = 0;
    packet->m_nTimeStamp = stamp;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    packet->m_nInfoField2 = live->rtmp->m_stream_id;

    return packet;
}

void sendAudio(jbyte *data, jint size, jlong stamp, jint type) {
    RTMPPacket *packet = createAudioPackate(data, size, stamp, type);
    addPacket(packet);
}

/**
 * 硬编码传递的数据,编码之后的数据包
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_xc_ffplayer_live_DataPush_sendData(JNIEnv *env, jobject thiz, jbyteArray bytes, jint size,
                                            jlong time_stamp, jint type) {
    jbyte *data = env->GetByteArrayElements(bytes, NULL);

    switch (type) {
        case RTMP_PKG_VIDEO:
//            LOGI("rtmp send vidio packet");
            sendVideo(data, size, time_stamp);
            break;
        case RTMP_PKG_AUDIO:
        case RTMP_PKG_AUDIO_HEAD:
//            LOGI("rtmp send audio packet");
            sendAudio(data, size, time_stamp, type);
            break;
    }

    env->ReleaseByteArrayElements(bytes, data, 0);
}


/**
 *  软编码初始化
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_xc_ffplayer_live_DataPush_nativeInit(JNIEnv *env, jobject thiz) {
    videoChannel = new VideoChannel;
    callbackHelper = new JavaCallbackHelper(javaVM,env,thiz);
    videoChannel->setVideoCallback(videoCallback);
}


// --------------  软编 相关

/**
 * 相机初始化完成之后，初始化x264解码器
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_xc_ffplayer_live_DataPush_nativeSetVideoEncodeInfo(JNIEnv *env, jobject thiz, jint width,
                                                            jint height, jint fps, jint bitrate) {
    if (videoChannel) {
        videoChannel->setVideoEncodeInfo(width,height,fps,bitrate);
    }

}

/**
 * 软编传递的数据，nv12数据，x264编码
 */
extern "C"
JNIEXPORT void JNICALL
Java_com_xc_ffplayer_live_DataPush_nativeSendNV21Data(JNIEnv *env, jobject thiz, jbyteArray nv12bytes,
                                                      jint len) {


}

extern "C"
JNIEXPORT void JNICALL
Java_com_xc_ffplayer_live_DataPush_nativeStop(JNIEnv *env, jobject thiz) {
    if (live != NULL) {
        live->isConnected = false;
        RTMP_Close(live->rtmp);
        RTMP_Free(live->rtmp);
        LOGI("rtmp close");
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_xc_ffplayer_live_DataPush_nativeRelease(JNIEnv *env, jobject thiz) {

    if(live) {
        if(live->rtmp) {
            RTMP_Close(live->rtmp);
            RTMP_Free(live->rtmp);
            live->rtmp = NULL;
        }
        delete live;
        delete live->sps;
        live->sps = NULL;
        live->pps = NULL;
        live = NULL;
    }

    if(videoChannel) {
        delete videoChannel;
        videoChannel = NULL;
    }

    if (callbackHelper) {
        delete callbackHelper;
        callbackHelper = NULL;
    }

}

