package com.xc.ffplayer.activitys

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xc.ffplayer.R
import com.xc.ffplayer.live.Live
import kotlinx.android.synthetic.main.activity_live_push.*

class LivePushActivity : AppCompatActivity() {

    var live = Live(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_push)
        live.initPreview(textureView)
        live.startPush("")
    }

    override fun onDestroy() {
        super.onDestroy()
        live.release()
    }
}