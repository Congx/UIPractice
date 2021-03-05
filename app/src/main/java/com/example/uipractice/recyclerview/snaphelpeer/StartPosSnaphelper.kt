package com.example.uipractice.recyclerview.snaphelpeer

import android.util.Log
import android.view.View
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * 停留在第一个item的snaphelper
 */
class StartPosSnaphelper : LinearSnapHelper() {

    @Nullable
    private lateinit var mVerticalHelper: OrientationHelper

    @Nullable
    private lateinit var mHorizontalHelper: OrientationHelper


    override fun calculateDistanceToFinalSnap(
        layoutManager: RecyclerView.LayoutManager,
        targetView: View
    ): IntArray? {
        val out = IntArray(2)
        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToTop(
                layoutManager, targetView!!,
                getHorizontalHelper(layoutManager)
            )
        } else {
            out[0] = 0
        }
        if (layoutManager.canScrollVertically()) {
            out[1] = distanceToTop(
                layoutManager, targetView!!,
                getVerticalHelper(layoutManager)
            )
        } else {
            out[1] = 0
        }
        return out
    }

//    override fun findTargetSnapPosition(
//        layoutManager: RecyclerView.LayoutManager?,
//        velocityX: Int,
//        velocityY: Int
//    ): Int {
//        return RecyclerView.NO_POSITION
//    }

    override fun findSnapView(layoutManager: RecyclerView.LayoutManager): View? {
        if (layoutManager.canScrollVertically()) {
            return findCenterView(layoutManager, getVerticalHelper(layoutManager))
        } else if (layoutManager.canScrollHorizontally()) {
            return findCenterView(layoutManager, getHorizontalHelper(layoutManager))
        }
        return null
    }

    private fun distanceToTop(
        @NonNull layoutManager: RecyclerView.LayoutManager,
        @NonNull targetView: View, helper: OrientationHelper
    ): Int {
        var dex = 0
        val height = helper.getDecoratedMeasurement(targetView)
        val heafHeight = height / 2
        val decoratedStart = helper.getDecoratedStart(targetView)
        Log.e("decoratedStart = ", decoratedStart.toString() )
        if (Math.abs(decoratedStart) > heafHeight) {
            dex = decoratedStart
            Log.e("distanceToTop1 = ", dex.toString() )
        }else {
            dex = decoratedStart
            Log.e("distanceToTop2 = ", dex.toString() )
        }
        return dex
    }

    private fun findCenterView(
        layoutManager: RecyclerView.LayoutManager,
        helper: OrientationHelper
    ): View? {
        val childCount = layoutManager.childCount
        if (childCount == 0) {
            return null
        }
        var closestChild: View? = null
        for (i in 0 until childCount) {
            val child = layoutManager.getChildAt(i)
            val decoratedStart = helper.getDecoratedStart(child)
            if (decoratedStart <= 0) {
                closestChild = child
                break
            }
        }
        return closestChild
    }

    @NonNull
    private fun getVerticalHelper(@NonNull layoutManager: RecyclerView.LayoutManager): OrientationHelper {
        if (!this::mVerticalHelper.isInitialized) {
            mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
        }
        return mVerticalHelper
    }

    @NonNull
    private fun getHorizontalHelper(
        @NonNull layoutManager: RecyclerView.LayoutManager
    ): OrientationHelper {
        if (!this::mVerticalHelper.isInitialized) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
        }
        return mHorizontalHelper
    }
}