package com.xc.ffplayer.camera2

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.xc.ffplayer.R

open class PlayContainerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    var surfaceView: MySurfaceView?  = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        surfaceView = findViewById(R.id.video_surfaceView)
    }
}