package com.base.utils

import android.content.Context
import android.view.WindowManager


/**
 * 获取屏幕宽高
 */
fun Context.screenWidthHeight(): Pair<Int, Int> {
  var width: Int
  var height: Int
  val wm = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
  if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
    val metrics = wm.currentWindowMetrics
    width = metrics.bounds.width()
    height = metrics.bounds.height()
  } else {
    width = wm.defaultDisplay.width
    height = wm.defaultDisplay.height
  }
  return Pair(width, height)
}