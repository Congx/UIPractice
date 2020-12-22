package com.example.uipractice.ui.nestedscroll

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.base.utils.toPX
import com.example.uipractice.R
import com.google.android.material.appbar.AppBarLayout
import kotlin.math.absoluteValue
import kotlin.math.log


class MainTitleBehavior(context: Context, attr: AttributeSet): CoordinatorLayout.Behavior<View>(context,attr) {

    // 界面整体向上滑动，达到列表可滑动的临界点
    private var upReach = false

    // 列表向上滑动后，再向下滑动，达到界面整体可滑动的临界点
    private var downReach = false

    // 列表上一个全部可见的item位置
    private var lastPosition = -1

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: View,
        ev: MotionEvent
    ): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                downReach = false
                upReach = false
            }
        }
        return super.onInterceptTouchEvent(parent, child, ev)
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    var appBarLayout:AppBarLayout? = null
    var verticalOffset:Int = 0

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {

        appBarLayout = parent.findViewById<AppBarLayout>(R.id.appbarLayout)

        appBarLayout?.addOnOffsetChangedListener(AppBarLayout.BaseOnOffsetChangedListener<AppBarLayout> { p0, p1 ->
            verticalOffset = p1
            Log.e("verticalOffset",verticalOffset.toString())
            if (verticalOffset == 0) {
                child.translationY = 0f
            }
        })
        val layoutParams: CoordinatorLayout.LayoutParams = appBarLayout?.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = layoutParams.behavior
//        behavior?.apply {
//            this as AppBarLayout.Behavior
//            setDragCallback(object : AppBarLayout.Behavior.DragCallback() {
//                override fun canDrag(appBarLayout: AppBarLayout): Boolean {
//                    return true
//                }
//            })
//        }

        return super.onLayoutChild(parent, child, layoutDirection)
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)

        if (target is NestedScrollView) {
            if (verticalOffset.absoluteValue <= 50.toPX()) {
                child.translationY = verticalOffset.toFloat()
            }
        }

        if (target is RecyclerView) {
            // 列表第一个全部可见Item的位置
            val pos = (target.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
            if (pos == 0 && pos < lastPosition) {
                downReach = true
            }
            // 整体可以滑动，否则RecyclerView消费滑动事件
            if (canScroll(child, dy.toFloat()) && pos == 0) {
                var finalY = child.translationY - dy
                if (finalY < -child.height) {
                    finalY = (-child.height).toFloat()
                    upReach = true
                } else if (finalY > 0) {
                    finalY = 0f
                }
                child.translationY = finalY
                // 让CoordinatorLayout消费滑动事件
                consumed[1] = dy
            }
            lastPosition = pos
        }
    }

    override fun onNestedPreFling(
        coordinatorLayout: CoordinatorLayout,
        child: View,
        target: View,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        return super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY)
    }

    private fun canScroll(child: View, scrollY: Float): Boolean {
        if (scrollY > 0 && child.translationY == -child.height.toFloat() && !upReach) {
            return false
        }
        if (downReach) {
            return false
        }
        return true
    }
}