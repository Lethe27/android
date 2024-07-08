package com.example.huangjinyuxuexiqiangguo.util

import okhttp3.HttpUrl
import android.graphics.Bitmap
import android.util.Log
import com.zhy.http.okhttp.OkHttpUtils
import com.zhy.http.okhttp.callback.BitmapCallback
import com.squareup.okhttp.Request
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.create
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder
import java.nio.charset.Charset
import java.util.ArrayList
import java.util.HashMap
import java.util.concurrent.TimeUnit

//便于使用okhttp3的工具类
class OkHttpTool {
    //获取图片  返回
    fun getImage(url: String?) {
        var response: Bitmap
        OkHttpUtils
                .get() //
                .url(url) //
                .tag(this) //
                .build() //
                .connTimeOut(20000) //链接超时
                .readTimeOut(20000) //读取超时
                .writeTimeOut(20000) //写入超时
                .execute(object : BitmapCallback() {
                    override fun onError(request: Request, e: Exception) {}
                    override fun onResponse(response: Bitmap) {}
                })
    }
    //回调接口
    interface ResponseCallback {
        fun onResponse(isSuccess: Boolean, responseCode: Int, response: String?, exception: Exception?)
    }
    companion object {
        //日志标志
        private const val TAG = "OkHttpTool"
        //OkHttpClient类
        private var myOkHttpClient: OkHttpClient? = null
        init {
            //cookie处理--让服务端记住app
            //这里是设置cookie的,但是并没有做持久化处理;只是把cookie保存在内存中
            val cookieJar: CookieJar = object : CookieJar {
                private val cookieStore = HashMap<String, List<Cookie>>()
                //保存cookie
                override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
                    cookieStore[url.host] = cookies
                }
                //获取cookie
                override fun loadForRequest(url: HttpUrl): List<Cookie> {
                    val cookies = cookieStore[url.host]
                    return cookies ?: ArrayList()
                }
            }
            //创建OkHttpClient
            myOkHttpClient = OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS) //连接超时
                    .writeTimeout(20, TimeUnit.SECONDS) //写入超时
                    .readTimeout(20, TimeUnit.SECONDS) //读取超时
                    .cookieJar(cookieJar)
                    .build()
        }
        // 对外方法
        // Get 请求
        fun httpGet(url: String, parameters: MutableMap<String, Any>, responseCallback: ResponseCallback) {
            val request = createGetRequest(url, parameters)
            doRequest(request, responseCallback)
        }
        //POST 请求
        //参数：请求地址、请求参数、请求回调
        fun httpPost(url: String, parameters: Map<String, Any>?, responseCallback: ResponseCallback) {
            val request = createPostRequest(url, parameters)
            doRequest(request, responseCallback)
        }
        // POST 请求 JSON格式参数
        //参数：请求地址、请求参数、请求回调
        fun httpPostJson(url: String, json: String, responseCallback: ResponseCallback) {
            val request = createPostRequestJson(url, json)
            doRequest(request, responseCallback)
        }
        // 私有方法
        //构建请求
        //构建 Get请求
        //参数：请求地址、请求参数、请求回调
        private fun createGetRequest(url: String, parameters: Map<String, Any>): okhttp3.Request {
            val urlBuilder = StringBuilder()
            urlBuilder.append(url)
            if (url.indexOf('?') <= -1) {
                //未拼接参数
                urlBuilder.append("?")
            }
            for ((key, value) in parameters) {
                urlBuilder.append("&")
                urlBuilder.append(key)
                urlBuilder.append("=")
                urlBuilder.append(value.toString())
            }
            return baseRequest.url(urlBuilder.toString()).build()
        }
        //构建 POST 请求
        //参数：请求地址、请求参数、请求回调
        private fun createPostRequest(url: String, parameters: Map<String, Any>?): okhttp3.Request {
            val builder = FormBody.Builder(Charset.forName("UTF-8"))
            if (parameters != null) {
                for ((key, value) in parameters) {
                    builder.add(key, value.toString())
                }
            }
            val formBody: FormBody = builder.build()
            return baseRequest.url(url).post(formBody).build()
        }
        //构建 POST JSON格式参数请求
        //参数：请求地址、请求参数、请求回调
        private fun createPostRequestJson(url: String, json: String): okhttp3.Request {
            val mediaType: MediaType? = "application/json;charset=utf-8".toMediaTypeOrNull()
            val body: RequestBody = create( mediaType,json)
            return baseRequest.url(url).post(body).build()
        }
        //实际进行请求的方法
        private fun doRequest(request: okhttp3.Request, responseCallback: ResponseCallback) {
            //使用okhttp3的异步请求
            myOkHttpClient!!.newCall(request).enqueue(object : Callback {
                //失败回调
                override fun onFailure(call: Call, e: IOException) {
                    //回调
                    responseCallback.onResponse(false, -1, null, e)
                    //用于输出错误调试信息
                    if (e.message != null) {
                        Log.e(TAG, e.message!!)
                    }
                }
                //成功回调
                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    val responseCode = response.code //获取响应码
                    val responseBody = response.body //获取 ResponseBody
                    if (response.isSuccessful && responseBody != null) {
                        val strResponse = responseBody.string()
                        //回调
                        responseCallback.onResponse(true, responseCode, strResponse, null)
                    } else {
                        //回调
                        responseCallback.onResponse(false, responseCode, null, null)
                    }
                }
            })
        }
        //获取请求 指定client为Android
        private val baseRequest: okhttp3.Request.Builder
            private get() {
                val builder = okhttp3.Request.Builder()
                builder.addHeader("client", "Android")
                return builder
            }
    }
}