package com.example.huangjinyuxuexiqiangguo.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.huangjinyuxuexiqiangguo.util.MySqliteOpenHelper
import android.os.Bundle
import com.example.huangjinyuxuexiqiangguo.R
import android.widget.LinearLayout
import android.content.Intent
import android.content.res.Resources
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.calligraphy3.CalligraphyConfig
import android.os.Build
import android.os.Handler
import com.example.huangjinyuxuexiqiangguo.MyApplication
import com.example.huangjinyuxuexiqiangguo.util.SPUtils
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import java.text.SimpleDateFormat
//登陆页面
class OpenActivity : AppCompatActivity() {
    var helper: MySqliteOpenHelper? = null
    private val sf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open)
        val bg = SPUtils[this, "bg", 0] as Int
        val root = findViewById<LinearLayout>(R.id.root)
        when (bg) {
            0 -> {
                root.setBackgroundResource(R.drawable.splash_layer)
            }
            1 -> {
                root.setBackgroundResource(R.drawable.splash_layer_1)
            }
            2 -> {
                root.setBackgroundResource(R.drawable.splash_layer_2)
            }
            3 -> {
                root.setBackgroundResource(R.drawable.splash_layer_3)
            }
        }
        helper = MySqliteOpenHelper(this)
        Handler().postDelayed({
            val userId = SPUtils[this@OpenActivity, SPUtils.USER_ID, 0] as Int?
            //两秒后跳转到主页面
            val intent2 = Intent()
            if (userId!! > 0) {
                intent2.setClass(this@OpenActivity, MainActivity::class.java)
            } else {
                intent2.setClass(this@OpenActivity, LoginActivity::class.java)
            }
            startActivity(intent2)
            finish()
        }, 2000)
        initFont()
    }

    private fun initFont() {
        ViewPump.init(ViewPump.builder()
                .addInterceptor(CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                                .setDefaultFontPath(SPUtils[this, "fonts", "fonts/Roboto-Bold.ttf"] as String?)
                                .setFontAttrId(R.attr.fontPath)
                                .build()
                ))
                .build())
    }

    override fun attachBaseContext(newBase: Context) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            val res = newBase.resources
            val config = res.configuration
            config.fontScale = textSize
            val newContext = newBase.createConfigurationContext(config)
            super.attachBaseContext(ViewPumpContextWrapper.wrap(newContext))
        } else {
            super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
        }
    }

    override fun getResources(): Resources {
        val res = super.getResources()
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            val config = res.configuration
            config.fontScale = textSize
            res.updateConfiguration(config, res.displayMetrics)
        }
        return res
    }

    //设置字体大、中、小
    private val textSize: Float
        private get() {
            val fontScale: Float
            val sizeType = SPUtils[MyApplication.Instance!!.applicationContext, "tt", 2] as Int
            fontScale = if (sizeType == 3) {
                1.5f
            } else if (sizeType == 2) {
                1.0f
            } else {
                0.5f
            }
            return fontScale
        }
}