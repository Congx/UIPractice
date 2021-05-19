//
// Created by Luffy on 18/5/2021.
//

#include <cstdlib>
#include <cstring>
#include "AudioChannel.h"

AudioChannel::AudioChannel() {

}

AudioChannel::~AudioChannel() {

}

int AudioChannel::setAudioInfo(int sampleRate, int channels) {

    LOGI("faac audio init ....");
    //输入样本的容器 大小： 要送给编码器编码的样本数
    unsigned long inputSamples;
//    实例化faac编码器
    /**
     unsigned long   nSampleRate,        // 采样率，单位是bps
    unsigned long   nChannels,          // 声道，1为单声道，2为双声道
    unsigned long   &nInputSamples,     // 传引用，采样点的个数
    unsigned long   &nMaxOutputBytes    // 传引用，得到每次调用编码时生成的AAC数据的最大长度
     */
    codec = faacEncOpen(sampleRate, channels, &inputSamples, &maxOutputBytes);

//输入容器真正大小  字节数 采样个数 * 位数 / 8
    inputByteNum = inputSamples * 16 / 8;

    this->inputSamples = inputSamples;

//实例化 输出的容器
    outputBuffer = static_cast<unsigned char *>(malloc(maxOutputBytes));
    LOGI("初始化-----------》%d  inputByteNum %d  maxOutputBytes:%d ",codec,inputByteNum,maxOutputBytes);
//参数
    faacEncConfigurationPtr configurationPtr = faacEncGetCurrentConfiguration(codec);
//编码  MPEG AAC
    configurationPtr->mpegVersion = MPEG4;
    configurationPtr->useLfe = 0;//是否允许一个声道为低频通道
    configurationPtr->useTns = 1;  //是否使用瞬时噪声定形滤波器(具体作用不是很清楚)
//    编码等级
    configurationPtr->aacObjectType = LOW;

    //是否允许midSide coding (在MPEG-2 AAC 系统中，M/S(Mid/Side) Stereo coding被提供在多声道信号中，每个声道对(channel pair)的组合，也就是每个通道对，是对称地排列在人耳听觉的左右两边，其方式简单，且对位串不会引起较显著的负担。 一般其在左右声道数据相似度大时常被用到，并需记载每一频带的四种能量临界组合，分别为左、右、左右声道音频合并(L+R)及相减(L-R)的两种新的能量。一般，若所转换的Sid声道的能量较小时，M/S Stereo coding 可以节省此通道的位数，而将多余的位应用于另一个所转换的声道，即Mid 声道，进而可提高此编码效率。)
//    configurationPtr->allowMidside = 0;
//    configurationPtr->bitRate =48000;  //设置比特率
//    configurationPtr->useLfe = 0;
//    configurationPtr->bandWidth = 32000;

//输出aac裸流数据
    configurationPtr->outputFormat = 0;
//采样位数
    configurationPtr->inputFormat = FAAC_INPUT_16BIT;
//    将我们的配置生效
    faacEncSetConfiguration(codec, configurationPtr);

    LOGI("faac audio init succeeced");
    return 0;
}

void AudioChannel::encode(int32_t *data, int len) {

    // 发送音频头
    if (!isSendHead) {
        isSendHead = true;
        callback(getAudioConfig());
    }

//    LOGI("faac 编码 outputBuffer=%d,maxOutputBytes=%d,",outputBuffer,maxOutputBytes);
//    音频的数据   data   原始数据  1 编码 = 压缩 数据2  检查  bug   编码初始化成功
//    LOGE("发送音频%d", len);

//    一句话  将pcm数据编码成aac数据
    int samplesInput = len * 8 / 16; // 采样点的个数!!!! 不是数据长度
//    int samplesInput = len;
//    LOGI("faac 编码 samplesInput=%d",samplesInput);
//    LOGI("faac 编码 inputSamples=%d",inputSamples);
    int bytelen=faacEncEncode(codec, data, samplesInput, outputBuffer, maxOutputBytes);
//    int bytelen=0;
        //outputBuffer   压缩1   原始 2
    if (bytelen > 0) {
//        拼装packet  数据   NDK
        RTMPPacket *packet = new RTMPPacket;
        RTMPPacket_Alloc(packet, bytelen + 2);
        packet->m_body[0] = 0xAF;//
        packet->m_body[1] = 0x01;
        memcpy(&packet->m_body[2], outputBuffer, bytelen);
        packet->m_hasAbsTimestamp = 0;
        packet->m_nBodySize = bytelen + 2;
        packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
        packet->m_nChannel = 0x11;
        packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
        callback(packet);
    }
}

/**
 * 音频头
 * @return
 */
RTMPPacket *AudioChannel::getAudioConfig() {
//    视频帧的sps pps
    u_char *buf;
    u_long len;
//头帧的内容   {0x12 0x08}
    faacEncGetDecoderSpecificInfo(codec, &buf, &len);
//头帧的  rtmpdump  实时录制  实时给时间戳
    RTMPPacket *packet = new RTMPPacket;
    RTMPPacket_Alloc(packet, len + 2);

    packet->m_body[0] = 0xAF;
    packet->m_body[1] = 0x00;
    memcpy(&packet->m_body[2], buf, len);

    packet->m_hasAbsTimestamp = 0;
    packet->m_nBodySize = len + 2;
    packet->m_packetType = RTMP_PACKET_TYPE_AUDIO;
    packet->m_nChannel = 0x11;
    packet->m_headerType = RTMP_PACKET_SIZE_LARGE;
    return 0;
}

void AudioChannel::setCallback(VideoCallback callback) {
    this->callback = callback;
}

int AudioChannel::getMinBuferSize() {
    return inputByteNum;
}
