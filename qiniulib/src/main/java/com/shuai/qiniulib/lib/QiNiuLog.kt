package com.shuai.qiniulib.lib

import android.util.Log

/**
 * 日志简易管理类
 */
class QiNiuLog {

    companion object {
        private var isLogEnable = true
        private var defaultTag = "QiNiuLib"

        fun debug(logTag: String = defaultTag, isEnable: Boolean = isLogEnable) {
            defaultTag = logTag
            isLogEnable = isEnable
        }

        fun v(msg: String) {
            v(defaultTag, msg)
        }

        fun v(tag: String, msg: String) {
            if (isLogEnable) Log.v(tag, msg)
        }

        fun d(msg: String) {
            d(defaultTag, msg)
        }

        fun d(tag: String, msg: String) {
            if (isLogEnable) Log.d(tag, msg)
        }

        fun i(msg: String) {
            i(defaultTag, msg)
        }

        fun i(tag: String, msg: String) {
            if (isLogEnable) Log.i(tag, msg)
        }

        fun w(msg: String) {
            w(defaultTag, msg)
        }

        fun w(tag: String, msg: String) {
            if (isLogEnable) Log.w(tag, msg)
        }

        fun e(msg: String) {
            e(defaultTag, msg)
        }

        fun e(tag: String, msg: String) {
            if (isLogEnable) Log.e(tag, msg)
        }

        fun printStackTrace(t: Throwable) {
            if (isLogEnable) t.printStackTrace()
        }

    }

}
