package com.base.framwork.ui.recyclerview.utils

import android.util.Log
import android.view.View
import androidx.viewpager2.widget.ViewPager2

/**
 * @date 27/4/2020
 * @Author luffy
 * @description
 */
class ScaleInTransformer : ViewPager2.PageTransformer {

    private val mMinScale = DEFAULT_MIN_SCALE

    override fun transformPage(view: View, position: Float) {
        Log.e("ScaleInTransformer",position.toString())
        view.elevation = -kotlin.math.abs(position)
        val pageWidth = view.width
        val pageHeight = view.height
        Log.e("pageWidth",pageWidth.toString())

        view.pivotY = (pageHeight / 2).toFloat()
        view.pivotX = (pageWidth / 2).toFloat()
        if (position < -1) {
            view.scaleX = mMinScale
            view.scaleY = mMinScale
            view.pivotX = pageWidth.toFloat()
        } else if (position <= 1) {
            if (position < 0) {
                val scaleFactor = (1 + position) * (1 - mMinScale) + mMinScale
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
                view.pivotX = pageWidth * (DEFAULT_CENTER + DEFAULT_CENTER * -position)
            } else {
                val scaleFactor = (1 - position) * (1 - mMinScale) + mMinScale
                view.scaleX = scaleFactor
                view.scaleY = scaleFactor
                view.pivotX = pageWidth * ((1 - position) * DEFAULT_CENTER)
            }
        } else {
            view.pivotX = 0f
            view.scaleX = mMinScale
            view.scaleY = mMinScale
        }
    }

    companion object {

        const val DEFAULT_MIN_SCALE = 0.85f
        const val DEFAULT_CENTER = 0.5f
    }
}
