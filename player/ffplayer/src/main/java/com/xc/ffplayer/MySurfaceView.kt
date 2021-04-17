package com.xc.ffplayer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView

class MySurfaceView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback ,Runnable{

    var isValidate = false

    //绘图的Canvas
    private var mCanvas: Canvas? = null

    //子线程标志位
    var x = 0.0
    var y = 0.0
    private lateinit var mPaint: Paint
    private lateinit var mPath: Path

    init {
        holder.addCallback(this)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        Log.e("MySurfaceView","surfaceChanged() -->format:$format,width:$width,height:$height")
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        isValidate = false
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        isValidate = true
        mPaint = Paint()
        mPaint.setColor(Color.BLACK)
        mPaint.setStyle(Paint.Style.STROKE)
        mPaint.setAntiAlias(true)
        mPaint.setStrokeWidth(5f)
        mPath = Path()
        //路径起始点(0, 100)
        //路径起始点(0, 100)
        mPath.moveTo(0f, 100f)
        initView()
    }

    override fun run() {
        while (isValidate){
            val start = System.currentTimeMillis()
            drawSomething()
            x += 1;
            y = (100 * Math.sin(2 * x * Math.PI / 180) + 400)
            //加入新的坐标点
            mPath?.lineTo(x.toFloat(), y.toFloat())

            val end = System.currentTimeMillis()
            if (end - start < 100) {
                try {
                    Thread.sleep(100 - (end - start))
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun drawSomething() {
        try {
            //获得canvas对象
            mCanvas = holder.lockCanvas()
            //绘制背景
            mCanvas?.drawColor(Color.CYAN)
            //绘制路径
            mCanvas?.drawPath(mPath!!, mPaint!!)
        } catch (e: Exception) {
        } finally {
            if (mCanvas != null) {
                //释放canvas对象并提交画布
                holder.unlockCanvasAndPost(mCanvas)
            }
        }
    }

    /**
     * 初始化View
     */
    private fun initView() {
        holder.addCallback(this)
        isFocusable = true
        keepScreenOn = true
        isFocusableInTouchMode = true
    }
}