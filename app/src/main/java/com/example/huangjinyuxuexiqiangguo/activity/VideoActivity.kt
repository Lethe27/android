package com.example.huangjinyuxuexiqiangguo.activity

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.widget.VideoView
import android.os.Bundle
import com.example.huangjinyuxuexiqiangguo.R
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.calligraphy3.CalligraphyConfig
import android.os.Build
import android.view.View
import android.widget.MediaController

import com.example.huangjinyuxuexiqiangguo.MyApplication
import com.example.huangjinyuxuexiqiangguo.util.SPUtils
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import java.util.*
class VideoActivity : AppCompatActivity() {
    var video = intArrayOf(R.raw.video1)
    private var mVideo: VideoView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video)
        mVideo = findViewById<View>(R.id.video) as VideoView
        mVideo!!.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + video[Random().nextInt(video.size)]))
        val controller = MediaController(this)
        mVideo!!.setMediaController(controller)
        mVideo!!.start()
        initFont()
    }
    override fun onDestroy() {
        super.onDestroy()
    }

    fun back(view: View?) {
        finish()
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