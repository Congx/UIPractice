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
    pthread_mutex_init(&codecMutex, NULL);
}

FFVideo::~FFVideo() {
    pthread_mutex_unlock(&codecMutex);
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
        pthread_mutex_lock(&codecMutex);
        if(avcodec_send_packet(codecContext,avPacket) != 0) {
            av_packet_free(&avPacket);
            av_free(avPacket);
            avPacket = NULL;
            pthread_mutex_unlock(&codecMutex);
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
            pthread_mutex_unlock(&codecMutex);
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
            pthread_mutex_unlock(&codecMutex);
        }else {
            AVFrame *pFrameYUV420P = av_frame_alloc();
            int num = av_image_get_buffer_size(
                    AV_PIX_FMT_YUV420P,
                    width,
                    height,
                    1);
            uint8_t *buffer = static_cast<uint8_t *>(av_malloc(num * sizeof(uint8_t)));
            av_image_fill_arrays(
                    pFrameYUV420P->data,
                    pFrameYUV420P->linesize,
                    buffer,
                    AV_PIX_FMT_YUV420P,
                    width,
                    height,
                    1);
            SwsContext *sws_ctx = sws_getContext(
                    width,
                    height,
                    pix_fmt,
                    width,
                    height,
                    AV_PIX_FMT_YUV420P,
                    SWS_BICUBIC, NULL, NULL, NULL);

            if(!sws_ctx)
            {
                av_frame_free(&pFrameYUV420P);
                av_free(pFrameYUV420P);
                av_free(buffer);
                pthread_mutex_unlock(&codecMutex);
                continue;
            }
            sws_scale(
                    sws_ctx,
                    reinterpret_cast<const uint8_t *const *>(avFrame->data),
                    avFrame->linesize,
                    0,
                    avFrame->height,
                    pFrameYUV420P->data,
                    pFrameYUV420P->linesize);
            //渲染
            callback->onCallRenderYUV(
                    width,
                    height,
                    pFrameYUV420P->data[0],
                    pFrameYUV420P->data[1],
                    pFrameYUV420P->data[2]);

            av_frame_free(&pFrameYUV420P);
            av_free(pFrameYUV420P);
            av_free(buffer);
            sws_freeContext(sws_ctx);
        }
        av_frame_free(&avFrame);
        av_free(avFrame);
        avFrame = NULL;
        av_packet_free(&avPacket);
        av_free(avPacket);
        avPacket = NULL;
        pthread_mutex_unlock(&codecMutex);
    }
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

void FFVideo::stop() {
    queue.clear();
}

void FFVideo::release() {
    queue.setWork(0);
    if(audio != NULL) {
        audio = NULL;
    }

    if(codecContext != NULL) {
        avcodec_close(codecContext);
        avcodec_free_context(&codecContext);
        codecContext = NULL;
    }

    if(status != NULL) {
        status = NULL;
    }
}

