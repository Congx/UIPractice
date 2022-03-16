//
// Created by xucong on 2021/6/7.
//

#ifndef UIPRACTICE_FFVIDEO_H
#define UIPRACTICE_FFVIDEO_H


#include "Playerstatus.h"
#include "FFPlayerJavaCallback.h"
#include <safe_queue.h>
#include <pthread.h>
#include "FFAudio.h"

extern "C" {
#include "libavcodec/packet.h"
#include "libavutil/time.h"
#include "libavcodec/avcodec.h"
#include "libavutil/imgutils.h"
#include "libswscale/swscale.h"
}

class FFVideo {
public:
    FFVideo(FFPlayerJavaCallback *callback,Playerstatus *status);
    ~FFVideo();
    void start();
    void release();
    double getFrameDiffTime(AVFrame *avFrame);
    double getDelayTime(double diff);
    void decodeVideo();
    void stop();

public:
    FFAudio *audio = NULL;
    SafeQueue<AVPacket *> queue;
    FFPlayerJavaCallback *callback = NULL;
    AVCodecContext *codecContext = NULL;
    AVPixelFormat pix_fmt;
    Playerstatus *status = NULL;
    pthread_t decode_thread = NULL;
    int streamIndex = 0;
    AVRational time_base;
    int fps;

    int width = 0;
    int height = 0;
    double clock = 0;
    double defaultDelayTime = 0;

    pthread_mutex_t codecMutex;

};


#endif //UIPRACTICE_FFVIDEO_H
