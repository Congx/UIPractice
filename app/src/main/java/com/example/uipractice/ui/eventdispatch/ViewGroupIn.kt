package com.example.uipractice.ui.eventdispatch

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout

class ViewGroupIn @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        logEvent(ev, "ViewGroupIn", "dispatchTouchEvent")
        val dispatchTouchEvent = super.dispatchTouchEvent(ev)
//        Log.e("ViewGroupIn","super->dispatchTouchEvent = $dispatchTouchEvent")
//        Log.e("uip-ViewGroupIn","super->dispatchTouchEvent = $dispatchTouchEvent")
//        requestDisallowInterceptTouchEvent(false)
        return true
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        logEvent(ev, "ViewGroupIn", "onInterceptTouchEvent")
//        requestDisallowInterceptTouchEvent(false)
//        return super.onInterceptTouchEvent(ev)
        if (MotionEvent.ACTION_DOWN == ev?.action) {
            return super.onInterceptTouchEvent(ev)
        }
        return true
//        return super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
//        requestDisallowInterceptTouchEvent(false)
//        logEvent(event,"ViewGroupIn","onTouchEvent")
//        if (event?.action == MotionEvent.ACTION_DOWN) {
//            return true
//        }
//        return true
        return super.onTouchEvent(event)
    }

    companion object {
        fun logEvent(ev: MotionEvent?, s: String, s1: String) {
            when(ev?.action) {
                MotionEvent.ACTION_DOWN -> Log.e("uip-$s","$s1 -> MotionEvent = ACTION_DOWN")
                MotionEvent.ACTION_MOVE -> Log.e("uip-$s","$s1 -> MotionEvent = ACTION_MOVE")
                MotionEvent.ACTION_UP -> Log.e("uip-$s","$s1 -> MotionEvent = ACTION_UP")
                MotionEvent.ACTION_CANCEL -> Log.e("uip-$s","$s1 -> MotionEvent = ACTION_CANCEL")
            }
        }
    }


}