package com.example.uipractice

import com.example.uipractice.architecture.ArchitectureActivity
import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean
import com.example.uipractice.basepractice.BaseLibActivity
import com.example.uipractice.fragment.FragmentListActivity
import com.example.uipractice.rxjava.RxJavaActivity
import com.example.uipractice.ui.AttrStyleActivity
import com.example.uipractice.ui.UIActivity
import com.example.uipractice.ui.ivpacage.ImageViewActivity
import com.example.uipractice.ui.keybord.KeybordActivity
import com.example.uipractice.ui.nestedscroll.NestedBaseActivity
import com.example.uipractice.ui.recyclerview.RecyclerviewBaseActivity

class MainActivity : BaseItemListActivity() {

    var list:ArrayList<ItemBean>? = arrayListOf(
        ItemBean("ui相关", UIActivity::class.java),
        ItemBean("架构组件", ArchitectureActivity::class.java),
        ItemBean("rxJava", RxJavaActivity::class.java),
        ItemBean("base库的一些用法", BaseLibActivity::class.java),
        ItemBean("fragment", FragmentListActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean>? {
        return list
    }

}
