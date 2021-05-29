package com.xc.ffplayer.utils

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor

object MainExecuter :Executor{

    private var handler = Handler(Looper.getMainLooper())

    override fun execute(task:Runnable) {
        handler.post(task)
    }
}