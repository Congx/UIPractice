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
#include "FFPlayerJavaCallback.h"
#include "Playerstatus.h"
#include "soundtouch/include/SoundTouch.h"

extern "C" {
#include "libavutil/time.h"
#include "libswresample/swresample.h"
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libavutil/imgutils.h"
#include "libswscale/swscale.h"
}


FFPlayer *player = NULL;
FFPlayerJavaCallback *javaCallback = NULL;
Playerstatus *playerStatus = NULL;
JavaVM* javaVM;

//回调函数 在这里面注册函数
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
    JNIEnv *env = NULL;
    //判断虚拟机状态是否有问题
    if (vm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return -1;
    }
    assert(env != NULL);
    javaVM = vm;
    //返回jni 的版本
    return JNI_VERSION_1_6;
}

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
Java_com_xc_ffplayer_ffplayer_FFPlayer_nativePrepare(JNIEnv *env, jobject thiz, jstring jurl) {
    if (player == NULL) {
        if (javaCallback == NULL) {
            javaCallback = new FFPlayerJavaCallback(javaVM,env,&thiz);
        }
        if(playerStatus == NULL) {
            playerStatus = new Playerstatus();
        }
        char *url = const_cast<char *>(env->GetStringUTFChars(jurl, NULL));
        player = new FFPlayer(javaCallback,url,playerStatus);
        player->prepare();
        player->status->setStatus(Playerstatus::PREPARE);
    }
    return 0;
}

extern "C"
JNIEXPORT int JNICALL
Java_com_xc_ffplayer_ffplayer_FFPlayer_nativeStart(JNIEnv *env, jobject thiz, jobject surface) {

    if (player != NULL) {
        player->start();
    }

//
//    player->status = START;
//
//    ANativeWindow* nativeWindow = ANativeWindow_fromSurface(env,surface);
//    if (!nativeWindow) {
//        LOGE("can't open native window");
//        return -1;
//    }
//
//    AVFrame *avFrame = av_frame_alloc();
//    AVPacket *avPacket = av_packet_alloc();
//    AVFrame *rgbFrame = av_frame_alloc();

//
//    int width = player->width;
//    int height = player->height;
//    LOGD("video width=%d,height=%d",width,height);
//    int bufferSize = av_image_get_buffer_size(AV_PIX_FMT_RGBA,width,height,1);
//    LOGD("bufferSize=%d",bufferSize);
//
//    uint8_t *outBuffer = static_cast<uint8_t *>(malloc(bufferSize * sizeof(uint8_t)));
//    // 格式化数据
//    av_image_fill_arrays(rgbFrame->data,rgbFrame->linesize,outBuffer,AV_PIX_FMT_RGBA,width,height,1);
//
//    SwsContext *swsContext = sws_getContext(width,height,player->getPix_fmt(),width,height,AV_PIX_FMT_RGBA,SWS_BICUBIC,NULL,NULL,NULL);
//
//    // audio 相关 ----
//    // 输出的通道，立体声、左右声道
//    // 音频的转换器
//    SwrContext *swrContext = swr_alloc();
//    uint64_t out_ch_layout = AV_CH_LAYOUT_STEREO; // 声道
//    int out_sample_rate = player->audio->sample_rate; // 采样频率
//    AVSampleFormat out_sample_fmt = AV_SAMPLE_FMT_S16; // 采样位数
//    // 一个采样几个字节
//    int sampleByteCount = av_get_bytes_per_sample(out_sample_fmt);
//    LOGD("sampleByteCount：%d",sampleByteCount);
//    swr_alloc_set_opts(swrContext,
//                        // 输出到喇叭的参数，抓换后的参数
//                        out_ch_layout,
//                        out_sample_fmt,
//                        out_sample_rate,
//                       //  视频文件的参数
//                       player->audio->channel_layout,
//                       *player->audio->sample_fmts,
//                       player->audio->sample_rate,
//                       0,NULL);
//    swr_init(swrContext);
//    uint8_t *outAudioBuffer = static_cast<uint8_t *>(av_malloc(out_sample_rate * sampleByteCount));
////    // 通道数
//    int out_sample_nb = av_get_channel_layout_nb_channels(out_ch_layout);
//    LOGD("out_sample_nb：%d",out_sample_nb);
    // 回调java 根据参数初始化AudioTrack
//    jclass jclazz = env->GetObjectClass(thiz);
//    jmethodID createAudioTrack = env->GetMethodID(jclazz,"createAudioTrack","(III)V");
//    LOGD("采样通道数：%d",out_sample_nb);
//    LOGD("采样频率：%d",out_sample_rate);
//    env->CallVoidMethod(thiz,createAudioTrack,out_sample_nb,out_sample_rate,2);
//    jmethodID playAudio = env->GetMethodID(jclazz,"playAudio","([BI)V");

//    player->callback->createAudioTrack()
//    // audio 相关 ---- END
//
//    if (ANativeWindow_setBuffersGeometry(nativeWindow,width,height,WINDOW_FORMAT_RGBA_8888) < 0) {
//        LOGD("Couldn't set buffers geometry");
//        return -1;
//    }
//
//    ANativeWindow_Buffer windowBuffer;
//
//    while (av_read_frame(player->avFormatContext,avPacket) >= 0 && player->status == START) {
//        LOGD("解码");
//        // 音频
//        if (avPacket->stream_index == player->audio->streamIndex) {
//            LOGD("音频解码");
//            int ret = avcodec_send_packet(player->audio->codecContext,avPacket);
//            if (ret < 0 && AVERROR(EAGAIN) && ret != AVERROR_EOF) {
//                LOGE("音频解码出错");
//                break;
//            }
//
//            ret = avcodec_receive_frame(player->audio->codecContext,avFrame);
//            if (ret == AVERROR(EAGAIN)) {
//                continue;
//            } else if (ret != 0){
//                LOGE("音频解码receive_frame出错");
//                break;
//            }
//            // 重采样，转换 采样的通道数、频率等数据，按照swrContext的参数来转换
//            swr_convert(swrContext, &outAudioBuffer,out_sample_rate*2,
//                        (const uint8_t **)avFrame->data, avFrame->nb_samples);

//            int size = av_samples_get_buffer_size(NULL,out_sample_nb,avFrame->nb_samples,AV_SAMPLE_FMT_S16,1);
//
//            jbyteArray arr = env->NewByteArray(size);
//            env->SetByteArrayRegion(arr, 0, size, reinterpret_cast<const jbyte *>(outAudioBuffer));

//            env->CallVoidMethod(thiz,playAudio,arr,size);
//            env->DeleteLocalRef(arr);
//        }
//
//    }
//
//    av_frame_free(&avFrame);
//    av_free(avPacket);
//    av_free(swsContext);
//    swr_free(&swrContext);
//    avformat_close_input(&player->avFormatContext);
    LOGD("解码结束");
    return 0;
}

extern "C"
JNIEXPORT void JNICALL
Java_com_xc_ffplayer_ffplayer_FFPlayer_nativePause(JNIEnv *env, jobject thiz) {
    if (player) {
        player->status->setStatus(Playerstatus::PAUSEE);
        player->pause();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_xc_ffplayer_ffplayer_FFPlayer_nativeResume(JNIEnv *env, jobject thiz) {
    if (player != NULL) {
        player->status->setStatus(Playerstatus::PLAYING);
        player->resume();
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_xc_ffplayer_ffplayer_FFPlayer_nativeStop(JNIEnv *env, jobject thiz) {
    if (player != NULL) {
        player->stop();
        player->status->setStatus(Playerstatus::EXIT);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_xc_ffplayer_ffplayer_FFPlayer_nativeSeek(JNIEnv *env, jobject thiz, jint progress) {
    if (player != NULL) {
        player->seek(progress);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_xc_ffplayer_ffplayer_FFPlayer_nativeSetVolume(JNIEnv *env, jobject thiz, jint volume) {
    if (player != NULL) {
        player->setVolume(volume);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_xc_ffplayer_ffplayer_FFPlayer_nativeSetMute(JNIEnv *env, jobject thiz, jint mute) {
    if (player != NULL) {
        player->setMute(mute);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_xc_ffplayer_ffplayer_FFPlayer_nativeSetSpeed(JNIEnv *env, jobject thiz, jfloat speed) {
    if (player != NULL) {
        player->setSpeed(speed);
    }
}extern "C"
JNIEXPORT void JNICALL
Java_com_xc_ffplayer_ffplayer_FFPlayer_nativeSetPitch(JNIEnv *env, jobject thiz, jfloat pitch) {
    if (player != NULL) {
        player->setPitch(pitch);
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_xc_ffplayer_ffplayer_FFPlayer_nativeRelease(JNIEnv *env, jobject thiz) {
    if (player != NULL) {
        delete player;
        player = NULL;
        javaCallback = NULL;
        playerStatus = NULL;
    }
}