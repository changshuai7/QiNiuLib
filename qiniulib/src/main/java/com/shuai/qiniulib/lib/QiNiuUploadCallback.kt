package com.shuai.qiniulib.lib

/**
 * 七牛云上传回调接口
 */
interface QiNiuUploadCallback {
    fun onStart(key: String?)
    fun onProgress(key: String?, percent: Double)
    fun onComplete(key: String?, info: String?)
    fun onError(key: String?, statusCode: Int, error: String?)
    fun onCancel(key: String?, statusCode: Int, error: String?)

    // QiNiuUploader回调接口，此回调仅限内部使用
    interface QiNiuUploadInnerCallback {
        fun onProgress(key: String?, percent: Double)
        fun onComplete(key: String?, info: String?)
        fun onError(key: String?, statusCode: Int, error: String?)
        fun onCancel(key: String?, statusCode: Int, error: String?)
    }
}