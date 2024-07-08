package com.example.huangjinyuxuexiqiangguo.activity

import android.content.Context
import com.example.huangjinyuxuexiqiangguo.util.SPUtils.put
import androidx.appcompat.app.AppCompatActivity
import com.example.huangjinyuxuexiqiangguo.util.MySqliteOpenHelper
import android.widget.EditText
import android.widget.TextView
import android.os.Bundle
import com.example.huangjinyuxuexiqiangguo.R
import android.content.Intent
import android.content.res.Resources
import android.widget.Toast
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.calligraphy3.CalligraphyConfig
import android.os.Build
import android.view.View
import android.widget.Button
import com.example.huangjinyuxuexiqiangguo.MyApplication
import com.example.huangjinyuxuexiqiangguo.bean.User
import com.example.huangjinyuxuexiqiangguo.util.SPUtils
import io.github.inflationx.viewpump.ViewPumpContextWrapper

// 登录页面
class LoginActivity : AppCompatActivity() {
    var helper: MySqliteOpenHelper? = null
    private var etAccount //账号
            : EditText? = null
    private var etPassword //密码
            : EditText? = null
    private var tvRegister //注册
            : TextView? = null
    private var btnLogin //登录按钮
            : Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        helper = MySqliteOpenHelper(this)
        etAccount = findViewById(R.id.et_account) //获取手机号
        etPassword = findViewById(R.id.et_password) //获取密码
        tvRegister = findViewById(R.id.tv_register) //获取注册
        btnLogin = findViewById(R.id.btn_login) //获取登录
        //手机号注册
        tvRegister!!.setOnClickListener(View.OnClickListener { //跳转到注册页面
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        })
        //设置点击按钮
        btnLogin!!.setOnClickListener(View.OnClickListener {
            //获取请求参数
            val account = etAccount!!.getText().toString()
            val password = etPassword!!.getText().toString()
            if ("" == account) { //用户名不能为空
                Toast.makeText(this@LoginActivity, "用户名不能为空", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            if ("" == password) { //密码为空
                Toast.makeText(this@LoginActivity, "密码不能为空", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            var mUser: User? = null
            //通过账号查询用户是否存在
            val sql = "select * from user where account = ?"
            val db = helper!!.writableDatabase
            val cursor = db.rawQuery(sql, arrayOf(account))
            if (cursor != null && cursor.columnCount > 0) {
                while (cursor.moveToNext()) {
                    val dbId = cursor.getInt(0)
                    val dbAccount = cursor.getString(1)
                    val dbPassword = cursor.getString(2)
                    val dbName = cursor.getString(3)
                    val dbSex = cursor.getString(4)
                    val dbPhone = cursor.getString(5)
                    val dbAddress = cursor.getString(6)
                    val dbPhoto = cursor.getString(7)
                    val score = cursor.getString(8)
                    mUser = User(dbId, dbAccount, dbPassword, dbName, dbSex, dbPhone, dbAddress, dbPhoto,score)
                }
            }
            db.close()
            if (mUser != null) { //用户存在
                if (password != mUser.password) { //判断密码是否正确
                    Toast.makeText(this@LoginActivity, "密码错误", Toast.LENGTH_SHORT).show()
                } else { //密码验证成功
                    put(this@LoginActivity, SPUtils.USER_ID, mUser.id)
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            } else { //账号不存在
                Toast.makeText(this@LoginActivity, "账号不存在", Toast.LENGTH_SHORT).show()
            }
        })
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