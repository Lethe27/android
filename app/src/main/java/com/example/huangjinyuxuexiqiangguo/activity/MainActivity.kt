package com.example.huangjinyuxuexiqiangguo.activity

import com.example.huangjinyuxuexiqiangguo.util.SPUtils.remove
import com.example.huangjinyuxuexiqiangguo.util.GlideEngine.Companion.createGlideEngine
import com.example.huangjinyuxuexiqiangguo.fragment.NewsFragment.Companion.newInstance
import android.app.Activity
import android.app.Fragment
import android.content.Context
import com.example.huangjinyuxuexiqiangguo.util.MySqliteOpenHelper
import com.bumptech.glide.request.RequestOptions
import com.example.huangjinyuxuexiqiangguo.util.NetworkReceiver
import android.os.Bundle
import com.example.huangjinyuxuexiqiangguo.R
import com.example.huangjinyuxuexiqiangguo.MyApplication
import android.text.TextWatcher
import android.text.Editable
import com.example.huangjinyuxuexiqiangguo.fragment.NewsFragment
import androidx.drawerlayout.widget.DrawerLayout
import android.view.Gravity
import android.content.Intent
import com.bumptech.glide.Glide
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.listener.OnResultCallbackListener
import com.luck.picture.lib.entity.LocalMedia
import android.text.TextUtils
import android.os.Build
import com.luck.picture.lib.tools.PictureFileUtils
import android.content.IntentFilter
import android.content.res.Resources
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import com.example.huangjinyuxuexiqiangguo.bean.User
import com.example.huangjinyuxuexiqiangguo.fragment.UserFragment
import com.example.huangjinyuxuexiqiangguo.util.SPUtils
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.viewpump.ViewPumpContextWrapper

// 主页面
class MainActivity : Activity() {
    private var myActivity: Activity? = null
    private var llContent: LinearLayout? = null
    private var rbNews: RadioButton? = null
    private var rbUser: RadioButton? = null
    private var rbCenter: RadioButton? = null
    private var rbTv: RadioButton? = null
    private var rbDianTai: RadioButton? = null
    var helper: MySqliteOpenHelper? = null
    private val mActivity: Activity? = null
    private var ivPhoto: ImageView? = null
    private var tvNickName: TextView? = null
    private var llPerson: LinearLayout? = null
    private var llSecurity: LinearLayout? = null
    private var llVideo: LinearLayout? = null
    private var btnLogout: Button? = null
    private var imagePath = ""
    private var mUser: User? = null
    private val headerRO = RequestOptions().circleCrop() //圆角变换
    private var mNetReceiver: NetworkReceiver? = null
    private val fragments = arrayOf<Fragment?>(null, null, null, null, null, null) //存放Fragment
    private var mSearchEdit: EditText? = null
    private var mSearchButton: ImageView? = null
    private var currentIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myActivity = this
        setContentView(R.layout.activity_main)
        MyApplication.Instance!!.mainActivity = myActivity
        llContent = findViewById<View>(R.id.ll_main_content) as LinearLayout
        rbNews = findViewById<View>(R.id.rb_main_news) as RadioButton
        rbUser = findViewById<View>(R.id.rb_main_user) as RadioButton
        rbCenter = findViewById<View>(R.id.rb_main_center) as RadioButton
        rbTv = findViewById<View>(R.id.rb_main_tv) as RadioButton
        rbDianTai = findViewById<View>(R.id.rb_main_diantai) as RadioButton
        mSearchEdit = findViewById<View>(R.id.search_edit) as EditText
        mSearchButton = findViewById<View>(R.id.search) as ImageView
        mSearchEdit!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (mSearchEdit!!.text.toString().isEmpty()) {
                    (fragments[currentIndex] as NewsFragment?)!!.filter("")
                }
            }
        })
        mSearchButton!!.setOnClickListener { (fragments[currentIndex] as NewsFragment?)!!.filter(mSearchEdit!!.text.toString()) }
        findViewById<View>(R.id.mine).setOnClickListener { (findViewById<View>(R.id.drawerLayout) as DrawerLayout).openDrawer(Gravity.LEFT) }
        initView()
        setViewListener()
        initNetworkReceiver()
        initFont()
        initUser()
    }
    private fun initUser() {
        helper = MySqliteOpenHelper(this)
        ivPhoto = findViewById(R.id.iv_photo)
        tvNickName = findViewById(R.id.tv_nickName)
        llPerson = findViewById(R.id.person)
        llSecurity = findViewById(R.id.security)
        llVideo = findViewById(R.id.video)
        btnLogout = findViewById(R.id.logout)
        findViewById<View>(R.id.set).setOnClickListener { startActivity(Intent(this@MainActivity, SetActivity::class.java)) }
        findViewById<View>(R.id.about).setOnClickListener { startActivity(Intent(this@MainActivity, AboutUsActivity::class.java)) }
        initDataUser()
        initUserView()
    }
    private fun initDataUser() {
        val userId = SPUtils[this, SPUtils.USER_ID, 0] as Int?
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
                mUser = User(dbId, dbAccount, dbPassword, dbName, dbSex, dbPhone, dbAddress, dbPhoto,score)
            }
        }
        db.close()
        tvNickName!!.text = mUser!!.name+ " 积分 : ${if(mUser!!.score == null) "0" else mUser!!.score}"
        Glide.with(this)
                .load(mUser!!.photo)
                .apply(headerRO.error(if ("男" == mUser!!.sex) R.drawable.ic_default_man else R.drawable.ic_default_woman))
                .into(ivPhoto!!)
    }
    private fun initUserView() {
        //从相册中选择头像
        ivPhoto!!.setOnClickListener { selectClick() }
        //个人信息
        llPerson!!.setOnClickListener { //跳转页面
            val intent = Intent(this@MainActivity, PersonActivity::class.java)
            startActivity(intent)
        }
        //账号安全
        llSecurity!!.setOnClickListener { //跳转页面
            val intent = Intent(this@MainActivity, PasswordActivity::class.java)
            startActivity(intent)
        }
        //视频介绍
        llVideo!!.setOnClickListener { //跳转页面
            val intent = Intent(this@MainActivity, VideoActivity::class.java)
            startActivity(intent)
        }
        //退出登录
        btnLogout!!.setOnClickListener {
            remove(this@MainActivity, SPUtils.USER_ID)
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }
    }
    // 选择图片
    private fun selectClick() {
        val db = helper!!.writableDatabase
        PictureSelector.create(mActivity)
                .openGallery(PictureMimeType.ofAll())
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
                                    PictureFileUtils.getPath(mActivity, Uri.parse(media.path))
                                } else {
                                    media.path
                                }
                            }
                            imagePath = path
                        }
                        Glide.with(mActivity!!)
                                .load(imagePath)
                                .apply(headerRO.error(if ("男" == mUser!!.sex) R.drawable.ic_default_man else R.drawable.ic_default_woman))
                                .into(ivPhoto!!)
                        db.execSQL("update user set photo = ? where id = ?", arrayOf<Any>(imagePath, mUser!!.id))
                        Toast.makeText(mActivity, "更新成功", Toast.LENGTH_SHORT).show()
                    }
                    override fun onCancel() {
                        // onCancel Callback
                    }
                })
    }
    private fun initNetworkReceiver() {
        mNetReceiver = NetworkReceiver()
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(mNetReceiver, filter)
    }
    private fun setViewListener() {
        rbNews!!.setOnClickListener {
            replaceFragment(0)
        }
        rbUser!!.setOnClickListener {
            replaceFragment(1)
        }
        rbCenter!!.setOnClickListener {
            replaceFragment(2)
        }
        rbTv!!.setOnClickListener {
            replaceFragment(3)
        }
        rbDianTai!!.setOnClickListener {
            replaceFragment(4)
        }
    }
    private fun initView() {
        //设置导航栏图标样式
        val iconTv = resources.getDrawable(R.drawable.selector_main_rb_tv) //设置主页图标样式
        iconTv.setBounds(0, 0, 55, 55) //设置图标边距 大小
        rbTv!!.setCompoundDrawables(null, iconTv, null, null) //设置图标位置
        rbTv!!.compoundDrawablePadding = 5 //设置
        val iconNews = resources.getDrawable(R.drawable.selector_main_rb_home) //设置主页图标样式
        iconNews.setBounds(0, 0, 55, 55) //设置图标边距 大小
        rbNews!!.setCompoundDrawables(null, iconNews, null, null) //设置图标位置
        rbNews!!.compoundDrawablePadding = 5 //设置文字与图片的间距
        val iconUser = resources.getDrawable(R.drawable.selector_main_rb_bailing) //设置主页图标样式
        iconUser.setBounds(0, 0, 55, 55) //设置图标边距 大小
        rbUser!!.setCompoundDrawables(null, iconUser, null, null) //设置图标位置
        rbUser!!.compoundDrawablePadding = 5 //设置文字与图片的间距
        val iconCenter = resources.getDrawable(R.drawable.selector_main_rb_center) //设置主页图标样式
        iconCenter.setBounds(0, 0, 120, 120) //设置图标边距 大小
        rbCenter!!.setCompoundDrawables(null, iconCenter, null, null) //设置图标位置
        rbCenter!!.compoundDrawablePadding = 5 //设置
        val iconDianTai = resources.getDrawable(R.drawable.selector_main_rb_diantai) //设置主页图标样式
        iconDianTai.setBounds(0, 0, 55, 55) //设置图标边距 大小
        rbDianTai!!.setCompoundDrawables(null, iconDianTai, null, null) //设置图标位置
        rbDianTai!!.compoundDrawablePadding = 5 //设置
        replaceFragment(0)
        rbNews!!.isChecked = true
    }
    // 切换Fragment
    //参数：fragmentIndex 要显示Fragment的索引
    private fun replaceFragment(fragmentIndex: Int) {
        //在Activity中显示Fragment
        //1、获取Fragment管理器 FragmentManager
        val fragmentManager = this.fragmentManager
        //2、开启fragment事务
        val transaction = fragmentManager.beginTransaction()
        //如果需要显示的Fragment为null，就new。并添加到Fragment事务中
        if (fragments[fragmentIndex] == null) {
            currentIndex = fragmentIndex
            when (fragmentIndex) {
                0 -> fragments[fragmentIndex] = newInstance("0")
                1 -> fragments[fragmentIndex] = newInstance("1")
                2 -> fragments[fragmentIndex] = newInstance("2")
                3 -> fragments[fragmentIndex] = newInstance("3")
                4 -> fragments[fragmentIndex] = newInstance("4")
                5 -> fragments[fragmentIndex] = UserFragment()
            }
            //添加Fragment对象到Fragment事务中
            //参数：显示Fragment的容器的ID，Fragment对象
            transaction.add(R.id.ll_main_content, fragments[fragmentIndex])
        }
        //隐藏其他的Fragment
        for (i in fragments.indices) {
            if (fragmentIndex != i && fragments[i] != null) {
                //隐藏指定的Fragment
                transaction.hide(fragments[i])
            }
        }
        //4、显示Fragment
        transaction.show(fragments[fragmentIndex])
        //5、提交事务
        transaction.commit()
    }
    //双击退出
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit()
        }
        return false
    }
    private var time: Long = 0
    fun exit() {
        if (System.currentTimeMillis() - time > 2000) {
            time = System.currentTimeMillis()
            Toast.makeText(myActivity, "再点击一次退出应用程序", Toast.LENGTH_LONG).show()
        } else {
            finish()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mNetReceiver)
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
            Log.e("sizeType","sizeType is $sizeType")
            fontScale = if (sizeType == 3) {
                1.5f
            } else if (sizeType == 2) {
                1.0f
            } else {
                0.5f
            }
            return fontScale
        }
    override fun onResume() {
        super.onResume()
        try {
            initDataUser()
        }catch(e:Exception){
        }
    }
}