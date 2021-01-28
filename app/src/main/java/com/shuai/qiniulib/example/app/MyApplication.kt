package com.shuai.qiniulib.example.app

import android.app.Application
import android.os.Handler
import android.os.Process
import com.shuai.qiniulib.QiNiuConfig

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        mainTid = Process.myTid()
        handler = Handler()
        initQiNiu()
    }

    private fun initQiNiu() {
        QiNiuConfig.init(Constant.accessKey, Constant.secretKey)
    }

    companion object {
        //获取单例对象
        var instance: MyApplication? = null
            private set

        //获取主线程id
        var mainTid = 0
            private set

        //获取Handler
        var handler: Handler? = null
            private set
    }
}