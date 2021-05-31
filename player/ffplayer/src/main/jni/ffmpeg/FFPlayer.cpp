//
// Created by Luffy on 27/5/2021.
//

#include <log/log.h>
#include "FFPlayer.h"
#include "pthread.h"
#include <unistd.h>

extern "C" {
#include "libswresample/swresample.h"
}

FFPlayer::FFPlayer(FFPlayerJavaCallback *callback, char *url,Playerstatus *status) : url(url),status(status) {
    this->callback = callback;

}

FFPlayer::~FFPlayer() {
    if(audio != NULL) {
        delete audio;
        audio = NULL;
    }
    if(callback != NULL) {
        delete callback;
        callback = NULL;
    }
    avformat_close_input(&avFormatContext);
//    pthread_exit(&prepare_thread);
    LOGD("FFPlayer ~释放");
}

void *prepareThreadCall(void *data) {
    FFPlayer *player = static_cast<FFPlayer *>(data);
    player->decodeFFmpegThread();
    pthread_exit(&player->prepare_thread);
}

void FFPlayer::prepare() {
    pthread_create(&prepare_thread, NULL, prepareThreadCall, this);
//    decodeFFmpegThread();
}

int FFPlayer::decodeFFmpegThread() {

    avcodec_register_all();
    // 网络数据
    avformat_network_init();
    // 视频信息上下文，视频的封装信息都存在这里面
    AVFormatContext *avFormatContext = avformat_alloc_context();

    if (!avFormatContext) {
        return -1;
    }

    this->avFormatContext = avFormatContext;

    if (avformat_open_input(&avFormatContext, url, NULL, NULL) != 0) {
        LOGE("打开视频失败");
        status->setStatus(Playerstatus::EXIT);
        return -1;
    }

    LOGD("open video success");
    if (avformat_find_stream_info(avFormatContext, NULL) < 0) {
        LOGE("find_stream failure");
        status->setStatus(Playerstatus::EXIT);
        return -1;
    }

    int videoIndex = -1;
    int audioIndex = -1;
    for (int i = 0; i < avFormatContext->nb_streams; ++i) {
//        if (avFormatContext->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
//            // 视频流
//            videoIndex = i;
//            videoIndex = videoIndex;
//            // 找解码器
//            // video
//            LOGD("videoIndex=%d",videoIndex);
//            AVCodecContext *videoCodecContext = avFormatContext->streams[videoIndex]->codec;
//            AVCodec *videoCodec = avcodec_find_decoder(videoCodecContext->codec_id);
//
//            if (avcodec_open2(videoCodecContext,videoCodec,NULL) != 0) {
//                LOGE("open video codec failure");
//            } else {
//                width = videoCodecContext->width;
//                height = videoCodecContext->height;
//                videoCodec = videoCodec;
//            }
//        }
        if (avFormatContext->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
            // audio
            if (audio == NULL) {
                audio = new FFAudio(callback,status);
            }
            audioIndex = i;
            LOGD("audioIndex=%d", audioIndex);
            // 解码器参数，都放这里
            AVCodecParameters *codecpar = avFormatContext->streams[i]->codecpar;
//            // 解码器id
//            AVCodecID codecId = codecpar->codec_id;
//            AVCodec *audioCodec = avcodec_find_decoder(codecId);
//            AVCodecContext *audioCodecContext = avcodec_alloc_context3(audioCodec);
            AVCodecContext *audioCodecContext = avFormatContext->streams[audioIndex]->codec;
            AVCodec *audioCodec = avcodec_find_decoder(
                    avFormatContext->streams[i]->codecpar->codec_id);
            if (avcodec_open2(audioCodecContext, audioCodec, NULL) != 0) {
                LOGE("open audio codec failure");
            } else {
                duration = avFormatContext->duration / AV_TIME_BASE;// 微秒-> 秒
                audio->duration = duration;
                audio->time_base = audioCodecContext->time_base;
                audio->streamIndex = audioIndex;
//                audio->codecpar = codecpar;
                audio->codecContext = audioCodecContext;
//                audio->audioCodec = audioCodec;
                // 采样位数
                audio->sample_fmts = audioCodec->sample_fmts;
                // 采样频率
                audio->sample_rate = codecpar->sample_rate;
                audio->channels = codecpar->channels;
                audio->channel_layout = codecpar->channel_layout;
                LOGD("FFaudio init success");
            }
        }
    }

    if (videoIndex == -1) {
        LOGE("Couldn't find video stream");
    }

    if (audioIndex == -1) {
        LOGE("Couldn't find audio stream");
    }

    if (audioIndex == -1 && videoIndex == -1) {
        LOGE("Couldn't find stream");
        status->setStatus(Playerstatus::EXIT);
        return -1;
    }

    LOGD("ffmpeg init success");
    callback->onPrepared(CHILD_THREAD);
    return 0;
}

void FFPlayer::start() {
    status->setStatus(Playerstatus::PLAYING);
    audio->start();
    while (status != NULL && !status->isExit()) {

        AVPacket *avPacket = av_packet_alloc();
        if (av_read_frame(avFormatContext, avPacket) >= 0) {
            // 音频 丢入列队
            if (avPacket->stream_index == audio->streamIndex) {
                playerLock.lock();
                audio->queue.push(avPacket);
                playerLock.unlock();
            }
        } else {
            av_packet_free(&avPacket);
            av_free(avPacket);
            while(status != NULL && !status->isExit())
            {
                if(audio->queue.size() > 0)
                {
                    continue;
                } else{
                    status->setStatus(Playerstatus::EXIT);
                    break;
                }
            }
        }
//        playerLock.unlock();
    }

    LOGD("ffplayer 结束");
//    avformat_close_input(&avFormatContext);
}

void FFPlayer::pause() {
    if (audio != NULL) {
        audio->pause();
    }
}

void FFPlayer::resume() {
    if (audio != NULL) {
        audio->resume();
    }
}

void FFPlayer::stop() {
    if (audio != NULL) {
        audio->stop();
    }
}

void FFPlayer::seek(jint secds) {
    if(duration <= 0) {
        return;
    }
    if(secds >= 0 && secds <= duration && audio != NULL) {
        uint64_t rel = secds * AV_TIME_BASE;
        playerLock.lock();
        status->setStatus(Playerstatus::SEEKING);
        audio->queue.clear();
        audio->last_time = 0;
        audio->clock = 0;
        audio->frame_time = 0;
        avformat_seek_file(avFormatContext, -1, INT64_MIN, rel, INT64_MAX, 0);
        status->setStatus(Playerstatus::PLAYING);
//        audio->queue.notify();
        playerLock.unlock();
    }



}

void FFPlayer::setVolume(int volume) {
    if (audio != NULL) {
        audio->setVolume(volume);
    }
}

