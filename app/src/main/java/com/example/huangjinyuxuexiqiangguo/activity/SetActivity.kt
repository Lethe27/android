package com.example.huangjinyuxuexiqiangguo.activity

import android.content.Context
import android.content.res.Resources
import com.example.huangjinyuxuexiqiangguo.util.SPUtils.put
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.huangjinyuxuexiqiangguo.R
import android.widget.Toast
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.calligraphy3.CalligraphyConfig
import android.os.Build
import android.view.View
import com.example.huangjinyuxuexiqiangguo.MyApplication
import com.example.huangjinyuxuexiqiangguo.util.SPUtils
import io.github.inflationx.viewpump.ViewPumpContextWrapper

class SetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_font)
        initFont()
        findViewById<View>(R.id.iv_back).setOnClickListener { finish() }
        findViewById<View>(R.id.ll_gtw).setOnClickListener { changeFont("gtw") }
        findViewById<View>(R.id.ll_hb).setOnClickListener { changeFont("HarmonyOS_Sans_Condensed_Bold") }
        findViewById<View>(R.id.ll_rb).setOnClickListener { changeFont("Roboto-Bold") }
        findViewById<View>(R.id.ll_os).setOnClickListener { changeFont("Oswald-Stencbab") }
        findViewById<View>(R.id.ll_rt).setOnClickListener { changeFont("Roboto-ThinItalic") }
        findViewById<View>(R.id.ll_rr).setOnClickListener { changeFont("RobotoCondensed-Regular") }
        findViewById<View>(R.id.ll_big).setOnClickListener { changeFontSize(3) }
        findViewById<View>(R.id.ll_normal).setOnClickListener { changeFontSize(2) }
        findViewById<View>(R.id.ll_small).setOnClickListener { changeFontSize(1) }
        findViewById<View>(R.id.iv_1).setOnClickListener { changeBg(0) }
        findViewById<View>(R.id.iv_2).setOnClickListener { changeBg(1) }
        findViewById<View>(R.id.iv_3).setOnClickListener { changeBg(2) }
        findViewById<View>(R.id.iv_4).setOnClickListener { changeBg(3) }
        reShow()
    }

    private fun changeBg(type: Int) {
        put(this, "bg", type)
        reShow()
        Toast.makeText(this, "重启APP后生效!!!", Toast.LENGTH_SHORT).show()
    }

    private fun changeFontSize(tt: Int) {
        put(this, "tt", tt)
        reShow()
        Toast.makeText(this, "重启APP后生效!!!", Toast.LENGTH_SHORT).show()
    }
    private fun changeFont(fontS: String) {
        put(this, "fonts", "fonts/$fontS.ttf")
        reShow()
        Toast.makeText(this, "重启APP后生效!!!", Toast.LENGTH_SHORT).show()
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
    //重写字体大小资源获取方法
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

    private fun reShow() {
        val bg = SPUtils[this, "bg", 0] as Int
        when (bg) {
            0 -> {
                findViewById<View>(R.id.ll_iv_1).setBackgroundResource(R.drawable.shape_select)
                findViewById<View>(R.id.ll_iv_2).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_iv_3).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_iv_4).setBackgroundDrawable(null)
            }
            1 -> {
                findViewById<View>(R.id.ll_iv_1).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_iv_2).setBackgroundResource(R.drawable.shape_select)
                findViewById<View>(R.id.ll_iv_3).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_iv_4).setBackgroundDrawable(null)
            }
            2 -> {
                findViewById<View>(R.id.ll_iv_1).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_iv_2).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_iv_3).setBackgroundResource(R.drawable.shape_select)
                findViewById<View>(R.id.ll_iv_4).setBackgroundDrawable(null)
            }
            3 -> {
                findViewById<View>(R.id.ll_iv_1).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_iv_2).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_iv_3).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_iv_4).setBackgroundResource(R.drawable.shape_select)
            }
        }
        val sizeType = SPUtils[this, "tt", 2] as Int
        if (sizeType == 1) {
            findViewById<View>(R.id.ll_small).setBackgroundResource(R.drawable.shape_select)
            findViewById<View>(R.id.ll_big).setBackgroundDrawable(null)
            findViewById<View>(R.id.ll_normal).setBackgroundDrawable(null)
        }
        if (sizeType == 2) {
            findViewById<View>(R.id.ll_normal).setBackgroundResource(R.drawable.shape_select)
            findViewById<View>(R.id.ll_big).setBackgroundDrawable(null)
            findViewById<View>(R.id.ll_small).setBackgroundDrawable(null)
        }
        if (sizeType == 3) {
            findViewById<View>(R.id.ll_big).setBackgroundResource(R.drawable.shape_select)
            findViewById<View>(R.id.ll_normal).setBackgroundDrawable(null)
            findViewById<View>(R.id.ll_small).setBackgroundDrawable(null)
        }
        val fonts = SPUtils[this, "fonts", "fonts/Roboto-Bold.ttf"] as String?
        when (fonts) {
            "fonts/Roboto-Bold.ttf" -> {
                findViewById<View>(R.id.ll_gtw).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_rb).setBackgroundResource(R.drawable.shape_select)
                findViewById<View>(R.id.ll_hb).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_rt).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_rr).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_os).setBackgroundDrawable(null)
            }
            "fonts/gtw.ttf" -> {
                findViewById<View>(R.id.ll_rb).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_gtw).setBackgroundResource(R.drawable.shape_select)
                findViewById<View>(R.id.ll_hb).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_rt).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_rr).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_os).setBackgroundDrawable(null)
            }
            "fonts/HarmonyOS_Sans_Condensed_Bold.ttf" -> {
                findViewById<View>(R.id.ll_rb).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_hb).setBackgroundResource(R.drawable.shape_select)
                findViewById<View>(R.id.ll_gtw).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_rt).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_rr).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_os).setBackgroundDrawable(null)
            }
            "fonts/Roboto-ThinItalic.ttf" -> {
                findViewById<View>(R.id.ll_rb).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_rt).setBackgroundResource(R.drawable.shape_select)
                findViewById<View>(R.id.ll_hb).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_hb).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_rr).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_os).setBackgroundDrawable(null)
            }
            "fonts/RobotoCondensed-Regular.ttf" -> {
                findViewById<View>(R.id.ll_rb).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_rr).setBackgroundResource(R.drawable.shape_select)
                findViewById<View>(R.id.ll_hb).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_os).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_rt).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_rt).setBackgroundDrawable(null)
            }
            "fonts/Oswald-Stencbab.ttf" -> {
                findViewById<View>(R.id.ll_rb).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_os).setBackgroundResource(R.drawable.shape_select)
                findViewById<View>(R.id.ll_rr).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_hb).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_rt).setBackgroundDrawable(null)
                findViewById<View>(R.id.ll_rt).setBackgroundDrawable(null)
            }
        }
    }
}