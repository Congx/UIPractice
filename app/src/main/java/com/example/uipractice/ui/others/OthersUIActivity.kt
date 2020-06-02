package com.example.uipractice.ui.others

import android.content.Context
import android.graphics.Outline
import android.graphics.Rect
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.app.AppCompatActivity
import com.example.uipractice.R
import kotlinx.android.synthetic.main.activity_others_u_i.*
import java.lang.Exception

class OthersUIActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("onCreate","1")

        setContentView(R.layout.activity_others_u_i)
//        view.outlineProvider = object : ViewOutlineProvider() {
//            override fun getOutline(view: View?, outline: Outline?) {
////                outline?.setRoundRect(0, 0, view!!.measuredWidth, view.measuredHeight, 30f)
//                val background = view!!.background
//                if (background != null) {
//                    background.getOutline(outline)
//                } else {
//                    outline?.setRoundRect(0, 0, view!!.measuredWidth, view.measuredHeight, 30f)
//                }
//
//            }
//
//        }
////        view.clipBounds = Rect(0, 0, view!!.measuredWidth, view.measuredHeight)
//        view.clipToOutline = true
        Log.e("onCreate","2")
    }

    /**
     * 为了统一设置clipToOutline 自定义属性，切割水波纹圆角为背景的圆角大小
     * 只需要在xml中 app:clipToOutline = true view将会以background的
     * 的外边框切割，如果背景设置了 圆角shape，那么设置了此属性的View将outline
     * 切割为背景的outline
     */
    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        val onCreateView = super.onCreateView(name, context, attrs)
        try {
            var count = attrs.attributeCount
            for (i in count-1 downTo  0) {
                var attributeName = attrs.getAttributeName(i)
                var attributeValue = attrs.getAttributeValue(i)
                if ("clipToOutline" == attributeName) {
//                    onCreateView?.clipToOutline = attributeValue.toBoolean()
                    Log.e("onCreateView",attributeName)
                    Log.e("onCreateView",attributeValue)

                    onCreateView?.outlineProvider = object : ViewOutlineProvider() {
                        override fun getOutline(view: View?, outline: Outline?) {
//                          outline?.setRoundRect(0, 0, view!!.measuredWidth, view.measuredHeight, 30f)
                            val background = view!!.background
                            if (background != null) {
                                background.getOutline(outline)
                            } else {
                                outline?.setRoundRect(0, 0, view!!.measuredWidth, view.measuredHeight, 30f)
                            }

                        }

                    }
                }
            }
        }catch (e: Exception) {
            e.printStackTrace()
        }
        return onCreateView
    }
}
