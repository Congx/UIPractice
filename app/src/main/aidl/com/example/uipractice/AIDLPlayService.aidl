// AIDLPlayService.aidl
package com.example.uipractice;
import com.example.uipractice.PlayCallback;

// Declare any non-default types here with import statements

interface AIDLPlayService {
        void start();
        void seekTo(String id, int position);
        void pause(String id);

        void addPlayCallback(PlayCallback playCallback);
        void removePlayCallback(PlayCallback playCallback);
}
