package com.base.view

import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class SpaceItemDecoration(var horizontalSize:Int? = 0,var verticalSize:Int? = 0,var spaceSize:Int? = null) : ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemCount = parent.adapter?.itemCount ?: 0
        var layoutManager = parent.layoutManager
        val position = layoutManager?.getPosition(view) ?: 1
        if (layoutManager is GridLayoutManager) {
            var rights = (spaceSize ?: verticalSize) ?: 0
            var bottom = (spaceSize ?: horizontalSize) ?: 0

            if (isEndRow(parent,position,getSpanCount(parent),itemCount)) {
                rights = 0
            }
            if (isLastRaw(parent,position,getSpanCount(parent),itemCount)) {
                bottom = 0
            }
            outRect.set(0,0,rights,bottom)
        }else if (layoutManager is LinearLayoutManager) {
            if (!isLastRaw(parent,position,itemCount,itemCount)) {
                outRect.set(0,0,0,0)
            }else {
                var rights = (spaceSize ?: verticalSize) ?: 0
                var bottom = (spaceSize ?: horizontalSize) ?: 0
                outRect.set(0,0,rights,bottom)
            }
        }

    }

    /**
     * 一行的最后一个
     */
    private fun isEndRow(parent: RecyclerView, position: Int, spanCount: Int, itemCount: Int): Boolean {
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            val spanSize = spanCount / layoutManager.spanSizeLookup.getSpanSize(position)
            Log.e("spanSize",spanSize.toString())
            return (position+1) % spanSize == 0
        }

        return false
    }

    private fun isLastRaw(parent: RecyclerView, pos: Int, spanCount: Int, childCount: Int): Boolean {
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            val realSpanSize = spanCount / layoutManager.spanSizeLookup.getSpanSize(pos)
            val remainCount = childCount % realSpanSize //获取余数
            //如果正好最后一行完整;
            if (remainCount == 0) {
                if (pos >= childCount - realSpanSize) {
                    return true //最后一行全部不绘制;
                }
            } else {
                if (pos >= childCount - childCount % realSpanSize) {
                    return true
                }
            }

        } else if (layoutManager is LinearLayoutManager) {
            return pos == childCount -1
        }
        return false
    }

    /**
     * 获取列数
     */
    private fun getSpanCount(
        parent: RecyclerView
    ): Int {
        // 列数
        var mSpanCount = 1
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            mSpanCount = layoutManager.spanCount
        } else if (layoutManager is StaggeredGridLayoutManager) {
            mSpanCount = layoutManager.spanCount
        }else if (layoutManager is LinearLayoutManager) {
            mSpanCount = 1
        }
        return mSpanCount
    }

}