package com.example.huangjinyuxuexiqiangguo.activity

import com.example.huangjinyuxuexiqiangguo.util.SPUtils.put
import androidx.appcompat.app.AppCompatActivity
import android.widget.EditText
import android.os.Bundle
import android.view.View
import com.example.huangjinyuxuexiqiangguo.R
import android.widget.Toast
import com.example.huangjinyuxuexiqiangguo.util.SPUtils
//应用帮助页面
class AboutUsActivity : AppCompatActivity() {
    lateinit var mEd: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        findViewById<View>(R.id.iv_back).setOnClickListener { finish() }
        mEd = findViewById(R.id.ed_help)
        val help = SPUtils[this@AboutUsActivity, "help", """
    1.App 通过本地数据库登录注册模块登录App主页面
    2.主页面通过京东聚合数据平台API请求新闻数据
    3.用户可通过我的页面修改个人信息，应用内字体和字体大小以及欢迎页面背景！！！
    """.trimIndent()] as String?
        mEd.setText(help)
        findViewById<View>(R.id.bt_ed).setOnClickListener {
            val s = mEd.getText().toString()
            put(this@AboutUsActivity, "help", s)
            Toast.makeText(this@AboutUsActivity, "修改帮助信息成功", Toast.LENGTH_SHORT).show()
        }
    }
}