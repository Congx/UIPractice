package com.example.uipractice.ndk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uipractice.R
import kotlinx.android.synthetic.main.activity_n_d_k.*

class NDKActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_n_d_k)
        textView.text = add(-21,3).toString()
    }

    external fun add(a:Int,b:Int):Int

    companion object {
        init {
            System.loadLibrary("native-lib")
        }
    }
}