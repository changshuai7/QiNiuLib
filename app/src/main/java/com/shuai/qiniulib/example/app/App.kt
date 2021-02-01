package com.shuai.qiniulib.example.app

import android.app.Application
import androidx.multidex.MultiDex
import com.shuai.qiniulib.QiNiuConfig

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        MultiDex.install(this);
        INSTANCE = this

        initQiNiu()
    }

    private fun initQiNiu() {
        QiNiuConfig.init(Constant.accessKey, Constant.secretKey)
    }

    companion object {
        var INSTANCE: App? = null
            private set
    }
}