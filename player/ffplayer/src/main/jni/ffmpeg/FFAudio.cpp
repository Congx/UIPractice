//
// Created by Luffy on 28/5/2021.
//

#include "FFAudio.h"
#include "log.h"
#include "pthread.h"

extern "C" {
#include "libswresample/swresample.h"
}

FFAudio::FFAudio(FFPlayerJavaCallback *callback, Playerstatus *status) : callback(callback),
                                                                         status(status) {

}

FFAudio::~FFAudio() {
    (*engineObject)->Destroy(engineObject);
}

void *decodeThreadCall(void *context) {
    LOGD("audio decodeThreadCall");
    FFAudio *audio = static_cast<FFAudio *>(context);
    audio->initOpenSLES();
//    audio->decodeFFmpegThread();
    pthread_exit(&audio->decode_thread);
}

void FFAudio::start() {
    LOGD("audio start");
    queue.setWork(1);
    pthread_create(&decode_thread, NULL, decodeThreadCall, this);
}


void pcmBufferCallBack(SLAndroidSimpleBufferQueueItf bf, void *context) {
    FFAudio *audio = (FFAudio *) context;
    if (audio != NULL) {
        int buffersize = audio->resampleAudio();
        if (buffersize > 0) {
            audio->clock += buffersize/(audio->out_channel_nb * audio->out_sample_rate * audio->out_sample_byte_count);
            // 计算播放时长
            if(audio->clock - audio->last_time >= 0.5) {
                audio->last_time = audio->clock;
                audio->callback->onCurrentTime(audio->clock,audio->duration,CHILD_THREAD);
            }
            (*audio->pcmBufferQueue)->Enqueue(audio->pcmBufferQueue, (char *) audio->buffer,buffersize);
        }
    }
}

void FFAudio::initOpenSLES() {
    SLresult result;
    result = slCreateEngine(&engineObject, 0, 0, 0, 0, 0);
    (void)result;
//    if(result == SL_RESULT_MEMORY_FAILURE) {
//
//    }
    result = (*engineObject)->Realize(engineObject, SL_BOOLEAN_FALSE);
    (void)result;
    result = (*engineObject)->GetInterface(engineObject, SL_IID_ENGINE, &engineEngine);
    (void)result;

    //第二步，创建混音器
    const SLInterfaceID mids[1] = {SL_IID_ENVIRONMENTALREVERB};
    const SLboolean mreq[1] = {SL_BOOLEAN_FALSE};
    result = (*engineEngine)->CreateOutputMix(engineEngine, &outputMixObject, 1, mids, mreq);
    (void) result;
    result = (*outputMixObject)->Realize(outputMixObject, SL_BOOLEAN_FALSE);
    (void) result;
    result = (*outputMixObject)->GetInterface(outputMixObject, SL_IID_ENVIRONMENTALREVERB,
                                              &outputMixEnvironmentalReverb);
    if (SL_RESULT_SUCCESS == result) {
        result = (*outputMixEnvironmentalReverb)->SetEnvironmentalReverbProperties(
                outputMixEnvironmentalReverb, &reverbSettings);
        (void) result;
    }
    SLDataLocator_OutputMix outputMix = {SL_DATALOCATOR_OUTPUTMIX, outputMixObject};
    SLDataSink audioSnk = {&outputMix, 0};


    // 第三步，配置PCM格式信息
    SLDataLocator_AndroidSimpleBufferQueue android_queue = {SL_DATALOCATOR_ANDROIDSIMPLEBUFFERQUEUE,
                                                            2};

    SLDataFormat_PCM pcm = {
            SL_DATAFORMAT_PCM,//播放pcm格式的数据
            2,//2个声道（立体声）
            static_cast<SLuint32>(getCurrentSampleRateForOpensles(sample_rate)),//44100hz的频率
            SL_PCMSAMPLEFORMAT_FIXED_16,//位数 16位
            SL_PCMSAMPLEFORMAT_FIXED_16,//和位数一致就行
            SL_SPEAKER_FRONT_LEFT | SL_SPEAKER_FRONT_RIGHT,//立体声（前左前右）
            SL_BYTEORDER_LITTLEENDIAN//结束标志
    };
    SLDataSource slDataSource = {&android_queue, &pcm};


    const SLInterfaceID ids[1] = {SL_IID_BUFFERQUEUE};
    const SLboolean req[1] = {SL_BOOLEAN_TRUE};

    (*engineEngine)->CreateAudioPlayer(engineEngine, &pcmPlayerObject, &slDataSource, &audioSnk, 1,
                                       ids, req);
    //初始化播放器
    (*pcmPlayerObject)->Realize(pcmPlayerObject, SL_BOOLEAN_FALSE);

//    得到接口后调用  获取Player接口
    (*pcmPlayerObject)->GetInterface(pcmPlayerObject, SL_IID_PLAY, &pcmPlayerPlay);

//    注册回调缓冲区 获取缓冲队列接口
    (*pcmPlayerObject)->GetInterface(pcmPlayerObject, SL_IID_BUFFERQUEUE, &pcmBufferQueue);
    //缓冲接口回调
    (*pcmBufferQueue)->RegisterCallback(pcmBufferQueue, pcmBufferCallBack, this);
//    获取播放状态接口
    (*pcmPlayerPlay)->SetPlayState(pcmPlayerPlay, SL_PLAYSTATE_PLAYING);
    pcmBufferCallBack(pcmBufferQueue, this);
}

int FFAudio::getCurrentSampleRateForOpensles(int sample_rate) {
    int rate = 0;
    switch (sample_rate) {
        case 8000:
            rate = SL_SAMPLINGRATE_8;
            break;
        case 11025:
            rate = SL_SAMPLINGRATE_11_025;
            break;
        case 12000:
            rate = SL_SAMPLINGRATE_12;
            break;
        case 16000:
            rate = SL_SAMPLINGRATE_16;
            break;
        case 22050:
            rate = SL_SAMPLINGRATE_22_05;
            break;
        case 24000:
            rate = SL_SAMPLINGRATE_24;
            break;
        case 32000:
            rate = SL_SAMPLINGRATE_32;
            break;
        case 44100:
            rate = SL_SAMPLINGRATE_44_1;
            break;
        case 48000:
            rate = SL_SAMPLINGRATE_48;
            break;
        case 64000:
            rate = SL_SAMPLINGRATE_64;
            break;
        case 88200:
            rate = SL_SAMPLINGRATE_88_2;
            break;
        case 96000:
            rate = SL_SAMPLINGRATE_96;
            break;
        case 192000:
            rate = SL_SAMPLINGRATE_192;
            break;
        default:
            rate = SL_SAMPLINGRATE_44_1;
    }
    return rate;
}

int FFAudio::resampleAudio() {
    // audio 相关 ----
    if(avFrame == NULL) {
        avFrame = av_frame_alloc();
    }
    // 初始化转换器
    if(swr_ctx == NULL) {
        initSwrCtx();
    }

    if (!status->isExit()) {
        queue.pop(avPacket);
        if (!avPacket || avPacket != NULL) {
            int ret = avcodec_send_packet(codecContext, avPacket);
            if (ret < 0 && AVERROR(EAGAIN) && ret != AVERROR_EOF) {
                LOGE("音频解码出错");
                return 0;
            }
            ret = avcodec_receive_frame(codecContext, avFrame);
            if (ret == AVERROR(EAGAIN)) {
                return 0;
            } else if (ret != 0) {
                LOGE("音频解码receive_frame出错");
                return 0;
            }
//            LOGE("nb_samples %d",avFrame->nb_samples);
            // 重采样，转换 采样的通道数、频率等数据，按照swrContext的参数来转换
            swr_convert(swr_ctx, &buffer, avFrame->nb_samples,(const uint8_t **) avFrame->data, avFrame->nb_samples);
            out_buffer_size = av_samples_get_buffer_size(NULL, out_channel_nb, avFrame->nb_samples,out_sample_fmt, 1);
            // 计算时间
            frame_time = avFrame->pts * av_q2d(time_base);
            if(frame_time < clock) {
                frame_time = clock;
            }
            clock = frame_time;
            av_packet_free(&avPacket);
            av_free(avPacket);
            avPacket = NULL;
//            av_frame_free(&avFrame);
//            av_free(avFrame);
//            avFrame = NULL;
//            swr_free(&swr_ctx);
            return out_buffer_size;
        } else {
            swr_free(&swr_ctx);
            av_packet_free(&avPacket);
            av_free(avPacket);
            avPacket = NULL;
            av_frame_free(&avFrame);
            av_free(avFrame);
            avFrame = NULL;
            return 0;
        }
    }

    return 0;
}

void FFAudio::initSwrCtx() {
    swr_ctx = swr_alloc();
    uint64_t out_ch_layout = AV_CH_LAYOUT_STEREO; // 声道
    out_sample_rate = sample_rate; // 采样频率
    out_sample_fmt = AV_SAMPLE_FMT_S16; // 采样位数
    // 一个采样几个字节
    out_sample_byte_count = av_get_bytes_per_sample(out_sample_fmt);
    // 采样通道数
    out_channel_nb = av_get_channel_layout_nb_channels(out_ch_layout);
    // 音频的转换器
    swr_alloc_set_opts(swr_ctx,
                    // 输出到喇叭的参数，抓换后的参数
                       out_ch_layout,
                       out_sample_fmt,
                       out_sample_rate,
                    //  视频文件的参数
                       channel_layout,
                       *sample_fmts,
                       sample_rate,
                       0, NULL);
    if (!swr_ctx || swr_init(swr_ctx) < 0) {
        av_packet_free(&avPacket);
        av_free(avPacket);
        avPacket = NULL;
        av_frame_free(&avFrame);
        av_free(avFrame);
        avFrame = NULL;
        swr_free(&swr_ctx);
    }

    if (buffer == NULL) {
        int outBufferSize = out_sample_rate * out_sample_byte_count * out_channel_nb;
        LOGD("outBufferSize：%d", outBufferSize);
        buffer = static_cast<uint8_t *>(av_malloc(outBufferSize));
    }
    LOGD("out_sample_rate：%d", out_sample_rate);
    LOGD("out_sample_byte_count：%d", out_sample_byte_count);
    LOGD("out_channel_nb：%d", out_channel_nb);

    LOGD("初始化swr_ctx");
}

//void FFAudio::decodeFFmpegThread() {
//    queue.setWork(1);
//    LOGD("audio decodeFFmpegThread");
//    // audio 相关 ----
//    AVFrame *avFrame = av_frame_alloc();
//    SwrContext *swrContext = swr_alloc();
//    uint64_t out_ch_layout = AV_CH_LAYOUT_STEREO; // 声道
//    int out_sample_rate = sample_rate; // 采样频率
//    AVSampleFormat out_sample_fmt = AV_SAMPLE_FMT_S16; // 采样位数
//    // 一个采样几个字节
//    int sampleByteCount = av_get_bytes_per_sample(out_sample_fmt);
//    // 采样通道数
//    int out_channel_nb = av_get_channel_layout_nb_channels(out_ch_layout);
//    // 音频的转换器
//    swr_alloc_set_opts(swrContext,
//            // 输出到喇叭的参数，抓换后的参数
//                       out_ch_layout,
//                       out_sample_fmt,
//                       out_sample_rate,
//            //  视频文件的参数
//                       channel_layout,
//                       *sample_fmts,
//                       sample_rate,
//                       0, NULL);
//    swr_init(swrContext);
//    int outBufferSize = out_sample_rate * sampleByteCount * out_channel_nb;
//    uint8_t *outAudioBuffer = static_cast<uint8_t *>(av_malloc(outBufferSize));
//    LOGD("outBufferSize：%d", outBufferSize);
//    LOGD("out_sample_rate：%d", out_sample_rate);
//    LOGD("sampleByteCount：%d", sampleByteCount);
//    LOGD("out_channel_nb：%d", out_channel_nb);
//    callback->createAudioTrack(out_channel_nb, out_sample_rate, sampleByteCount, CHILD_THREAD,
//                               true);
//    while (!status->exit) {
//        AVPacket *avPacket = NULL;
//        queue.pop(avPacket);
//        if (avPacket) {
//            int ret = avcodec_send_packet(codecContext, avPacket);
//            if (ret < 0 && AVERROR(EAGAIN) && ret != AVERROR_EOF) {
//                LOGE("音频解码出错");
//                break;
//            }
//
//            ret = avcodec_receive_frame(codecContext, avFrame);
//            if (ret == AVERROR(EAGAIN)) {
//                continue;
//            } else if (ret != 0) {
//                LOGE("音频解码receive_frame出错");
//                break;
//            }
////            LOGE("nb_samples %d",avFrame->nb_samples);
//            // 重采样，转换 采样的通道数、频率等数据，按照swrContext的参数来转换
//            swr_convert(swrContext, &outAudioBuffer, avFrame->nb_samples,
//                        (const uint8_t **) avFrame->data, avFrame->nb_samples);
//
//            int size = av_samples_get_buffer_size(NULL, out_channel_nb, avFrame->nb_samples,
//                                                  out_sample_fmt, 1);
//            callback->playAudio(outAudioBuffer, size, CHILD_THREAD, true);
//        }
//    }
//}

void FFAudio::pause() {
    if (pcmPlayerPlay != NULL) {
        SLresult result;
        result = (*pcmPlayerPlay)->SetPlayState(pcmPlayerPlay, SL_PLAYSTATE_STOPPED);
        assert(SL_RESULT_SUCCESS == result);
    }
}

void FFAudio::resume() {
    if (pcmPlayerPlay != NULL) {
        SLresult result;
        result = (*pcmPlayerPlay)->SetPlayState(pcmPlayerPlay, SL_PLAYSTATE_PLAYING);
        assert(SL_RESULT_SUCCESS == result);
    }
}
