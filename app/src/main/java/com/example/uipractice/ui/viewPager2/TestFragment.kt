package com.example.uipractice.ui.viewPager2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.base.framwork.fragment.BaseFragment

import com.example.uipractice.R
import kotlinx.android.synthetic.main.fragment_test.*

/**
 * @date 2020-01-04
 * @Author luffy
 * @description
 */
class TestFragment : BaseFragment() {
    
    var title:String? = null

    companion object {
        fun instance(title: String):Fragment {
            var fragment = TestFragment()
            var bundle = Bundle()
            bundle.putString("title",title)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun initParams(intent: Intent) {
        title = arguments?.getString("title")
    }

    override fun generateIdLayout(): Int {
        return R.layout.fragment_test
    }

    override fun initView() {
        var textView = findViewById<TextView>(R.id.textView)
        textView.text = title
    }

    override fun initEvent() {
    }

    override fun initData() {
    }

}
