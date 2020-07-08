package com.example.uipractice.ui.viewPager2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.base.framwork.fragment.AbstractFragment
import com.example.uipractice.R

/**
 * @date 2020-01-04
 * @Author luffy
 * @description
 */
class TestFragment : AbstractFragment() {
    
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
        return inflater.inflate(R.layout.fragment_test,container,false)
    }
}
