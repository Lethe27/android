
package com.example.huangjinyuxuexiqiangguo.bean

class NewsTitle {
    var code: String? = null
    var charge = false
    var msg: String? = null
    var result: Result? = null
    inner class Result {
        var status: String? = null
        var msg: String? = null
        var result: List<String>? = null
    }
}