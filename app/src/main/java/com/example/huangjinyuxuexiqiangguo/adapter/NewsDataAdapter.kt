package com.example.huangjinyuxuexiqiangguo.adapter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.example.huangjinyuxuexiqiangguo.bean.NewsDataBean
import android.view.ViewGroup
import android.view.LayoutInflater
import com.example.huangjinyuxuexiqiangguo.R
import com.bumptech.glide.Glide
import android.content.Intent
import android.view.View
import android.widget.ImageView
import com.example.huangjinyuxuexiqiangguo.activity.NewsDataDetailActivity
import android.widget.TextView
import java.util.ArrayList
class NewsDataAdapter : RecyclerView.Adapter<NewsDataAdapter.ViewHolder>() {
    private val list: MutableList<NewsDataBean> = ArrayList()
    private var mActivity: Context? = null
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        mActivity = viewGroup.context
        val view = LayoutInflater.from(mActivity).inflate(R.layout.item_rv_new_data_list, viewGroup, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        val newsDataBean = list[i]
        if (newsDataBean != null) {
            viewHolder.title.text = newsDataBean.title
            viewHolder.author_name.text = newsDataBean.src
            viewHolder.date.text = newsDataBean.time
            Glide.with(mActivity!!).load(newsDataBean.pic).into(viewHolder.thumbnail_pic_s)
            viewHolder.itemView.setOnClickListener {
                val intent = Intent(mActivity, NewsDataDetailActivity::class.java)
                intent.putExtra("url", newsDataBean.url)
                mActivity!!.startActivity(intent)
            }
        }
    }
    override fun getItemCount(): Int {
        return list.size
    }
    fun addItem(listAdd: List<NewsDataBean>?) {
        //如果是加载第一页，需要先清空数据列表
        list.clear()
        if (listAdd != null) {
            //添加数据
            list.addAll(listAdd)
        }
        //通知RecyclerView进行改变--整体
        notifyDataSetChanged()
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView
        val author_name: TextView
        val date: TextView
        val thumbnail_pic_s: ImageView
        init {
            title = itemView.findViewById(R.id.title)
            author_name = itemView.findViewById(R.id.author_name)
            date = itemView.findViewById(R.id.date)
            thumbnail_pic_s = itemView.findViewById(R.id.thumbnail_pic_s)
        }
    }
}