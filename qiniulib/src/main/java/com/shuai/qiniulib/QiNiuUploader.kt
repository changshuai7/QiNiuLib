package com.shuai.qiniulib

import com.shuai.qiniulib.lib.*
import java.lang.RuntimeException
import java.util.concurrent.Executors

class QiNiuUploader private constructor(
        val tokenLoader: QiNiuUploadTokenLoader,
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
        var mKey: String? = key
        if (QiNiuUtil.isStrNullOrEmpty(key)) {
            mKey = QiNiuUtil.getFileName(filePath)
        }
        callback?.onStart(mKey)

        if (!QiNiuUtil.isFileCanRead(filePath)) {
            callback?.onError(key, QiNiuErrorCode.UPLOAD_CANT_READ_FILE_ERROR.code, QiNiuErrorCode.UPLOAD_CANT_READ_FILE_ERROR.message)
            return object : QiNiuUploadFileHandler {
                override fun cancel() {}
            }
        }
        if (QiNiuUtil.isStrNullOrEmpty(mKey)) {
            callback?.onError(key, QiNiuErrorCode.UPLOAD_FILE_KEY_EMPTY_ERROR.code, QiNiuErrorCode.UPLOAD_FILE_KEY_EMPTY_ERROR.message)
            return object : QiNiuUploadFileHandler {
                override fun cancel() {}
            }
        }
        //到此处，filePath、mKey一定不会为空的
        val task = QiNiuUploadFileTask(filePath as String, mKey as String, this, callback)
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

        //设置TokenLoader
        fun setTokenLoader(mTokenLoader: QiNiuUploadTokenLoader): Build {
            this.mTokenLoader = mTokenLoader
            return this
        }

        //构建QiNiuUploader
        fun build(): QiNiuUploader {
            if (mTokenLoader == null) {
                throw RuntimeException("QiNiuUploadTokenLoader不可以为空");
            }
            return QiNiuUploader(mTokenLoader as QiNiuUploadTokenLoader, null)
        }
    }

}