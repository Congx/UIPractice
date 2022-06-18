package com.example.uipractice.fragment

import android.app.Activity
import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean
import com.example.uipractice.basepractice.BaseLibActivity

class FragmentListActivity : BaseItemListActivity<Activity>() {

    var list:ArrayList<ItemBean<out Activity>>? = arrayListOf(
        ItemBean("fragment生命周期", FragmentLiveActivity::class.java),
        ItemBean("fragment生命周期", FragmentLifecyclerActivity::class.java),
        ItemBean("fragment生命周期-navigator", FragmentNavigatorLifecyclerActivity::class.java),
        ItemBean("Viewpager2生命周期", FragmentViewpager2Activity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean<out Activity>>? {
        return list
    }
}
