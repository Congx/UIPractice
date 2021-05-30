#include "Playerstatus.h"

Playerstatus::Playerstatus() {
}

bool Playerstatus::isExit() {
    return status == EXIT;
}

bool Playerstatus::setStatus(STATUS status) {
    return this->status = status;
}
