package com.example.huangjinyuxuexiqiangguo.fragment

import com.example.huangjinyuxuexiqiangguo.util.OkHttpTool.Companion.httpGet
import android.app.Activity
import android.app.Fragment
import android.content.Context
import com.google.android.material.tabs.TabLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.huangjinyuxuexiqiangguo.adapter.NewsDataAdapter
import com.google.gson.GsonBuilder
import com.example.huangjinyuxuexiqiangguo.bean.NewsDataBean
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.huangjinyuxuexiqiangguo.R
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.example.huangjinyuxuexiqiangguo.util.OkHttpTool.ResponseCallback
import com.example.huangjinyuxuexiqiangguo.bean.NewsTitle
import org.json.JSONObject
import com.google.gson.reflect.TypeToken
import org.json.JSONException
import java.lang.Exception
import java.util.ArrayList
import java.util.HashMap

//新闻
class NewsFragment : Fragment() {
    lateinit var myActivity //上下文
            : Activity
    lateinit var tabTitle: TabLayout
    lateinit var type: String
    lateinit var rvNewsList: RecyclerView
    lateinit var mNewsAdapter: NewsDataAdapter
    private var dataType: String? = null
    private val gson = GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create()
    lateinit var titles: ArrayList<String>
    var newsDataBeanList: List<NewsDataBean> = ArrayList<NewsDataBean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val args = arguments
        if (args != null) {
            dataType = args.getString("type")
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        myActivity = context as Activity
    }
    override fun onCreateView(
        inflater: LayoutInflater?,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater!!.inflate(R.layout.fragment_news, container, false)
        tabTitle = view.findViewById(R.id.tab_title)
        rvNewsList = view.findViewById(R.id.rv_news_list)
        initView()
        return view
    }
    //初始化页面
    private fun initView() {
        loadTitle()
        //1.1、设置线性布局
        val layoutManager = LinearLayoutManager(myActivity)
        //1.2、设置为垂直排列，用setOrientation方法设置(默认为垂直布局)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        //1.3、设置recyclerView的布局管理器
        rvNewsList.layoutManager = layoutManager
        //2.1、初始化适配器
        mNewsAdapter = NewsDataAdapter()
        //2.2、设置recyclerView的适配器
        rvNewsList.adapter = mNewsAdapter
        tabTitle.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                type = titles[tab.position]
                loadData()
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
    private fun loadTitle() {
        titles = ArrayList()
        val url = "https://way.jd.com/jisuapi/channel"
        val map: MutableMap<String, Any> = HashMap()
        map["appkey"] = "59d2b7c28a660cad40772dc1e0bd2744"
        httpGet(url, map, object : ResponseCallback {
            override fun onResponse(isSuccess: Boolean, responseCode: Int, response: String?, exception: Exception?) {
                myActivity.runOnUiThread {
                    if (isSuccess && responseCode == 200) {
                        val newsTitle = gson.fromJson(response, NewsTitle::class.java)
                        if ("10000" == newsTitle.code) {
                            Log.e("onResponse", "onResponse titles size is " + newsTitle.result!!.result!!.size)
                            when (dataType) {
                                "0" -> {
                                    titles.add(newsTitle.result!!.result?.get(0) ?: "") //头条
                                    titles.add(newsTitle.result!!.result?.get(3) ?: "") //国际
                                    titles.add(newsTitle.result!!.result?.get(2) ?: "") //国内
                                }
                                "1" -> {
                                    titles.add(newsTitle.result!!.result?.get(12) ?:"") //股票
                                    titles.add(newsTitle.result!!.result?.get(5) ?: "") //财经
                                }
                                "2" -> {
                                    titles.add(newsTitle.result!!.result?.get(1) ?: "") //新闻
                                    titles.add(newsTitle.result!!.result?.get(4) ?: "") //政治
                                    titles.add(newsTitle.result!!.result?.get(8) ?: "") //军事
                                    titles.add(newsTitle.result!!.result?.get(9) ?: "") //教育
                                }
                                "3" -> {
                                    titles.add(newsTitle.result!!.result?.get(6) ?: "") //体育
                                    titles.add(newsTitle.result!!.result?.get(7) ?: "") //娱乐
                                    titles.add(newsTitle.result!!.result?.get(10) ?: "") //科技
                                    titles.add(newsTitle.result!!.result?.get(11) ?: "") //NBA
                                }
                                "4" -> {
                                    titles.add(newsTitle.result!!.result?.get(13) ?: "") //星座
                                    titles.add(newsTitle.result!!.result?.get(14) ?: "") //女性
                                    titles.add(newsTitle.result!!.result?.get(15) ?: "") //育儿
                                }
                            }
                            // titles.addAll(newsTitle.getResult().getResult());
                            tabTitle.tabMode = TabLayout.MODE_SCROLLABLE
                            //为TabLayout添加tab名称
                            for (i in titles.indices) {
                                when (titles.get(i)) {
                                    "财经" -> tabTitle.addTab(tabTitle.newTab().setText("金融"))
                                    "新闻" -> tabTitle.addTab(tabTitle.newTab().setText("时事"))
                                    "女性" -> tabTitle.addTab(tabTitle.newTab().setText("情感"))
                                    "育儿" -> tabTitle.addTab(tabTitle.newTab().setText("母婴"))
                                    else -> tabTitle.addTab(tabTitle.newTab().setText(titles.get(i)))
                                }
                            }
                            type = titles.get(0)
                            loadData()
                        }
                    }
                }
            }
        })
    }

    private fun loadData() {
        val url = "https://way.jd.com/jisuapi/get"
        val map: MutableMap<String, Any> = HashMap()
        map["channel"] = type
        map["num"] = "40"
        map["start"] = "0"
        map["appkey"] = "59d2b7c28a660cad40772dc1e0bd2744"
        httpGet(url, map, object : ResponseCallback {
            override fun onResponse(isSuccess: Boolean, responseCode: Int, response: String?, exception: Exception?) {
                myActivity.runOnUiThread {
                    if (isSuccess && responseCode == 200) {
                        try {
                            Log.d("响应数据：",response.toString())
                            val jsonObject = JSONObject(response)
                            val result = jsonObject.getString("result")
                            val jsonResult = JSONObject(result)
                            val result2 = jsonResult.getString("result")
                            val jsonResult2 = JSONObject(result2)
                            val list = jsonResult2.getString("list")
                            val type = object : TypeToken<List<NewsDataBean?>?>() {}.type //列表信息
                            newsDataBeanList = gson.fromJson(list, type)
                            if (newsDataBeanList.size > 0) {
                                mNewsAdapter.addItem(newsDataBeanList)
                            }
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        })
    }

    fun filter(name: String) {
        val dataList: MutableList<NewsDataBean> = ArrayList<NewsDataBean>()
        if (!name.isEmpty()) {
            if (newsDataBeanList.size == 0) {
                return
            }
            for (i in newsDataBeanList.indices) {
                if (newsDataBeanList[i].title!!.contains(name)) {
                    dataList.add(newsDataBeanList[i])
                }
            }
        } else {
            dataList.addAll(newsDataBeanList)
        }
        mNewsAdapter = NewsDataAdapter()
        //2.3、设置recyclerView的适配器
        rvNewsList.adapter = mNewsAdapter
        if (dataList.size > 0) {
            mNewsAdapter.addItem(dataList.toList())
        }
    }
    companion object {
        fun newInstance(type: String?): NewsFragment {
            val newFragment = NewsFragment()
            val bundle = Bundle()
            bundle.putString("type", type)
            newFragment.arguments = bundle
            return newFragment
        }
    }
}