package com.example.huangjinyuxuexiqiangguo.activity

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.net.http.SslError
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.huangjinyuxuexiqiangguo.MyApplication
import com.example.huangjinyuxuexiqiangguo.R
import com.example.huangjinyuxuexiqiangguo.util.MySqliteOpenHelper
import com.example.huangjinyuxuexiqiangguo.util.SPUtils
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import io.github.inflationx.viewpump.ViewPumpContextWrapper

//具体新闻页面
class NewsDataDetailActivity : AppCompatActivity() {
    private var myActivity: Activity? = null
    private var webView: WebView? = null
    private var url: String? = null
    private var isAlive = false
    private var timeTask: TimeTask? = null
    var helper: MySqliteOpenHelper? = null
    private var mHandler: Handler = Handler {
        val db = helper!!.writableDatabase
        val userId = SPUtils[this@NewsDataDetailActivity, SPUtils.USER_ID, 0] as Int?
        val sql = "select * from user where id = ?"
        val cursor = db.rawQuery(sql, arrayOf(userId.toString()))
        var score = "0"
        if (cursor != null && cursor.columnCount > 0) {
            while (cursor.moveToNext()) {
                score = cursor.getString(8)
            }
        }
        try {
            val toInt = score.toInt()
            db.execSQL("update user set score = ? where id = ?", arrayOf<Any?>((toInt + 1).toString(), userId))
        }catch (e:Exception){
        }
         db.close()
        false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myActivity = this
        setContentView(R.layout.activity_news_data_detail)
        url = intent.getStringExtra("url")
        webView = findViewById(R.id.webView)
        initData()
        initFont()
        helper = MySqliteOpenHelper(this@NewsDataDetailActivity)
        isAlive = true;
        timeTask = TimeTask()
        timeTask?.execute()
    }
    inner class TimeTask : AsyncTask<Void?, Int?, Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {
            while (isAlive) {
                Thread.sleep(1000)
                publishProgress()
            }
            return true
        }
        override fun onProgressUpdate(vararg values: Int?) {
            mHandler.sendEmptyMessage(0);
            super.onProgressUpdate(*values)
        }
    }
    private fun initData() {
        val webSettings = webView!!.settings
        //如果访问的页面中要与Javascript交互，则webview必须设置支持Javascript
        webSettings.javaScriptEnabled = true
        // 在onStop和onResume里分别把 setJavaScriptEnabled()给设置成 false和true即可
        //设置自适应屏幕，两者合用
        webSettings.useWideViewPort = true //将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小
        //缩放操作
        webSettings.setSupportZoom(true) //支持缩放，默认为true。是下面那个的前提。
        webSettings.builtInZoomControls = true //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.displayZoomControls = false //隐藏原生的缩放控件
        //其他细节操作
        webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK //关闭webview中缓存
        webSettings.allowFileAccess = true //设置可以访问文件
        webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
        webSettings.loadsImagesAutomatically = true //支持自动加载图片
        webSettings.defaultTextEncodingName = "utf-8" //设置编码格式
        webSettings.domStorageEnabled = true
        webView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(wv: WebView, url: String): Boolean {
                wv.loadUrl(url)
                return true
            }
            override fun onReceivedSslError(
                view: WebView,
                handler: SslErrorHandler,
                error: SslError
            ) {
                handler.proceed()
            }
        }
        webView!!.loadUrl(url!!)
    }
    //返回
    fun back(view: View?) {
        finish()
    }
    private fun initFont() {
        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath(SPUtils[this, "fonts", "fonts/Roboto-Bold.ttf"] as String?)
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                )
                .build()
        )
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
    override fun onDestroy() {
        isAlive = false
        super.onDestroy()
    }
}