package com.example.uipractice.nestedscroll

import android.content.Context
import android.util.AttributeSet
import com.example.uipractice.BaseItemListActivity
import com.example.uipractice.ItemBean
import java.util.*
import com.google.android.material.snackbar.Snackbar
import android.view.View
import androidx.core.view.ViewCompat
import com.example.uipractice.R
import kotlinx.android.synthetic.main.activity_cordinglayout_base_use.*
import kotlinx.android.synthetic.main.activity_cordinglayout_behavior.*


class NestedBaseActivity : BaseItemListActivity() {

    var list:ArrayList<ItemBean>? = arrayListOf(
        ItemBean("coordinglayout基本用法",NestedBaseUsedActivity::class.java),
        ItemBean("自定义behavior",NestedBehaviorActivity::class.java)
    )

    override fun getListData(): ArrayList<ItemBean>? {
        return list
    }

}
