//
// Created by Luffy on 31/5/2021.
//

#ifndef UIPRACTICE_PLAYERLOCK_H
#define UIPRACTICE_PLAYERLOCK_H

#include <pthread.h>

class PlayerLock {
private:
    pthread_cond_t cond;
    pthread_mutex_t mutex;
public:
    PlayerLock();
//    PlayerLock(int a);
    ~PlayerLock();

    void lock();

    void unlock();

    void await();

    void notify();
};


#endif //UIPRACTICE_PLAYERLOCK_H
