package com.example.uipractice.ui.nestedscroll

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.uipractice.R

/**
 * 基本用法
 */
open class NestedBaseUsedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cordinglayout_base_use)
    }
}
