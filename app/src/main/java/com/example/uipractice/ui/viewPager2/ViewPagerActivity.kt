package com.example.uipractice.ui.viewPager2

import android.app.Activity
import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean
import com.example.uipractice.ui.recyclerview.itemdivider.Viewpager2Activity

class ViewPagerActivity : BaseItemListActivity<Activity>() {

    var list:ArrayList<ItemBean<out Activity>>? = arrayListOf(
        ItemBean("ViewPager1", ViewPagerTestActivity::class.java),
        ItemBean("ViewPager2", Viewpager2Activity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean<out Activity>>? {
        return list
    }
}
