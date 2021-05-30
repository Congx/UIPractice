//
// Created by Luffy on 28/5/2021.
//

#ifndef UIPRACTICE_FFAUDIO_H
#define UIPRACTICE_FFAUDIO_H
#include "safe_queue.h"
#include "FFPlayerJavaCallback.h"
#include "Playerstatus.h"

extern "C" {
#include <libavcodec/avcodec.h>
#include <SLES/OpenSLES.h>
#include "SLES/OpenSLES_Android.h"
#include "libswresample/swresample.h"
}

class FFAudio {
public:
    FFAudio(FFPlayerJavaCallback *callback,Playerstatus *status);
    ~FFAudio();

    void start();
    void initOpenSLES();
    int resampleAudio();
    int getCurrentSampleRateForOpensles(int sample_rate);
    void initSwrCtx();
    void decodeFFmpegThread();

public:
    AVCodecContext *codecContext = NULL;
    AVCodec *audioCodec = NULL;
    const AVSampleFormat *sample_fmts = NULL;
    AVCodecParameters *codecpar = NULL;
    int sample_rate = NULL;
    int channels = NULL;
    uint64_t channel_layout = NULL;
    int streamIndex = NULL;

    int64_t duration; // 秒
    AVRational time_base;
    double frame_time;
    double last_time;
    double clock;

    SafeQueue<AVPacket *> queue;
    FFPlayerJavaCallback *callback = NULL;
    Playerstatus *status = NULL;
    pthread_t decode_thread = NULL;

    AVPacket *avPacket = NULL;
    AVFrame *avFrame = NULL;
    uint8_t *buffer = NULL;

    SwrContext *swr_ctx = NULL;
    int out_channel_nb; // 输出采样通道数量
    int out_sample_byte_count; // 输出采样字节数
    int out_sample_rate; // 输出采样频率
    AVSampleFormat out_sample_fmt;
    int out_buffer_size;

    // opensl
    // 引擎接口
    SLObjectItf engineObject = NULL;
    SLEngineItf engineEngine = NULL;

    //混音器
    SLObjectItf outputMixObject = NULL;
    SLEnvironmentalReverbItf outputMixEnvironmentalReverb = NULL;
    SLEnvironmentalReverbSettings reverbSettings = SL_I3DL2_ENVIRONMENT_PRESET_STONECORRIDOR;

    //pcm
    SLObjectItf pcmPlayerObject = NULL;
    SLPlayItf pcmPlayerPlay = NULL;

    //缓冲器队列接口
    SLAndroidSimpleBufferQueueItf pcmBufferQueue = NULL;

    void pause();

    void resume();
};


#endif //UIPRACTICE_FFAUDIO_H
