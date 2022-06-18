package com.example.uipractice.ui

import android.app.Activity
import com.example.uipractice.NewFragmentListActivity
import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean
import com.example.uipractice.recyclerview.layoutmanger.LayoutManagerActivity
import com.example.uipractice.ui.eventdispatch.EventDispatchActivity
import com.example.uipractice.ui.ivpacage.ImageViewActivity
import com.example.uipractice.ui.keybord.KeybordActivity
import com.example.uipractice.ui.nestedscroll.NestedBaseActivity
import com.example.uipractice.ui.others.OthersUIActivity
import com.example.uipractice.ui.others.ProgressbarActivity
import com.example.uipractice.ui.recyclerview.RecyclerviewBaseActivity
import com.example.uipractice.ui.viewPager2.ViewPagerActivity

class UIActivity : BaseItemListActivity<Activity>() {
    var list:ArrayList<ItemBean<out Activity>>? = arrayListOf(
        ItemBean("imageView相关", ImageViewActivity::class.java),
        ItemBean("嵌套滑动", NestedBaseActivity::class.java),
        ItemBean("recyclerview", RecyclerviewBaseActivity::class.java),
        ItemBean("软键盘", KeybordActivity::class.java),
        ItemBean("颜色-主题-属性", AttrStyleActivity::class.java),
        ItemBean("ViewPager-新懒加载", ViewPagerActivity::class.java),
        ItemBean("事件分发", EventDispatchActivity::class.java),
        ItemBean("自定义LayoutManager", LayoutManagerActivity::class.java),
        ItemBean("进度条", ProgressbarActivity::class.java),
        ItemBean("其他", OthersUIActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean<out Activity>>? {
        return list
    }

}
