//
// Created by Luffy on 31/5/2021.
//

#include "PlayerLock.h"

PlayerLock::PlayerLock() {
    pthread_mutex_init(&mutex,NULL);
    pthread_cond_init(&cond,NULL);
}

PlayerLock::~PlayerLock() {
    pthread_cond_destroy(&cond);
    pthread_mutex_destroy(&mutex);
}

void PlayerLock::lock() {
    pthread_mutex_lock(&mutex);
}

void PlayerLock::unlock() {
    pthread_mutex_unlock(&mutex);
}

void PlayerLock::await() {
    pthread_cond_wait(&cond, &mutex);
}

void PlayerLock::notify() {
    pthread_cond_signal(&cond);
}

