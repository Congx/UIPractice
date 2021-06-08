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
        if (avFormatContext->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            if(video == NULL) {
                video = new FFVideo(callback,status);
            }
            // 视频流
            videoIndex = i;
            LOGD("videoIndex=%d",videoIndex);
            AVCodecParameters *codecpar = avFormatContext->streams[i]->codecpar;
            AVCodec * videoCodec = avcodec_find_decoder(codecpar->codec_id);
            AVCodecContext *videoCodecContext = avcodec_alloc_context3(videoCodec);
            avcodec_parameters_to_context(videoCodecContext,codecpar);
//            AVCodecContext *videoCodecContext = avFormatContext->streams[videoIndex]->codec;
//            AVCodec *videoCodec = avcodec_find_decoder(codecpar->codec_id);
            if (avcodec_open2(videoCodecContext,videoCodec,NULL) != 0) {
                LOGD("open video codec failure");
            } else {
                video->streamIndex = videoIndex;
                video->width = codecpar->width;
                video->height = codecpar->height;
                video->codecContext = videoCodecContext;
                video->pix_fmt = videoCodecContext->pix_fmt;
                video->time_base = avFormatContext->streams[i]->time_base;
//                LOGD("video width = %d,height = %d",video->width,video->height);
//                LOGD("video time_base den = %d,num = %d",video->time_base.den,video->time_base.num);
                int den = avFormatContext->streams[i]->avg_frame_rate.den;
                int num = avFormatContext->streams[i]->avg_frame_rate.num;
//                LOGD("video avg_frame_rate den = %d,num = %d",den,num);
                video->fps = num / den;
//                LOGD("fps = %d",video->fps);
                video->defaultDelayTime = 1/(double)video->fps;
                LOGD("defaultDelayTime = %f",video->defaultDelayTime);
            }
        }else if (avFormatContext->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
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
            AVCodec *audioCodec = avcodec_find_decoder(audioCodecContext->codec_id);
            avcodec_parameters_to_context(audioCodecContext, codecpar);
            if (avcodec_open2(audioCodecContext, audioCodec, NULL) != 0) {
                LOGE("open audio codec failure");
            } else {
                duration = avFormatContext->duration / AV_TIME_BASE;// 微秒-> 秒
                audio->duration = duration;
                audio->time_base = avFormatContext->streams[i]->time_base;
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
    int width = 0;
    int height = 0;
    int fps = 0;
    if(video != NULL) {
        width = video->width;
        height = video->height;
        fps = video->fps;
    }
    callback->onPrepared(width,height,fps,CHILD_THREAD);
    return 0;
}

void FFPlayer::start() {
    status->setStatus(Playerstatus::PLAYING);
    audio->start();
    video->audio = audio;
    video->start();
    while (status != NULL && !status->isExit()) {
        AVPacket *avPacket = av_packet_alloc();
        if (av_read_frame(avFormatContext, avPacket) >= 0) {
            // 音频 丢入列队
            playerLock.lock();
            if (avPacket->stream_index == audio->streamIndex) {
                audio->queue.push(avPacket);
            }else if(avPacket->stream_index == video->streamIndex) {
//                LOGD("video queue push");
                video->queue.push(avPacket);
            }
            playerLock.unlock();
        } else {
            av_packet_free(&avPacket);
            av_free(avPacket);
            while(status != NULL && !status->isExit())
            {
                if(audio->queue.size() > 0)
                {
//                    LOGD("video queue continue");
                    continue;
                } else{
//                    LOGD("video queue EXIT");
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
    if (video != NULL) {
        video->stop();
    }
}

void FFPlayer::seek(jint secds) {
    if(duration <= 0) {
        return;
    }
    if(secds >= 0 && secds <= duration ) {
        uint64_t rel = secds * AV_TIME_BASE;
        playerLock.lock();
        status->setStatus(Playerstatus::SEEKING);
        avformat_seek_file(avFormatContext, -1, INT64_MIN, rel, INT64_MAX, 0);
        if(audio != NULL) {
            audio->queue.clear();
            audio->last_time = 0;
            audio->clock = 0;
            audio->frame_time = 0;
            avcodec_flush_buffers(audio->codecContext);
        }
        if(video != NULL) {
            video->queue.clear();
            pthread_mutex_lock(&video->codecMutex);
            avcodec_flush_buffers(video->codecContext);
            pthread_mutex_unlock(&video->codecMutex);
        }
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

void FFPlayer::setMute(int mute) {
    if (audio != NULL) {
        audio->setMute(mute);
    }
}

void FFPlayer::setPitch(float pitch) {
    if (audio != NULL) {
        audio->setPitch(pitch);
    }
}

void FFPlayer::setSpeed(float speed) {
    if (audio != NULL) {
        audio->setSpeed(speed);
    }
}

void FFPlayer::release() {
    if(audio != NULL) {
        audio->release();
        delete audio;
        audio = NULL;
    }
    if(video != NULL) {
        video->release();
        delete video;
        video = NULL;
    }
    if(callback != NULL) {
        delete callback;
        callback = NULL;
    }
    avformat_close_input(&avFormatContext);
    LOGD("FFPlayer ~释放");
}

