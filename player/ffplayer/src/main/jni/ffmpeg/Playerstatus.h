
#ifndef MYMUSIC_WLPLAYSTATUS_H
#define MYMUSIC_WLPLAYSTATUS_H


class Playerstatus {

public:
    enum STATUS {
        UNINIT,PREPARE,PLAYING,PAUSE,SEEKING,LOADING,EXIT
    };

private:
    STATUS status = UNINIT;

public:

    Playerstatus();

    bool isExit();

    bool isSeeking();

    bool isPause();

    bool setStatus(STATUS status);

    bool isLoading();
};


#endif //MYMUSIC_WLPLAYSTATUS_H
