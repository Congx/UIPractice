package com.example.uipractice.rxjava

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.uipractice.R
import com.example.uipractice.api.ApiRepository
import com.example.uipractice.api.AppApi
import com.example.uipractice.net.RetrofitServer

class BackPressActivity : AppCompatActivity() {

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_back_press)
        ApiRepository.getPublishList()
            .subscribe({
                Log.e("xx", it.size.toString())
            },{ e->e.printStackTrace()})
    }

}