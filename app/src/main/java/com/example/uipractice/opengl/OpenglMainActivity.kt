package com.example.uipractice.opengl

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uipractice.R
import kotlinx.android.synthetic.main.activity_opengl_main.*

class OpenglMainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_opengl_main)

        btnBase.setOnClickListener {
            startActivity(Intent(this,OpenglBaseUseActivity::class.java))
        }

        btnCamera.setOnClickListener {
            startActivity(Intent(this,OpenglCameractivity::class.java))
        }
    }
}