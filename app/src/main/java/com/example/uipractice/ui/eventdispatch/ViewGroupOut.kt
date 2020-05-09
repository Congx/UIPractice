package com.example.uipractice.ui.eventdispatch

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import com.example.uipractice.ui.eventdispatch.ViewGroupIn.Companion.logEvent

class ViewGroupOut @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    init {

    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        logEvent(ev,"ViewGroupOut","dispatchTouchEvent")
        val dispatchTouchEvent = super.dispatchTouchEvent(ev)
//        Log.e("ViewGroupOut","super->dispatchTouchEvent = $dispatchTouchEvent")
        return dispatchTouchEvent
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
//        logEvent(ev,"ViewGroupOut","onInterceptTouchEvent")
        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        logEvent(event,"ViewGroupOut","onTouchEvent")
        return super.onTouchEvent(event)
    }

}