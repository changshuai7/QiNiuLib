package com.shuai.qiniulib.lib

/**
 * 上传Token生成器接口
 */
interface QiNiuUploadTokenLoader {
    fun getUploadToken(result: QiNiuUploadTokenResult)
    interface QiNiuUploadTokenResult {
        fun onSuccess(token: String?)
        fun onError(error: String?)
    }
}