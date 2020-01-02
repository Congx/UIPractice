package com.example.uipractice.ui.recyclerview.itemdivider

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import android.content.Context
import com.example.uipractice.R


/**
 * @date 2019-10-17
 * @Author luffy
 * @description
 */
class SimpleItemDecoration : RecyclerView.ItemDecoration {

    private var wight: Int = 0
    private var height: Int = 0
    private var item_height: Int = 0
    private var paint: Paint? = null
    private var paintOver: Paint? = null
    private var item_padding: Float = 0.toFloat()

    companion object {
        var TAG:String = SimpleItemDecoration.javaClass.simpleName
    }

    constructor(context: Context) :super() {
        wight = context.getResources().getDisplayMetrics().widthPixels
        height = context.getResources().getDisplayMetrics().heightPixels
        paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.DITHER_FLAG)
        //更改画笔颜色为自定义的颜色
        paint?.setColor(context.getResources().getColor(R.color.colorAccent))
        paintOver = Paint()
        paintOver?.color = 0x3300FF00
        item_height = 60
        item_padding = 10F
    }


    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
//        Log.e(TAG, state.remainingScrollVertical.toString())
        Log.e(TAG,"onDraw")
        super.onDraw(c, parent, state)

        val count = parent.childCount
        for (i in 0 until count) {
            val view = parent.getChildAt(i)
            //分割线不能和item的内容重叠,所以把分割线画在getItemOffsets为item腾出来的位置上.
            //所以top需要上移item_height
            val top = view.top - item_height
            //bottom同理
            val bottom = top + item_height
            c.drawRect(0F, top.toFloat(), wight.toFloat(), bottom.toFloat(), paint)
        }
    }


    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        var childAt = parent.getChildAt(0)
        childAt?.let {
            if(childAt.bottom < item_height) {
                c.drawRect(0F, 0f, wight.toFloat(), childAt.bottom.toFloat(), paintOver)
            }else {
                c.drawRect(0F, 0f, wight.toFloat(), item_height.toFloat(), paintOver)
            }
        }
    }

    override fun getItemOffsets(
        outRect: Rect,//
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = item_height;
    }

}