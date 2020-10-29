package com.example.uipractice.ui.viewPager2

import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean
import com.example.uipractice.ui.recyclerview.itemdivider.Viewpager2Activity

class ViewPagerActivity : BaseItemListActivity() {

    var list:ArrayList<ItemBean>? = arrayListOf(
        ItemBean("ViewPager1", ViewPagerTestActivity::class.java),
        ItemBean("ViewPager2", Viewpager2Activity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean>? {
        return list
    }
}
