package com.example.uipractice.ui.viewPager2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.base.custom.XBaseFragment
import com.base.framwork.fragment.AbstractFragment
import com.example.uipractice.R
import kotlinx.android.synthetic.main.fragment_test.*

/**
 * @date 2020-01-04
 * @Author luffy
 * @description
 */
class TestFragment : XBaseFragment() {
    
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initParams()
        return layoutInflater.inflate(R.layout.fragment_test,container,false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView.text = title

    }

    fun initParams() {
        title = arguments?.getString("title")
    }

}
