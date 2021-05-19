//
// Created by Luffy on 18/5/2021.
//

#ifndef UIPRACTICE_AUDIOCHANNEL_H
#define UIPRACTICE_AUDIOCHANNEL_H

#include "faac.h"
#include "rtmp.h"
#include "log.h"

typedef void (*VideoCallback)(RTMPPacket *packet);
class AudioChannel {
public:
    AudioChannel();
    ~AudioChannel();

    int setAudioInfo(int sampleRate,int channels);

    void encode(int32_t *data,int len);

    RTMPPacket *getAudioConfig();

    void setCallback(VideoCallback callback);
    int getMinBuferSize();

    unsigned long maxOutputBytes;
    unsigned long inputByteNum;
    unsigned char* outputBuffer;
    VideoCallback callback;
    bool isSendHead = false;

private:
    faacEncHandle codec = 0;
};


#endif //UIPRACTICE_AUDIOCHANNEL_H
