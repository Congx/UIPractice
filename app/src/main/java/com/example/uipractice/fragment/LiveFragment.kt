package com.example.uipractice.fragment

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.uipractice.MainActivity
import com.example.uipractice.R

/**
 * @date 2020-01-18
 * @Author luffy
 * @description
 */
class LiveFragment : Fragment() {

    companion object {
        var instance = LiveFragment()
        var index = 0
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflate = inflater.inflate(R.layout.fragment_live, container, false);
        Log.e("LiveFragment","onCreateView")
        Log.e("LiveFragment",hashCode().toString())
        var textView = inflate.findViewById<TextView>(R.id.textView)
        textView.setOnClickListener { startActivity(Intent(activity,MainActivity::class.java)) }
        textView.text = index++.toString()
        activity.hashCode()
        return inflate
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.e("LiveFragment","onSaveInstanceState")
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        Log.e("LiveFragment","onViewStateRestored")
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
    }

    override fun onResume() {
        super.onResume()
        Log.e("LiveFragment","onResume")

//        fragmentManager.frag
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("LiveFragment","onDestroy")
    }
}