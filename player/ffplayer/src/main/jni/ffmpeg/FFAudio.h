//
// Created by Luffy on 28/5/2021.
//

#ifndef UIPRACTICE_FFAUDIO_H
#define UIPRACTICE_FFAUDIO_H
#include "safe_queue.h"
#include "FFPlayerJavaCallback.h"
#include "Playerstatus.h"
#include "soundtouch/include/SoundTouch.h"
#include "soundtouch/include/STTypes.h"

extern "C" {
#include <libavcodec/avcodec.h>
#include <SLES/OpenSLES.h>
#include "SLES/OpenSLES_Android.h"
#include "libswresample/swresample.h"
}

using namespace soundtouch;

class FFAudio {
public:
    FFAudio(FFPlayerJavaCallback *callback,Playerstatus *status);
    ~FFAudio();

    void start();
    void initOpenSLES();
    int resampleAudio(uint8_t **buffer);
    int getCurrentSampleRateForOpensles(int sample_rate);
    void initSwrCtx();
    void decodeFFmpegThread();
    int getSoundTouchData();
    void initSoundTouch();

public:
    AVCodecContext *codecContext = NULL;
//    AVCodec *audioCodec = NULL;
    const AVSampleFormat *sample_fmts = NULL;
//    AVCodecParameters *codecpar = NULL;
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

    SwrContext *swr_ctx = NULL;
    int out_channel_nb; // 输出采样通道数量
    int out_sample_byte_count; // 输出采样字节数
    int out_sample_rate; // 输出采样频率
    AVSampleFormat out_sample_fmt;
    int out_buffer_size;
    // swr 转换后的实际采样个数
    int nb_samples;
    // 解码后的pcm数据
    uint8_t *buffer = NULL;

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
    // 音量接口
    SLVolumeItf pcmVolumePlay = NULL;
    //立体声
    int mute = 2;
    // 声道
    SLMuteSoloItf  pcmMutePlay = NULL;
    //缓冲器队列接口
    SLAndroidSimpleBufferQueueItf pcmBufferQueue = NULL;

    // soundtouch 相关
    //变调
    float pitch= 1.0f;
    //变速度
    float speed= 1.0f;
    //采样位数 SoudTouch最低支持16bit，所以使用16bit的来播放
//    int bits= 16;
    //每秒理论PCM大小
//    int BUFF_SIZE =out_sample_rate * out_channel_nb * out_sample_byte_count;
    // soundtouch 输出数据
    SAMPLETYPE * sampleBuffer = NULL;
    SoundTouch *soundTouch = NULL;
    int s_len = 0;
    bool finished = true;

    void pause();

    void resume();

    void stop();

    void setVolume(int i);

    void setMute(int mute);

    void setPitch(float pitch);

    void setSpeed(float speed);
};


#endif //UIPRACTICE_FFAUDIO_H
