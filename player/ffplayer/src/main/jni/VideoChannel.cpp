//
// Created by Luffy on 17/5/2021.
//

#include "VideoChannel.h"

VideoChannel::~VideoChannel() {
    LOGI("VideoChannel release");
}

void VideoChannel::setVideoEncodeInfo(int width, int height, int fps, int bitrate) {
    this->width = width;
    this->height = height;
    this->fps = fps;
    this->bitrate = bitrate;

    ySize = height*height;
    uvSize = ySize/4;

    // 初始化之前先销毁
    if (videoCodec) {
        x264_encoder_close(videoCodec);
        videoCodec = NULL;
    }



}

void VideoChannel::setVideoCallback(VideoChannel::VideoCallback videoCallback) {
    this->videoCallback = videoCallback;
}

