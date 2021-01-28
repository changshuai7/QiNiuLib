package com.shuai.qiniulib.example.app

import android.os.Process

/**
 * 和线程相关的工具类
 *
 * @author changshuai
 */
object ThreadUtils {
    /**
     * 把Runnable方法提交到主线程执行
     *
     * @param runnable
     */
    fun runOnUiThread(runnable: Runnable) {
        if (Process.myTid() == MyApplication.mainTid) {
            runnable.run()
        } else {
            MyApplication.handler?.post(runnable) //运行一个handler
        }
    }
}