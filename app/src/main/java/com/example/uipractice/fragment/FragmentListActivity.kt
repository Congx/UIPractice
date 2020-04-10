package com.example.uipractice.fragment

import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean
import com.example.uipractice.basepractice.BaseLibActivity

class FragmentListActivity : BaseItemListActivity() {

    var list:ArrayList<ItemBean>? = arrayListOf(
        ItemBean("fragment生命周期", FragmentLiveActivity::class.java),
        ItemBean("fragment生命周期", FragmentLifecyclerActivity::class.java),
        ItemBean("fragment生命周期-navigator", FragmentNavigatorLifecyclerActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean>? {
        return list
    }
}
