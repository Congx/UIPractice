package com.example.uipractice.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uipractice.R
import com.example.uipractice.base.BaseItemListActivity
import com.example.uipractice.base.ItemBean
import com.example.uipractice.ui.ivpacage.ImageViewActivity
import com.example.uipractice.ui.keybord.KeybordActivity
import com.example.uipractice.ui.nestedscroll.NestedBaseActivity
import com.example.uipractice.ui.recyclerview.RecyclerviewBaseActivity

class UIActivity : BaseItemListActivity() {
    var list:ArrayList<ItemBean>? = arrayListOf(
        ItemBean("imageView相关", ImageViewActivity::class.java),
        ItemBean("嵌套滑动", NestedBaseActivity::class.java),
        ItemBean("recyclerview", RecyclerviewBaseActivity::class.java),
        ItemBean("软键盘", KeybordActivity::class.java),
        ItemBean("颜色-主题-属性", AttrStyleActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean>? {
        return list
    }

}
