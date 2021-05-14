package com.xc.ffplayer.activitys

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.xc.ffplayer.R
import com.xc.ffplayer.live.Live
import kotlinx.android.synthetic.main.activity_live_push.*

class LivePushActivity : AppCompatActivity() {

    var live = Live(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_push)

        window.decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)

//        window.decorView.rootWindowInsets

        if (ActivityCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO,Manifest.permission.CAMERA),100)
        }

        live.initPreview(textureView)

        btnStart.setOnClickListener {
            live.startPush("rtmp://192.168.1.2:1935/xucong/room")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        live.stop()
        live.release()
    }
}