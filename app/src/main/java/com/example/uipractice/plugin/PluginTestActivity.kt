package com.example.uipractice.plugin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.uipractice.R
import com.example.uipractice.plugin.service.PluginService
import com.example.uipractice.plugin.service.ProxyService
import kotlinx.android.synthetic.main.activity_plugin_test.*

class PluginTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plugin_test)

        button.setOnClickListener {
            var intent = Intent(this@PluginTestActivity,PluginActivity::class.java)
            startActivity(intent)
        }

        startService.setOnClickListener {
            startService(Intent(this@PluginTestActivity, PluginService::class.java))
        }
    }
}