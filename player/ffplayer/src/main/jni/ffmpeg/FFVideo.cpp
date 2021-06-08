//
// Created by xucong on 2021/6/7.
//

#include "FFVideo.h"


void videoRealseCallback(AVPacket *&packet) {
    av_packet_free(&packet);
//    LOGD("audio queue free");
}

FFVideo::FFVideo(FFPlayerJavaCallback *callback, Playerstatus *status) : callback(callback),
                                                                         status(status){
    queue.setReleaseCallback(videoRealseCallback);
}

FFVideo::~FFVideo() {

}

void *videoDecodeThread(void *context) {
    auto *video = static_cast<FFVideo *>(context);
    video->decodeVideo();
    pthread_exit(&video->decode_thread);
}

void FFVideo::start() {
    queue.setWork(1);
    pthread_create(&decode_thread, NULL, videoDecodeThread, this);
}

/**
 * 开始解码
 */
void FFVideo::decodeVideo() {
    while (status != NULL && !status->isExit()) {
        if (status->isSeeking()) {
            LOGD("video isSeeking...");
            av_usleep(1000 * 100);
            continue;
        }
        if (status->isPause()) {
            LOGD("video isPause...");
            av_usleep(1000 * 100);
            continue;
        }

//        if (status->isLoading()) {
//            av_usleep(1000 * 100);
//            continue;
//        }

        if(queue.size() <= 0) {
            status->setStatus(Playerstatus::LOADING);
            callback->onCallLoad(CHILD_THREAD, true);
            av_usleep(1000 * 100);
            LOGD("video loading...");
            continue;
        }else {
//            LOGD("video loading else...");
            if(status->isLoading()) {
                status->setStatus(Playerstatus::PLAYING);
                callback->onCallLoad(CHILD_THREAD, false);
            }
        }

        AVPacket *avPacket = av_packet_alloc();
//        LOGD("video queue size = %d",queue.size());
        queue.pop(avPacket);
//        LOGD("video queue pop");
        if(avcodec_send_packet(codecContext,avPacket) != 0) {
            av_packet_free(&avPacket);
            av_free(avPacket);
            avPacket = NULL;
            continue;
        }
        AVFrame *avFrame = av_frame_alloc();
        if(avcodec_receive_frame(codecContext,avFrame) != 0) {
            av_frame_free(&avFrame);
            av_free(avFrame);
            avFrame = NULL;
            av_packet_free(&avPacket);
            av_free(avPacket);
            avPacket = NULL;
            continue;
        }
        if(avFrame->format == AV_PIX_FMT_YUV420P) {
            double diff = getFrameDiffTime(avFrame);
            double delayTime = getDelayTime(diff) * AV_TIME_BASE;
            LOGD("video diff = %f,delayTime = %f",diff,delayTime);
            av_usleep(delayTime);
            callback->onCallRenderYUV(width,height,
                    avFrame->data[0],
                    avFrame->data[1],
                    avFrame->data[2]);
        }else {

        }
        av_frame_free(&avFrame);
        av_free(avFrame);
        avFrame = NULL;
        av_packet_free(&avPacket);
        av_free(avPacket);
        avPacket = NULL;
    }
}

void FFVideo::release() {

}

double FFVideo::getFrameDiffTime(AVFrame *avFrame) {
    uint64_t pts = avFrame->pts;
//    LOGD("video pts %d",pts);
    if(pts == AV_NOPTS_VALUE)
    {
        pts = 0;
    }
    pts *= av_q2d(time_base);
    if(pts > 0)
    {
        clock = pts;
    }

    return audio->clock - clock;
}

double FFVideo::getDelayTime(double diff) {
    double delayTime = 0;
    //视频超越  音频3ms   2
    if(diff > 0.003) {
//        视频休眠时间
        delayTime = delayTime * 2 / 3;// * 3/2;
        if (delayTime < defaultDelayTime / 2) {
//            用户有所察觉
            delayTime = defaultDelayTime * 2 / 3;
        }else if(delayTime > defaultDelayTime * 2) {
            delayTime = defaultDelayTime * 2;
        }
    } else if(diff < - 0.003)
    {
//视频超前    休眠时间 相比于以前大一些
        delayTime = delayTime * 3 / 2;
        if(delayTime < defaultDelayTime / 2)
        {
            delayTime = defaultDelayTime * 2 / 3;
        }
        else if(delayTime > defaultDelayTime * 2)
        {
            delayTime = defaultDelayTime * 2;
        }
    }
//感觉的 视频加速
    if (diff >= 0.5) {
        delayTime = 0;
    } else if(diff <= -0.5)
    {
        delayTime = defaultDelayTime * 2;
    }
//    音频太快了   视频怎么赶也赶不上        视频队列全部清空   直接解析最新的 最新鲜的
    if(diff>= 10)
    {
        queue.clear();
        delayTime = defaultDelayTime;
    }
//视频太快了  音频赶不上
    if (diff <= -10) {
        audio->queue.clear();
        delayTime = defaultDelayTime;
    }
    return delayTime;
}

