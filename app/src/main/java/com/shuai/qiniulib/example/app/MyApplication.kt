package com.shuai.qiniulib.example.app

import android.app.Application
import android.os.Handler
import android.os.Process
import com.shuai.qiniulib.QiNiuConfig

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this

        initQiNiu()
    }

    private fun initQiNiu() {
        QiNiuConfig.init(Constant.accessKey, Constant.secretKey)
    }

    companion object {
        var INSTANCE: MyApplication? = null
            private set
    }
}