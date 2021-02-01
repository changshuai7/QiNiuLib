package com.shuai.qiniulib.lib

import com.shuai.qiniulib.QiNiuUploader
import com.shuai.qiniulib.lib.QiNiuUploadCallback.QiNiuUploadInnerCallback
import com.shuai.qiniulib.lib.QiNiuUploadTokenLoader.QiNiuUploadTokenResult
import java.util.concurrent.CountDownLatch

/**
 * 单文件上传任务Runnable
 */
class QiNiuUploadFileTask(
        private val mFilePath: String,
        private val mKey: String,
        private val mUploader: QiNiuUploader,
        private val mCallback: QiNiuUploadCallback?) : Runnable, QiNiuUploadFileHandler {

    private var mQiNiuUploadCore: QiNiuUploadCore = QiNiuUploadCore.create()

    @Transient
    private var mDoneSignal: CountDownLatch? = null

    private val mRetryTimes = 3
    private var mRetryCount = 0 //重试次数统计

    override fun run() {
        mDoneSignal = CountDownLatch(1) //只允许单线程访问Task
        doUpload(false, mFilePath, mKey, mCallback)
        try {
            mDoneSignal?.await()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    fun doUpload(obtainTokenFromTokenLoader: Boolean, filePath: String, key: String, callback: QiNiuUploadCallback?) {
        QiNiuLog.d("doUpload执行->mRetryCount = $mRetryCount")

        /*1、获取Token*/
        obtainToken(obtainTokenFromTokenLoader, object : ObtainTokenCallback {

            override fun onSuccess(token: String?) {

                if (!QiNiuUtil.isStrNullOrEmpty(token)) {

                    mQiNiuUploadCore.upload(filePath, key, token as String, object : QiNiuUploadInnerCallback {

                        //进度回调
                        override fun onProgress(key: String?, percent: Double?) {
                            callback?.onProgress(key, percent)
                        }

                        //上传完成
                        override fun onComplete(key: String?, info: String?) {
                            mRetryCount = 0
                            callback?.onComplete(key, info)
                            releaseLatch() //上传完成，释放锁
                        }

                        //上传失败
                        override fun onError(key: String?, statusCode: Int, error: String?) {
                            //从TokenLoader重新获取Token以重试
                            mRetryCount++
                            if (mRetryCount < mRetryTimes && !QiNiuUtil.isStrNullOrEmpty(key)) {
                                doUpload(true, filePath, key as String, callback)
                            } else {
                                callback?.onError(key, statusCode, error)
                                releaseLatch() //上传失败（超过最大重试次数），释放锁
                            }
                        }

                        //上传取消
                        override fun onCancel(key: String?, statusCode: Int, error: String?) {
                            callback?.onCancel(key, statusCode, error)
                            releaseLatch() //上传取消，释放锁
                        }
                    })
                } else {
                    callback?.onError(key, QiNiuErrorCode.OBTAIN_TOKEN_EMPTY.code, QiNiuErrorCode.OBTAIN_TOKEN_EMPTY.message)
                    releaseLatch() //释放锁
                }
            }

            //获取Token失败
            override fun onError(error: String?) {
                callback?.onError(key, QiNiuErrorCode.OBTAIN_TOKEN_ERROR.code, QiNiuErrorCode.OBTAIN_TOKEN_ERROR.message + ":" + error)
                releaseLatch() //释放锁
            }
        })
    }

    /**
     * 获取Token
     *
     * @param obtainTokenFromTokenLoader 是否从TokenLoader中从获取Token，false的话，使用已经保存的token
     * @param callback                   获取结果
     */
    private fun obtainToken(obtainTokenFromTokenLoader: Boolean, callback: ObtainTokenCallback) {

        //当getTokenFromTokenLoader为true，或者token 为空的时候，则重新获取
        if (obtainTokenFromTokenLoader || QiNiuUtil.isStrNullOrEmpty(mUploader.token)) {
            mUploader.tokenLoader.getUploadToken(object : QiNiuUploadTokenResult {
                override fun onSuccess(token: String?) {
                    //更新QiNiuUploadFileHelper中的Token
                    mUploader.token = token
                    callback.onSuccess(token)
                }

                override fun onError(error: String?) {
                    callback.onError(error)
                }
            })
        } else {
            callback.onSuccess(mUploader.token)
        }
    }

    internal interface ObtainTokenCallback {
        fun onSuccess(token: String?)
        fun onError(error: String?)
    }

    override fun cancel() {
        mQiNiuUploadCore.cancel()//取消上传任务，会直接回调到onCancel()
    }

    private fun releaseLatch() {
        try {
            mDoneSignal?.countDown() //释放锁
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}