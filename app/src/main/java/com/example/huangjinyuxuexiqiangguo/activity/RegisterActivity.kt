package com.example.huangjinyuxuexiqiangguo.activity

import android.content.Context
import com.example.huangjinyuxuexiqiangguo.util.GlideEngine.Companion.createGlideEngine
import androidx.appcompat.app.AppCompatActivity
import com.example.huangjinyuxuexiqiangguo.util.MySqliteOpenHelper
import com.bumptech.glide.request.RequestOptions
import android.os.Bundle
import com.example.huangjinyuxuexiqiangguo.R
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.entity.LocalMedia
import android.text.TextUtils
import android.os.Build
import android.view.View
import android.widget.*
import com.luck.picture.lib.tools.PictureFileUtils
import com.bumptech.glide.Glide
import com.example.huangjinyuxuexiqiangguo.MyApplication
import com.example.huangjinyuxuexiqiangguo.bean.User
import com.example.huangjinyuxuexiqiangguo.util.SPUtils
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.viewpump.ViewPumpContextWrapper

// 注册页面
class RegisterActivity : AppCompatActivity() {
    var helper: MySqliteOpenHelper? = null
    private var etAccount //账号
            : EditText? = null
    private var etNickName //昵称
            : EditText? = null
    private var etPhone //手机号
            : EditText? = null
    private var etAddress //简介
            : EditText? = null
    private var etPassword //密码
            : EditText? = null
    private var etPasswordSure //确认密码
            : EditText? = null
    private var ivPhoto //头像
            : ImageView? = null
    private var rgSex //性别
            : RadioGroup? = null
    private var tvLogin //登录
            : TextView? = null
    private var btnRegister //注册按钮
            : Button? = null
    private var imagePath = ""
    private val headerRO = RequestOptions().circleCrop() //圆角变换
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        helper = MySqliteOpenHelper(this)
        etAccount = findViewById(R.id.et_account) //获取账号
        etNickName = findViewById(R.id.et_nickName) //获取昵称
        etPhone = findViewById(R.id.et_phone) //获取手机号
        etAddress = findViewById(R.id.et_address) //获取简介
        etPassword = findViewById(R.id.et_password) //获取密码
        etPasswordSure = findViewById(R.id.et_password_sure) //获取确认密码
        ivPhoto = findViewById(R.id.iv_photo)
        rgSex = findViewById(R.id.rg_sex)
        tvLogin = findViewById<View>(R.id.tv_login) as TextView //登录
        btnRegister = findViewById<View>(R.id.btn_register) as Button //获取注册按钮
        //从相册中选择头像
        ivPhoto!!.setOnClickListener(View.OnClickListener { selectClick() })
        //返回登录
        tvLogin!!.setOnClickListener { //跳转到登录页面
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        //设置注册点击按钮
        btnRegister!!.setOnClickListener(View.OnClickListener { //获取请求参数
            val account = etAccount!!.getText().toString()
            val nickName = etNickName!!.getText().toString()
            val phone = etPhone!!.getText().toString()
            val address = etAddress!!.getText().toString()
            val password = etPassword!!.getText().toString()
            val passwordSure = etPasswordSure!!.getText().toString()
            if ("" == imagePath) { //请上传头像
                Toast.makeText(this@RegisterActivity, "请上传头像", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            if ("" == account) { //用户名不能为空
                Toast.makeText(this@RegisterActivity, "用户名不能为空", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            if ("" == nickName) { //昵称不能为空
                Toast.makeText(this@RegisterActivity, "昵称不能为空", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            if ("" == phone) { //手机号不能为空
                Toast.makeText(this@RegisterActivity, "手机号不能为空", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            if (phone.length != 11) { //手机号格式错误
                Toast.makeText(this@RegisterActivity, "手机号格式错误", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            if ("" == address) { //简介不能为空
                Toast.makeText(this@RegisterActivity, "简介不能为空", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            if ("" == password) { //密码为空
                Toast.makeText(this@RegisterActivity, "密码为空", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            if (password != passwordSure) { //密码不一致
                Toast.makeText(this@RegisterActivity, "密码不一致", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }
            val sex = if (rgSex!!.getCheckedRadioButtonId() == R.id.rb_man) "男" else "女" //性别
            var mUser: User? = null
            //通过账号查询是否存在
            val sql = "select * from user where account = ? "
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
            if (mUser == null) { //用户不存在 注册
                val insertSql = "insert into user(account, password,name,sex, phone,address,photo,score) values(?,?,?,?,?,?,?,?)"
                db.execSQL(insertSql, arrayOf<Any>(account, password, nickName, sex, phone, address, imagePath,"0"))
                Toast.makeText(this@RegisterActivity, "注册成功", Toast.LENGTH_SHORT).show()
                finish()
            } else { //用户存在
                Toast.makeText(this@RegisterActivity, "该账号已存在", Toast.LENGTH_SHORT).show()
            }
            db.close()
        })
        initFont()
    }
    //选择图片
    private fun selectClick() {
        PictureSelector.create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(createGlideEngine())
                .maxSelectNum(1)
                .forResult(object : OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: List<LocalMedia>) {
                        for (i in result.indices) {
                            // onResult Callback
                            val media = result[i]
                            var path: String
                            // 压缩过,或者裁剪同时压缩过,以最终压缩过图片为准
                            val compressPath = media.isCompressed || media.isCut && media.isCompressed
                            // 裁剪过
                            val isCutPath = media.isCut && !media.isCompressed
                            path = if (isCutPath) {
                                media.cutPath
                            } else if (compressPath) {
                                media.compressPath
                            } else if (!TextUtils.isEmpty(media.androidQToPath)) {
                                // AndroidQ特有path
                                media.androidQToPath
                            } else if (!TextUtils.isEmpty(media.realPath)) {
                                // 原图
                                media.realPath
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                    PictureFileUtils.getPath(this@RegisterActivity, Uri.parse(media.path))
                                } else {
                                    media.path
                                }
                            }
                            imagePath = path
                        }
                        Glide.with(this@RegisterActivity).load(imagePath).into(ivPhoto!!)
                    }
                    override fun onCancel() {
                        // onCancel Callback
                    }
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