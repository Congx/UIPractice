package com.example.uipractice.window

import android.content.Context
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.uipractice.R
import kotlinx.android.synthetic.main.activity_window_test.*

class WindowTestActivity : AppCompatActivity() {

    var wm:WindowManager? = null
    lateinit var rootView:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_window_test)
        btnActivity.setOnClickListener {
            wm = windowManager
            showWindow(wm!!)
        }

        btnApplication.setOnClickListener {
            wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
            showWindow(wm!!)
        }

        btnDelete.setOnClickListener {
            wm?.removeView(rootView)
        }
    }

    fun showWindow(wm: WindowManager) {
        val layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE
        layoutParams.format = PixelFormat.TRANSPARENT;
        layoutParams.gravity = Gravity.CENTER
        layoutParams.height = 200
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        wm.addView(getView(),layoutParams)
    }

    fun getView():View {
        rootView = Button(this)
        rootView.text = "botton"
        return rootView
    }
}