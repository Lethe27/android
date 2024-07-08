package com.example.huangjinyuxuexiqiangguo.activity

import androidx.appcompat.app.AppCompatActivity
import com.example.huangjinyuxuexiqiangguo.util.MySqliteOpenHelper
import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import com.example.huangjinyuxuexiqiangguo.R
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.calligraphy3.CalligraphyConfig
import android.os.Build
import android.view.View
import android.widget.*
import com.example.huangjinyuxuexiqiangguo.MyApplication
import com.example.huangjinyuxuexiqiangguo.bean.User
import com.example.huangjinyuxuexiqiangguo.util.SPUtils
import io.github.inflationx.viewpump.ViewPumpContextWrapper

// 个人信息
class PersonActivity : AppCompatActivity() {
    var helper: MySqliteOpenHelper? = null
    private var mActivity: Activity? = null
    private var tvAccount: TextView? = null
    private var etNickName: EditText? = null
    private var etPhone //手机号
            : EditText? = null
    private var etAddress //简介
            : EditText? = null
    private var rgSex //性别
            : RadioGroup? = null
    private var btnSave //保存
            : Button? = null
    var mUser: User? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person)
        mActivity = this
        helper = MySqliteOpenHelper(this)
        tvAccount = findViewById(R.id.tv_account)
        etNickName = findViewById(R.id.et_nickName) //获取昵称
        etPhone = findViewById(R.id.et_phone) //获取手机号
        etAddress = findViewById(R.id.et_address) //获取简介
        rgSex = findViewById(R.id.rg_sex)
        btnSave = findViewById(R.id.btn_save)
        initView()
        initFont()
    }
    private fun initView() {
        val userId = SPUtils[mActivity!!, SPUtils.USER_ID, 0] as Int?
        val db = helper!!.writableDatabase
        val sql = "select * from user where id = ?"
        val cursor = db.rawQuery(sql, arrayOf(userId.toString()))
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
                tvAccount!!.text = dbAccount
                etNickName!!.setText(dbName)
                etPhone!!.setText(dbPhone)
                etAddress!!.setText(dbAddress)
                rgSex!!.check(if ("男" == dbSex) R.id.rb_man else R.id.rb_woman)
                mUser = User(dbId, dbAccount, dbPassword, dbName, dbSex, dbPhone, dbAddress, dbPhoto,score)
            }
        }
        db.close()
        //保存
        btnSave!!.setOnClickListener(View.OnClickListener {
            val db = helper!!.writableDatabase
            val nickName = etNickName!!.text.toString()
            val phone = etPhone!!.text.toString()
            val address = etAddress!!.text.toString()
            if ("" == nickName) { //昵称不能为空
                Toast.makeText(mActivity, "昵称不能为空", Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }
            if ("" == phone) { //手机号不能为空
                Toast.makeText(mActivity, "手机号不能为空", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            if (phone.length != 11) { //手机号格式错误
                Toast.makeText(mActivity, "手机号格式错误", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            if ("" == address) { //简介不能为空
                Toast.makeText(mActivity, "简介不能为空", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            val sex = if (rgSex!!.checkedRadioButtonId == R.id.rb_man) "男" else "女" //性别
            db.execSQL("update user set name = ?,phone=?,address=?,sex=? where id = ?", arrayOf<Any>(nickName, phone, address, sex, mUser!!.id))
            Toast.makeText(this@PersonActivity, "更新成功", Toast.LENGTH_SHORT).show()
            db.close()
            finish()
        })
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