//
// Created by Luffy on 17/5/2021.
//

#ifndef UIPRACTICE_VIDEOCHANNEL_H
#define UIPRACTICE_VIDEOCHANNEL_H

#include "jni.h"
#include "stdint.h"
#include "log/log.h"
#include "rtmp.h"
#include "x264.h"


class VideoChannel {
    typedef void (*VideoCallback)(RTMPPacket *packet);
public:
    ~VideoChannel();
    void setVideoEncodeInfo(int width, int height, int fps, int bitrate);
    void setVideoCallback(VideoCallback videoCallback);
    void encodeData(uint8_t *data);
    void sendSpsPps(uint8_t *sps,uint8_t *pps,int sps_len,int pps_len);
    void sendFrame(int type,int len,uint8_t *data);

private:
    int width;
    int height;
    int fps;
    int bitrate;
    int ySize;
    int uvSize;
    x264_t *videoCodec;
    x264_picture_t *pic_in;
    VideoCallback videoCallback;
};


#endif //UIPRACTICE_VIDEOCHANNEL_H
