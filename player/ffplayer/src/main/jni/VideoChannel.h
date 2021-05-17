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

private:
    int width;
    int height;
    int fps;
    int bitrate;
    int ySize;
    int uvSize;
    x264_t *videoCodec;
    VideoCallback videoCallback;
};


#endif //UIPRACTICE_VIDEOCHANNEL_H
