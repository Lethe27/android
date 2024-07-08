package com.example.huangjinyuxuexiqiangguo.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.widget.Toast

class NetworkReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == ConnectivityManager.CONNECTIVITY_ACTION) {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            //说明当前有网络
            if (networkInfo != null && networkInfo.isAvailable) {
                val type = networkInfo.type
                when (type) {
                    ConnectivityManager.TYPE_MOBILE -> Toast.makeText(context, "当前移动网络正常", Toast.LENGTH_SHORT).show()
                    ConnectivityManager.TYPE_WIFI -> Toast.makeText(context, "当前WIFI网络正常", Toast.LENGTH_SHORT).show()
                    ConnectivityManager.TYPE_ETHERNET -> Toast.makeText(context, "当前以太网网络正常", Toast.LENGTH_SHORT).show()
                }
            } else {
                //说明当前没有网络
                Toast.makeText(context, "当前网络异常", Toast.LENGTH_SHORT).show()
            }
        }
    }
}