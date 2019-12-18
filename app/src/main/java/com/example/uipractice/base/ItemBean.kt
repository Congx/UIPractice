package com.example.uipractice.base

import android.app.Activity

data class ItemBean(var content:String,var clazz:Class<out Activity>)
