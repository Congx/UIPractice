package com.example.uipractice.ui.eventdispatch

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class ViewIn @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var count = 0

    init {
        setOnClickListener {
            Log.e("uip-setOnClickListener","xxxxxxxxx")
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        parent.requestDisallowInterceptTouchEvent(true)
        ViewGroupIn.logEvent(ev, "ViewIn", "dispatchTouchEvent")
        val dispatchTouchEvent = super.dispatchTouchEvent(ev)
//        Log.e("ViewGroupOut","super->dispatchTouchEvent = $dispatchTouchEvent")
//        count++
//        if (count < 50) {
//            return true
//        }
//        Log.e("dispatchTouchEvent","count$count")
        return false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        ViewGroupIn.logEvent(event, "ViewIn", "onTouchEvent")
//        return true
        return super.onTouchEvent(event)
    }
}