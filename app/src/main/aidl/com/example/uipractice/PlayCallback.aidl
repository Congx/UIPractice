// PlayCallback.aidl
package com.example.uipractice;

// Declare any non-default types here with import statements

interface PlayCallback {
    void stateNow(int state, String songId, int position);
}
