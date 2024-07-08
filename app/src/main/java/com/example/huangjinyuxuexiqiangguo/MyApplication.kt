package com.example.huangjinyuxuexiqiangguo

import android.app.Activity
import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Instance = this
    }
    var mainActivity: Activity? = null
    companion object {
        var Instance: MyApplication? = null
    }
}