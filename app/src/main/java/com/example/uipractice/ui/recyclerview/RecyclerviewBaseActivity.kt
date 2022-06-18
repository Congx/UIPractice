package com.example.uipractice.ui.recyclerview

import android.app.Activity
import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean
import com.example.uipractice.ui.recyclerview.itemdivider.RvDividerActivity
import com.example.uipractice.ui.viewPager2.ViewPagerActivity

import java.util.ArrayList

class RecyclerviewBaseActivity : BaseItemListActivity<Activity>() {


    var list:ArrayList<ItemBean<out Activity>>? = arrayListOf(
        ItemBean("recyclerview基本用法", RvDividerActivity::class.java),
        ItemBean("viewpager2", ViewPagerActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean<out Activity>>? {
        return list
    }
}
