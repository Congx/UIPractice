#include "Playerstatus.h"

Playerstatus::Playerstatus() {
}

bool Playerstatus::isExit() {
    return status >= EXIT;
}

bool Playerstatus::setStatus(STATUS status) {
    return this->status = status;
}

bool Playerstatus::isSeeking() {
    return status == SEEKING;
}

bool Playerstatus::isPause() {
    return status == PAUSE;
}

bool Playerstatus::isLoading() {
    return status == LOADING;
}
