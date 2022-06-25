package com.example.uipractice.fragment.floatwindow

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.uipractice.R

object FloatViewManager {

  const val FLOAT_VIEW_ID = R.id.float_view

  fun showFloatView(activity: Activity) {
    if (isAdd(activity)) {
      if (!isShow(activity)) {
        internalShow(activity)
      }
    } else {
      internalAdd(activity)
    }
  }

  fun isAdd(activity: Activity): Boolean {
    return isAdd(activity.window.decorView as? ViewGroup)
  }

  fun isAdd(parent: ViewGroup?): Boolean {
    parent?.apply {
      return findViewById<View>(FLOAT_VIEW_ID) != null
    }
    return false
  }

  fun isShow(activity: Activity): Boolean {
    return activity.findViewById<View>(FLOAT_VIEW_ID) != null
  }

  fun isShow(parent: View?): Boolean {
    return parent?.findViewById<View>(FLOAT_VIEW_ID) != null
  }

  private fun internalShow(activity: Activity) {
    activity.findViewById<View>(FLOAT_VIEW_ID)?.apply {
      isVisible = true
    }
  }

  private fun internalShow(parent: ViewGroup) {
    parent.findViewById<View>(FLOAT_VIEW_ID)?.apply {
      isVisible = true
    }
  }

  private fun internalAdd(activity: Activity) {
    activity.window?.decorView?.let {
      internalAdd(it as? ViewGroup)
    }
  }

  private fun internalAdd(parent: ViewGroup?) {
    parent?.apply {
      val floatView = FloatView(context)
      val lp = ViewGroup.MarginLayoutParams(ViewGroup.MarginLayoutParams.WRAP_CONTENT, ViewGroup.MarginLayoutParams.WRAP_CONTENT)
      floatView.x = 200f
      floatView.y = 400f
      floatView.id = FLOAT_VIEW_ID
      addView(floatView, lp)
    }
  }
}