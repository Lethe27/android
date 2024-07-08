package com.example.huangjinyuxuexiqiangguo.bean

import java.io.Serializable

//用户
class User(//用户ID
        var id: Int, //账号
        var account: String, //密码
        var password: String, //昵称
        var name: String, //性别
        var sex: String, //手机号
        var phone: String, //简介
        var address: String, //头像
        var photo:String,//存储地址
        var score: String) : Serializable