package com.example.uipractice.plugin

import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.Log

class HCallback(var handler: Handler):Handler.Callback {

    companion object {
        @JvmStatic
        var LAUNCH_ACTIVITY = 100
    }

    override fun handleMessage(msg: Message): Boolean {

        if (Build.VERSION.SDK_INT <= 26) {
            if (msg.what == LAUNCH_ACTIVITY) {
                var o = msg.obj
                var intent = FieldUtils.getField(o::class.java, o, "intent") as Intent
                val target = intent.getParcelableExtra<Intent>(HookHelper.TARGET_INTENT)
                if (target != null) {
                    intent.component = target?.component
                    Log.d("HCallback",target!!.component.toString())
                }
            }
        }
        handler.handleMessage(msg)
        return true
    }
}