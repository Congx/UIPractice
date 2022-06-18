package com.example.uipractice

import androidx.fragment.app.Fragment
import com.example.uipractice.base.FragmentItemListActivity
import com.example.uipractice.base.ItemBean
import com.example.uipractice.fragment.floatwindow.FloatWindowFragment

class NewFragmentListActivity : FragmentItemListActivity() {

  override fun getListData(): ArrayList<out ItemBean<out Fragment>>? {
    return arrayListOf(
      ItemBean("悬浮窗", FloatWindowFragment::class.java)
    )
  }
}