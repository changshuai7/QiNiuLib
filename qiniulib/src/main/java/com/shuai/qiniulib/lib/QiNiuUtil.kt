package com.shuai.qiniulib.lib

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.text.TextUtils
import java.io.File

/**
 * 工具类
 */
class QiNiuUtil {

    companion object {

        @JvmStatic
        fun isStrNullOrEmpty(string: String?): Boolean {
            return string == null || string.trim { it <= ' ' }.isEmpty()
        }

        @JvmStatic
        fun isFileCanRead(filePath: String?): Boolean {
            if (TextUtils.isEmpty(filePath)) return false
            val file = File(filePath)
            return !(!file.isFile || !file.canRead())
        }

        @JvmStatic
        fun getFileName(filePath: String?): String? {
            if (TextUtils.isEmpty(filePath)) return null
            val file = File(filePath)
            return file.name
        }

        @JvmStatic
        fun isNetAvailable(context: Context): Boolean {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm is ConnectivityManager) {
                val info = cm.activeNetworkInfo
                if (info != null && info.isConnected) {
                    if (info.state == NetworkInfo.State.CONNECTED) {
                        return true
                    }
                }
            }
            return false
        }
    }

}


