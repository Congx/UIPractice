package com.example.uipractice.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.uipractice.R
import kotlinx.android.synthetic.main.activity_fragment_live.*
import kotlinx.android.synthetic.main.activity_fragment_live_back.*

/**
 * fragment 被回收的生命周期
 */
class FragmentLiveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_live_back)

        var fragment1:Fragment = LiveFragment()
        var fragment2:Fragment = LiveFragment()

        supportFragmentManager.beginTransaction().add(R.id.content,fragment1).commit()

        btnFragment1.setOnClickListener {
            supportFragmentManager.beginTransaction().add(R.id.content,fragment1).commit()
            supportFragmentManager.beginTransaction().hide(fragment1)
        }

        btnFragment2.setOnClickListener {
            supportFragmentManager.beginTransaction().add(R.id.content,fragment2).commit()
        }

        Log.e("FragmentLiveActivity","onCreate")
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        Log.e("FragmentLiveActivity","onResume")
        super.onSaveInstanceState(outState, outPersistentState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        Log.e("FragmentLiveActivity","onResume")
    }

    override fun onResume() {
        super.onResume()
        Log.e("FragmentLiveActivity","onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("FragmentLiveActivity","onDestroy")
    }
}
