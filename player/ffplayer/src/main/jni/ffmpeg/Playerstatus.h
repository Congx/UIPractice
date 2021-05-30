
#ifndef MYMUSIC_WLPLAYSTATUS_H
#define MYMUSIC_WLPLAYSTATUS_H


class Playerstatus {

public:
    enum STATUS {
        UNINIT,PREPARE,PLAYING,PAUSEE,EXIT
    };

private:
    STATUS status = UNINIT;

public:

    Playerstatus();

    bool isExit();

    bool setStatus(STATUS status);

};


#endif //MYMUSIC_WLPLAYSTATUS_H
