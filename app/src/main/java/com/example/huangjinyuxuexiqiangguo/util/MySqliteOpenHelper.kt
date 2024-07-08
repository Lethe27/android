package com.example.huangjinyuxuexiqiangguo.util

import android.content.Context
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase

//数据库管理类,负责管理数据库的创建、升级工作
class MySqliteOpenHelper(private val context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    //在数据库首次创建的时候调用，创建表以及可以进行一些表数据的初始化
    override fun onCreate(db: SQLiteDatabase) {
        //创建表
        //_id为主键并且自增长一般命名为_id
        val newsSql = "create table news(id integer primary key autoincrement,typeId,title,img,content,issuer,date)"
        val userSql = "create table user(id integer primary key autoincrement,account, password,name,sex, phone,address,photo,score)"
        db.execSQL(newsSql)
        db.execSQL(userSql)
    }
    //数据库升级的时候回调该方法，在数据库版本号DB_VERSION升级的时候才会调用
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
    //数据库打开的时候回调该方法
    override fun onOpen(db: SQLiteDatabase) {
        super.onOpen(db)
    }
    companion object {
        //数据库名字
        const val DB_NAME = "news.db"
        //数据库版本
        const val DB_VERSION = 2
    }
}