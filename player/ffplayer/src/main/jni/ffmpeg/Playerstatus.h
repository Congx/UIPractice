
#ifndef MYMUSIC_WLPLAYSTATUS_H
#define MYMUSIC_WLPLAYSTATUS_H


class Playerstatus {

public:
    enum STATUS {
        UNINIT,PREPARE,PLAYING,PAUSEE,SEEKING,EXIT
    };

private:
    STATUS status = UNINIT;

public:

    Playerstatus();

    bool isExit();

    bool isSeeking();

    bool setStatus(STATUS status);

};


#endif //MYMUSIC_WLPLAYSTATUS_H
