package com.base.view

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.base.framwork.R
import com.base.utils.toPX

class LinearItemDecoration @JvmOverloads constructor(color: Int = R.color.colorPrimary,
                                                     var size: Int = 1,
                                                     var orientation: Int = LinearLayoutManager.VERTICAL,
                                                     var paddingStart:Int = 0, var paddingEnd:Int = 0,
                                                     var paddingTop:Int = 0, var paddingBottom:Int = 0) : ItemDecoration() {

    private val mDivider: Drawable

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        var left: Int
        var right: Int
        var top: Int
        var bottom: Int
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            top = parent.paddingTop
            bottom = parent.height - parent.paddingBottom
            val childCount = parent.childCount
            for (i in 0 until childCount - 1) {
                val child = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams
                left = child.right + params.rightMargin + paddingStart.toPX()
                right = left + size - paddingEnd.toPX()
                mDivider.setBounds(left, top, right, bottom)
                mDivider.draw(c)
            }
        } else {
            left = parent.paddingLeft + paddingStart.toPX()
            right = parent.width - parent.paddingRight - paddingEnd.toPX()
            val childCount = parent.childCount
            for (i in 0 until childCount - 1) {
                val child = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams
                top = child.bottom + params.bottomMargin
                bottom = top + size
                mDivider.setBounds(left, top, right, bottom)
                mDivider.draw(c)
            }
        }
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            outRect[0, 0, size] = 0
        } else {
            outRect[0, 0, 0] = size
        }
    }

    init {
        mDivider = ColorDrawable(Color.RED)
    }
}