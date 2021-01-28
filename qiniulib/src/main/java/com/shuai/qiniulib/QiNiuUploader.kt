package com.shuai.qiniulib

import com.shuai.qiniulib.lib.*
import java.util.concurrent.Executors

class QiNiuUploader private constructor(
        val tokenLoader: QiNiuUploadTokenLoader?,
        //保存Token，下次无特殊情况，不需要重新通过QiNiuUploadTokenLoader获取(仅存于调用者的内存中)
        @field:Transient var token: String?) {

    //管理线程池
    private val mUploadFileExecutor = Executors.newFixedThreadPool(3)

    companion object {
        @JvmStatic
        fun create(): Build {
            return Build()
        }
    }

    fun upload(filePath: String?, callback: QiNiuUploadCallback?): QiNiuUploadFileHandler {
        return upload(filePath, null, callback)
    }

    /**
     * 执行上传
     *
     * @param filePath 文件路径
     * @param key      文件key（名称）
     * @param callback 上传回调
     * @return
     */
    fun upload(filePath: String?, key: String?, callback: QiNiuUploadCallback?): QiNiuUploadFileHandler {
        var k = key
        if (QiNiuUtil.isStrNullOrEmpty(key)) {
            k = QiNiuUtil.getFileName(filePath)
        }
        callback?.onStart(k)
        val task = QiNiuUploadFileTask(filePath, k, this, callback)
        //mUploadFileExecutor.execute(task);
        val submit = mUploadFileExecutor?.submit(task)
        return object : QiNiuUploadFileHandler {
            override fun cancel() {
                try {
                    task.cancel() //取消正在上传的任务
                    submit?.cancel(true) //取消pending中的任务
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    /////////////////////////////// Build 用于传递相关配置 ///////////////////////////////
    class Build {
        private var mTokenLoader: QiNiuUploadTokenLoader? = null
        fun setTokenLoader(mTokenLoader: QiNiuUploadTokenLoader?): Build {
            this.mTokenLoader = mTokenLoader
            return this
        }

        fun build(): QiNiuUploader {
            return QiNiuUploader(mTokenLoader, null)
        }
    }


}