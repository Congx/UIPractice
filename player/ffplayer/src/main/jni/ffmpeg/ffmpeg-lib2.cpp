//
// Created by Luffy on 26/5/2021.
//

//#include "jni.h"
//#include "log.h"
//#include "string"
//#include "string"
//#include "android/native_window_jni.h"
//#include "android/native_window.h"
//#include "FFPlayer.h"
//
//extern "C" {
//#include "libavutil/time.h"
//#include "libswresample/swresample.h"
//}
//
//
//FFPlayer *player = new FFPlayer;
//
//extern "C"
//JNIEXPORT jstring JNICALL
//Java_com_xc_ffplayer_ffplayer_FFPlayer_nativeffmpegInfo(JNIEnv *env, jobject thiz) {
//    char *info = const_cast<char *>(av_version_info());
//    int version = avcodec_version();
//    LOGD("ffmpeg version=%d",version);
//    LOGD("ffmpeg info=%s",info);
//    return env->NewStringUTF(info);
//}
//
//extern "C"
//JNIEXPORT int JNICALL
//Java_com_xc_ffplayer_ffplayer_FFPlayer_nativeSetPath(JNIEnv *env, jobject thiz, jstring jurl) {
//    const char* url = env->GetStringUTFChars(jurl,NULL);
//    avcodec_register_all();
//    // 网络数据
//    avformat_network_init();
//    // 视频信息上下文，视频的封装信息都存在这里面
//    AVFormatContext * avFormatContext = avformat_alloc_context();
//
//    if (!avFormatContext) {
//        return -1;
//    }
//
//    player->avFormatContext = avFormatContext;
//    if (avformat_open_input(&avFormatContext,url,NULL,NULL) != 0) {
//        LOGE("打开视频失败");
//        player->status = FAILURE;
//        return -1;
//    }
//
//    LOGD("open video success");
//    if (avformat_find_stream_info(avFormatContext,NULL) < 0) {
//        LOGE("find_stream failure");
//        player->status = FAILURE;
//        return -1;
//    }
//
//    int videoIndex = -1;
//    int audioIndex = -1;
//    for (int i = 0; i < avFormatContext->nb_streams; ++i) {
//        if (avFormatContext->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_VIDEO) {
//            // 视频流
//            videoIndex = i;
//            player->videoIndex = videoIndex;
//            // 找解码器
//            // video
//            LOGD("videoIndex=%d",videoIndex);
//            AVCodecContext *videoCodecContext = avFormatContext->streams[videoIndex]->codec;
//            AVCodec *videoCodec = avcodec_find_decoder(videoCodecContext->codec_id);
//
//            if (avcodec_open2(videoCodecContext,videoCodec,NULL) != 0) {
//                LOGE("open video codec failure");
//            } else {
//                player->width = videoCodecContext->width;
//                player->height = videoCodecContext->height;
//                player->videoCodec = videoCodec;
//            }
//        }
//        if (avFormatContext->streams[i]->codecpar->codec_type == AVMEDIA_TYPE_AUDIO) {
//            // 音频流
//            // audio
//            audioIndex = i;
//            player->audioIndex = audioIndex;
//            LOGD("audioIndex=%d",audioIndex);
//            // audio
//            AVCodecContext *audioCodecContext = avFormatContext->streams[audioIndex]->codec;
//            AVCodec *audioCodec = avcodec_find_decoder(audioCodecContext->codec_id);
//            if (avcodec_open2(audioCodecContext,audioCodec,NULL) != 0) {
//                LOGE("open audio codec failure");
//            } else {
//                player->audioCodec = audioCodec;
//            }
//        }
//    }
//
//    if (videoIndex == -1) {
//        LOGE("Couldn't find video stream");
//    }
//
//    if (audioIndex == -1) {
//        LOGE("Couldn't find audio stream");
//    }
//
//    if (audioIndex == -1 && videoIndex == -1) {
//        LOGE("Couldn't find audio stream");
//        player->status = FAILURE;
//        return -1;
//    }
//    LOGD("find stream success");
//
//    if (!player->videoCodec && !player->audioCodec) {
//        LOGE("can't open codec");
//        player->status = FAILURE;
//        return -1;
//    }
//
//    LOGD("ffmpeg init success");
//    env->ReleaseStringUTFChars(jurl,url);
//
//    return 0;
//}
//
//extern "C"
//JNIEXPORT int JNICALL
//Java_com_xc_ffplayer_ffplayer_FFPlayer_nativeStart(JNIEnv *env, jobject thiz, jobject surface) {
//    if (player->status == FAILURE) {
//        return -1;
//    }
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
////    AVFrame *avAudioFrame = av_frame_alloc();
////    AVPacket *avAudioPacket = av_packet_alloc();
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
//    int out_sample_rate = player->getAudioCodecContext()->sample_rate; // 采样频率
//    AVSampleFormat out_sample_fmt = AV_SAMPLE_FMT_S16; // 采样位数
//
//    swr_alloc_set_opts(swrContext,
//                        // 输出到喇叭的参数，抓换后的参数
//                        out_ch_layout,out_sample_fmt,out_sample_rate,
//                       //  视频文件的参数
//                       player->getAudioCodecContext()->channel_layout,
//                       player->getAudioCodecContext()->sample_fmt,
//                       player->getAudioCodecContext()->sample_rate,
//                       0,NULL);
//    swr_init(swrContext);
//    uint8_t *outAudioBuffer = static_cast<uint8_t *>(av_malloc(out_sample_rate * 2));
//    // 通道数
//    int out_sample_nb = av_get_channel_layout_nb_channels(out_ch_layout);
//    // 回调java 根据参数初始化AudioTrack
//    jclass jclazz = env->GetObjectClass(thiz);
//    jmethodID createAudioTrack = env->GetMethodID(jclazz,"createAudioTrack","(III)V");
//    LOGD("采样通道数：%d",out_sample_nb);
//    LOGD("采样频率：%d",out_sample_rate);
//    env->CallVoidMethod(thiz,createAudioTrack,out_sample_nb,out_sample_rate,2);
//    jmethodID playAudio = env->GetMethodID(jclazz,"playAudio","([BI)V");
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
//        // 视频
//        if (avPacket->stream_index == player->videoIndex)  {
//            int ret = avcodec_send_packet(player->getVideoCodecContext(),avPacket);
//            if (ret < 0 && AVERROR(EAGAIN) && ret != AVERROR_EOF) {
//                LOGE("视频解码出错");
//                break;
//            }
//
//            ret = avcodec_receive_frame(player->getVideoCodecContext(),avFrame);
//            if (ret == AVERROR(EAGAIN)) {
//                continue;
//            } else if (ret < 0){
//                break;
//            }
//
//            sws_scale(swsContext,avFrame->data,avFrame->linesize,0,height,rgbFrame->data,rgbFrame->linesize);
//
//            if (ANativeWindow_lock(nativeWindow,&windowBuffer,NULL) < 0) {
//                LOGE("lock window 出错");
//                break;
//            } else {
//                uint8_t *dst = static_cast<uint8_t *>(windowBuffer.bits);
//                for (int i = 0; i < height; ++i) {
//                    memcpy(dst+i*windowBuffer.stride*4,outBuffer+i*rgbFrame->linesize[0],rgbFrame->linesize[0]);
//
////                    switch (avFrame->pict_type) {
////                        case AV_PICTURE_TYPE_I:
////                            LOGD("I");
////                            break;
////                        case AV_PICTURE_TYPE_P:
////                            LOGD("P");
////                            break;
////                        case AV_PICTURE_TYPE_B:
////                            LOGD("B");
////                            break;
////                    }
//                }
//            }
//
//            ANativeWindow_unlockAndPost(nativeWindow);
//            av_usleep(1000*20);
//        }
//
//        // 音频
//        if (avPacket->stream_index == player->audioIndex) {
//            LOGD("音频解码");
//            int ret = avcodec_send_packet(player->getAudioCodecContext(),avPacket);
//            if (ret < 0 && AVERROR(EAGAIN) && ret != AVERROR_EOF) {
//                LOGE("音频解码出错");
//                break;
//            }
//
//            ret = avcodec_receive_frame(player->getAudioCodecContext(),avFrame);
//            if (ret == AVERROR(EAGAIN)) {
//                continue;
//            } else if (ret != 0){
//                LOGE("音频解码receive_frame出错");
//                break;
//            }
////            // 重采样，转换 采样的通道数、频率等数据，按照swrContext的参数来转换
//            swr_convert(swrContext, &outAudioBuffer,out_sample_rate*2,
//                        (const uint8_t **)avFrame->data, avFrame->nb_samples);
//
//            int size = av_samples_get_buffer_size(NULL,out_sample_nb,avFrame->nb_samples,AV_SAMPLE_FMT_S16,1);
//
//            jbyteArray arr = env->NewByteArray(size);
//            env->SetByteArrayRegion(arr, 0, size, reinterpret_cast<const jbyte *>(outAudioBuffer));
//
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
//}
//
//extern "C"
//JNIEXPORT void JNICALL
//Java_com_xc_ffplayer_ffplayer_FFPlayer_nativePause(JNIEnv *env, jobject thiz) {
//
//}
//
//extern "C"
//JNIEXPORT void JNICALL
//Java_com_xc_ffplayer_ffplayer_FFPlayer_nativeResume(JNIEnv *env, jobject thiz) {
//
//}
//
//extern "C"
//JNIEXPORT void JNICALL
//Java_com_xc_ffplayer_ffplayer_FFPlayer_nativeStop(JNIEnv *env, jobject thiz) {
//    if (player) {
//        player->status = STOP;
//    }
//}
//
//extern "C"
//JNIEXPORT void JNICALL
//Java_com_xc_ffplayer_ffplayer_FFPlayer_nativeRelease(JNIEnv *env, jobject thiz) {
//    if (player) {
//        delete player;
//        player = NULL;
//    }
//}