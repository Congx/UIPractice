//
// Created by Luffy on 26/5/2021.
//

#include "jni.h"
#include "log.h"
#include "string"
#include "string"
#include "android/native_window_jni.h"
#include "android/native_window.h"
#include "FFPlayer.h"

extern "C" {
#include "libavutil/time.h"
}


FFPlayer *player = new FFPlayer;

extern "C"
JNIEXPORT jstring JNICALL
Java_com_xc_ffplayer_ffplayer_FFPlayer_nativeffmpegInfo(JNIEnv *env, jobject thiz) {
    char *info = const_cast<char *>(av_version_info());
    int version = avcodec_version();
    LOGD("ffmpeg version=%d",version);
    LOGD("ffmpeg info=%s",info);
    return env->NewStringUTF(info);
}

extern "C"
JNIEXPORT int JNICALL
Java_com_xc_ffplayer_ffplayer_FFPlayer_nativeSetPath(JNIEnv *env, jobject thiz, jstring jurl) {
    const char* url = env->GetStringUTFChars(jurl,NULL);
    avcodec_register_all();
    // 视频信息上下文，视频的封装信息都存在这里面
    AVFormatContext * avFormatContext = avformat_alloc_context();

    if (!avFormatContext) {
        return -1;
    }

    player->avFormatContext = avFormatContext;
    if (avformat_open_input(&avFormatContext,url,NULL,NULL) != 0) {
        LOGE("打开视频失败");
        player->status = FAILURE;
        return -1;
    }

    LOGD("open video success");
    if (avformat_find_stream_info(avFormatContext,NULL) < 0) {
        LOGE("find_stream failure");
        player->status = FAILURE;
        return -1;
    }

    int videoIndex = -1;
    int audioIndex = -1;
    for (int i = 0; i < avFormatContext->nb_streams; ++i) {
        if (avFormatContext->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            // 视频流
            videoIndex = i;
            player->videoIndex = videoIndex;
        }
        if (avFormatContext->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
            // 音频流
            audioIndex = i;
            player->audioIndex = audioIndex;
        }
    }

    if (videoIndex == -1) {
        LOGE("Couldn't find video stream");
    }

    if (audioIndex == -1) {
        LOGE("Couldn't find audio stream");
    }

    if (audioIndex == -1 && videoIndex == -1) {
        LOGE("Couldn't find audio stream");
        player->status = FAILURE;
        return -1;
    }
    LOGD("find stream success");

    // 找解码器
    // video
    AVCodecContext *videoCodecContext = avFormatContext->streams[videoIndex]->codec;
    AVCodec *videoCodec = avcodec_find_decoder(videoCodecContext->codec_id);

    if (avcodec_open2(videoCodecContext,videoCodec,NULL) != 0) {
        LOGE("open video codec failure");
    } else {
        player->width = videoCodecContext->width;
        player->height = videoCodecContext->height;
        player->videoCodec = videoCodec;
    }

    // audio
    AVCodecContext *audioCodecContext = avFormatContext->streams[audioIndex]->codec;
    AVCodec *audioCodec = avcodec_find_decoder(videoCodecContext->codec_id);

    if (avcodec_open2(audioCodecContext,audioCodec,NULL) != 0) {
        LOGE("open audio codec failure");
    } else {
        player->audioCodec = audioCodec;
    }

    if (!player->videoCodec && !player->audioCodec) {
        LOGE("can't open codec");
        player->status = FAILURE;
        return -1;
    }

    LOGD("ffmpeg init success");
    return 0;
}

extern "C"
JNIEXPORT int JNICALL
Java_com_xc_ffplayer_ffplayer_FFPlayer_nativeStart(JNIEnv *env, jobject thiz, jobject surface) {
    if (player->status == FAILURE) {
        return -1;
    }

    ANativeWindow* nativeWindow = ANativeWindow_fromSurface(env,surface);
    if (!nativeWindow) {
        LOGE("can't open native window");
        return -1;
    }

    AVFrame *avFrame = av_frame_alloc();
    AVPacket *avPacket = av_packet_alloc();
    AVFrame *rgbFrame = av_frame_alloc();

    int width = player->width;
    int height = player->height;
    LOGD("video width=%d,height=%d",width,height);
    int bufferSize = av_image_get_buffer_size(AV_PIX_FMT_RGBA,width,height,1);
    LOGD("bufferSize=%d",bufferSize);

    uint8_t *outBuffer = static_cast<uint8_t *>(malloc(bufferSize * sizeof(uint8_t)));

    av_image_fill_arrays(rgbFrame->data,rgbFrame->linesize,outBuffer,AV_PIX_FMT_RGBA,width,height,1);

    SwsContext *swsContext = sws_getContext(width,height,player->getPix_fmt(),width,height,AV_PIX_FMT_RGBA,SWS_BICUBIC,NULL,NULL,NULL);

    if (ANativeWindow_setBuffersGeometry(nativeWindow,width,height,WINDOW_FORMAT_RGBA_8888) < 0) {
        LOGD("Couldn't set buffers geometry");
        return -1;
    }

    ANativeWindow_Buffer windowBuffer;

    while (av_read_frame(player->avFormatContext,avPacket) >= 0) {
        if (avPacket->stream_index == player->videoIndex)  {
            int ret = avcodec_send_packet(player->getVideoCodecContext(),avPacket);
            if (ret < 0 && AVERROR(EAGAIN) && ret != AVERROR_EOF) {
                LOGE("解码出错");
                return -1;
            }

            ret = avcodec_receive_frame(player->getVideoCodecContext(),avFrame);
            if (ret == AVERROR(EAGAIN)) {
                continue;
            } else if (ret < 0){
                break;
            }

            sws_scale(swsContext,avFrame->data,avFrame->linesize,0,height,rgbFrame->data,rgbFrame->linesize);

            if (ANativeWindow_lock(nativeWindow,&windowBuffer,NULL) < 0) {
                LOGE("解码出错");
            } else {
                uint8_t *dst = static_cast<uint8_t *>(windowBuffer.bits);
                for (int i = 0; i < height; ++i) {
                    memcpy(dst+i*windowBuffer.stride*4,outBuffer+i*rgbFrame->linesize[0],rgbFrame->linesize[0]);

//                    switch (avFrame->pict_type) {
//                        case AV_PICTURE_TYPE_I:
//                            LOGE("I");
//                            break;
//                        case AV_PICTURE_TYPE_P:
//                            LOGE("P");
//                            break;
//                        case AV_PICTURE_TYPE_B:
//                            LOGE("B");
//                            break;
//                    }
                }
            }

            av_usleep(1000*30);
            ANativeWindow_unlockAndPost(nativeWindow);

        }
    }

}

extern "C"
JNIEXPORT void JNICALL
Java_com_xc_ffplayer_ffplayer_FFPlayer_nativePause(JNIEnv *env, jobject thiz) {
    // TODO: implement NativePause()
}

extern "C"
JNIEXPORT void JNICALL
Java_com_xc_ffplayer_ffplayer_FFPlayer_nativeResume(JNIEnv *env, jobject thiz) {
    // TODO: implement NativeResume()
}

extern "C"
JNIEXPORT void JNICALL
Java_com_xc_ffplayer_ffplayer_FFPlayer_nativeStop(JNIEnv *env, jobject thiz) {
    if (player) {
        delete player;
        player = NULL;
    }
}