//
// Created by Luffy on 27/5/2021.
//

#include "FFPlayer.h"

AVPixelFormat FFPlayer::getPix_fmt() {
    return avFormatContext->streams[videoIndex]->codec->pix_fmt;
}

AVCodecContext *FFPlayer::getVideoCodecContext() {
    return avFormatContext->streams[videoIndex]->codec;
}
