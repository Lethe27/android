package com.example.huangjinyuxuexiqiangguo.activity

import androidx.appcompat.app.AppCompatActivity
import com.example.huangjinyuxuexiqiangguo.util.MySqliteOpenHelper
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.widget.EditText
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
// 重置密码
class PasswordActivity : AppCompatActivity() {
    var helper: MySqliteOpenHelper? = null
    private var activity: Activity? = null
    private var etNewPassword: EditText? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = this
        setContentView(R.layout.activity_password)
        helper = MySqliteOpenHelper(this)
        etNewPassword = findViewById(R.id.et_new_password)
        initFont()
    }
    //保存信息
    fun save(v: View?) {
        val db = helper!!.writableDatabase
        val newPassword = etNewPassword!!.text.toString()
        if ("" == newPassword) { //密码为空
            Toast.makeText(activity, "新密码为空", Toast.LENGTH_LONG).show()
            return
        }
        val userId = SPUtils[this@PasswordActivity, SPUtils.USER_ID, 0] as Int?
        db.execSQL("update user set password = ? where id = ?", arrayOf<Any?>(newPassword,userId))
        Toast.makeText(this@PasswordActivity, "更新成功", Toast.LENGTH_SHORT).show()
        finish()
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