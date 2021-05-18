//
// Created by Luffy on 18/5/2021.
//

#ifndef UIPRACTICE_AUDIOCHANNEL_H
#define UIPRACTICE_AUDIOCHANNEL_H


class AudioChannel {
public:
    AudioChannel();
    ~AudioChannel();

    int setAudioInfo(int sampleRate,int channels);

    void encodeData(int32_t *data,int len);
};


#endif //UIPRACTICE_AUDIOCHANNEL_H
