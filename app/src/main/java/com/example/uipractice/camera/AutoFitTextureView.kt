package com.example.uipractice.camera

import android.content.Context
import android.util.AttributeSet
import android.view.TextureView

class AutoFitTextureView : TextureView {
    private var ratioW = 0
    private var ratioH = 0

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context!!, attrs, defStyleAttr) {}

    /**
     * 设置宽高比
     * @param width
     * @param height
     */
    fun setAspectRation(width: Int, height: Int) {
        require(!(width < 0 || height < 0)) { "width or height can not be negative." }
        ratioW = width
        ratioH = height
        //请求重新布局
        requestLayout()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        if (0 == ratioW || 0 == ratioH) {
            //未设定宽高比，使用预览窗口默认宽高
            setMeasuredDimension(width, height)
        } else {
            //设定宽高比，调整预览窗口大小（调整后窗口大小不超过默认值）
            if (width < height * ratioW / ratioH) {
                setMeasuredDimension(width, width * ratioH / ratioW)
            } else {
                setMeasuredDimension(height * ratioW / ratioH, height)
            }
        }
    }
}