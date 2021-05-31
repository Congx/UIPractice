//
// Created by Luffy on 27/5/2021.
//

#ifndef UIPRACTICE_FFPLAYER_H
#define UIPRACTICE_FFPLAYER_H

#include "FFPlayerJavaCallback.h"
#include "FFAudio.h"
#include "Playerstatus.h"
#include "PlayerLock.h"

extern "C"{
#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libavutil/imgutils.h"
#include "libswscale/swscale.h"
}

class FFPlayer {

private:
    PlayerLock playerLock;

public:
    AVFormatContext * avFormatContext = NULL;
    Playerstatus *status;

    FFPlayerJavaCallback *callback = NULL;
    pthread_t prepare_thread = NULL;
    char *url = NULL;
    FFAudio *audio = NULL;
    int64_t duration; // 秒

public:

    FFPlayer(FFPlayerJavaCallback *callback,char* url,Playerstatus *status);
    ~FFPlayer();
    void prepare();
    void start();
    int decodeFFmpegThread();

    void pause();

    void resume();

    void stop();

    void seek(jint i);
};


#endif //UIPRACTICE_FFPLAYER_H
