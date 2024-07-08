package com.example.huangjinyuxuexiqiangguo.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method

// 数据持久化工具类
object SPUtils {
    const val IF_FIRST = "is_first" //是否第一次进来
    const val IS_ADMIN = "is_admin" //是否是管理员
    const val USER_ID = "USER_ID" //账号
    // 保存在手机里面的文件名
    private const val FILE_NAME = "share_data"
    //保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
    fun put(context: Context, key: String?, `object`: Any) {
        val sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE)
        val editor = sp.edit()
        if (`object` is String) {
            editor.putString(key, `object`)
        } else if (`object` is Int) {
            editor.putInt(key, `object`)
        } else if (`object` is Boolean) {
            editor.putBoolean(key, `object`)
        } else if (`object` is Float) {
            editor.putFloat(key, `object`)
        } else if (`object` is Long) {
            editor.putLong(key, `object`)
        } else {
            editor.putString(key, `object`.toString())
        }
        SharedPreferencesCompat.apply(editor)
    }
    //得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
    operator fun get(context: Context, key: String?, defaultObject: Any?): Any? {
        return try {
            val sp = context.getSharedPreferences(FILE_NAME,
                    Context.MODE_PRIVATE)
            if (defaultObject is String) {
                return sp.getString(key, defaultObject as String?)
            } else if (defaultObject is Int) {
                return sp.getInt(key, (defaultObject as Int?)!!)
            } else if (defaultObject is Boolean) {
                return sp.getBoolean(key, (defaultObject as Boolean?)!!)
            } else if (defaultObject is Float) {
                return sp.getFloat(key, (defaultObject as Float?)!!)
            } else if (defaultObject is Long) {
                return sp.getLong(key, (defaultObject as Long?)!!)
            }
            null
        } catch (e: Exception) {
            Log.e("sizeType is","sizeType is error $e")
            defaultObject
        }
    }
    //移除某个key值已经对应的值
    fun remove(context: Context, key: String?) {
        val sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.remove(key)
        SharedPreferencesCompat.apply(editor)
    }
    // 清除所有数据
    fun clear(context: Context) {
        val sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE)
        val editor = sp.edit()
        editor.clear()
        SharedPreferencesCompat.apply(editor)
    }
    //查询某个key是否已经存在
    fun contains(context: Context, key: String?): Boolean {
        val sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE)
        return sp.contains(key)
    }
    //返回所有的键值对
    fun getAll(context: Context): Map<String, *> {
        val sp = context.getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE)
        return sp.all
    }
    //创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
    private object SharedPreferencesCompat {
        private val sApplyMethod = findApplyMethod()

   // 反射查找apply的方法
   private fun findApplyMethod(): Method? {
            try {
                val clz: Class<*> = SharedPreferences.Editor::class.java
                return clz.getMethod("apply")
            } catch (e: NoSuchMethodException) {
            }
            return null
        }
        // 如果找到则使用apply执行，否则使用commit
        fun apply(editor: SharedPreferences.Editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor)
                    return
                }
            } catch (e: IllegalArgumentException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
            editor.commit()
        }
    }
}