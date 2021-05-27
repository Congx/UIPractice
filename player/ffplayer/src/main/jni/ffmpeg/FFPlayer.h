//
// Created by Luffy on 27/5/2021.
//

#ifndef UIPRACTICE_FFPLAYER_H
#define UIPRACTICE_FFPLAYER_H

extern "C"{
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libavutil/imgutils.h"
#include "libswscale/swscale.h"
}

enum STATUES {
    UNUSE,INIT,START,STOP,FAILURE
};

class FFPlayer {

public:

    int videoIndex;
    int audioIndex;
    int width;
    int height;
    AVFormatContext * avFormatContext;
    AVCodec *videoCodec;
    AVCodec *audioCodec;
    STATUES status;

    AVPixelFormat getPix_fmt();
    AVCodecContext* getVideoCodecContext();
};


#endif //UIPRACTICE_FFPLAYER_H
