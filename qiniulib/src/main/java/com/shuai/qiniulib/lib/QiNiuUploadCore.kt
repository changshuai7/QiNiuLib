package com.shuai.qiniulib.lib

import com.qiniu.android.storage.UpCompletionHandler
import com.qiniu.android.storage.UploadOptions
import com.shuai.qiniulib.lib.QiNiuUploadCallback.QiNiuUploadInnerCallback

/**
 * 七牛云上传器，利用QiNiu的SDK,执行上传功能核心代码
 */
class QiNiuUploadCore private constructor() {

    @Volatile
    var isUploadCancelled = false // 任务是否已经取消
        private set

    fun upload(filePath: String?, key: String?, token: String?, callback: QiNiuUploadInnerCallback?) {
        QiNiuLog.d("七牛SDK - 开始执行upload")
        //上传进度 配置
        val options = UploadOptions(null, null, true, { k, percent -> callback?.onProgress(k, percent) }) {
            // 内部代码会检测 UpCancellationSignal##isCancelled() 的返回值
            // 当其返回 true 时，将停止上传。
            // 所以可外部维护一个变量 mUploadCancelled，当点击取消按钮时，设置 mUploadCancelled = true;
            isUploadCancelled
        }

        //上传结束 配置
        val upCompletionHandler = UpCompletionHandler { k, info, jsonData ->
            when {
                info.isOK -> {
                    QiNiuLog.d("七牛SDK - 返回 - isOK")
                    //String fileKey = jsonData.optString("key");
                    //String fileHash = jsonData.optString("hash");
                    callback?.onComplete(k, jsonData?.toString() ?: "")
                }
                info.isCancelled -> {
                    QiNiuLog.d("七牛SDK - 返回 - isCancelled")

                    callback?.onCancel(k, info.statusCode, info.error)
                }
                else -> {
                    QiNiuLog.d("七牛SDK - 返回 - isElse")

                    callback?.onError(k, info.statusCode, info.error)
                }
            }
        }

        QiNiuUploadManager.INSTANCE.put(filePath, key, token, upCompletionHandler, options)
    }

    // 点击取消按钮，让 UpCancellationSignal##isCancelled() 方法返回 true，以停止上传
    fun cancel() {
        isUploadCancelled = true
    }

    companion object {
        fun create(): QiNiuUploadCore {
            return QiNiuUploadCore()
        }
    }
}