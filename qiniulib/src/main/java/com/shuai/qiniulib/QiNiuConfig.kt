package com.shuai.qiniulib

import com.qiniu.util.Auth

class QiNiuConfig {

    companion object {

        var config: Config? = null
            private set

        @JvmStatic
        fun init(accessKey: String, secretKey: String): Config? {
            if (config == null) {
                synchronized(QiNiuConfig::class.java) {
                    if (config == null) {
                        config = Config(accessKey, secretKey)
                    }
                }
            }
            return config
        }
    }


    class Config(accessKey: String, secretKey: String) {
        val auth: Auth = Auth.create(accessKey, secretKey)

    }
}