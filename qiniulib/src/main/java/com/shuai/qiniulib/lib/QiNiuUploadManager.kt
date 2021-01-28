package com.shuai.qiniulib.lib

import android.os.Environment
import com.qiniu.android.storage.FileRecorder
import com.qiniu.android.storage.Recorder
import com.qiniu.android.storage.UploadManager
import java.io.File
import java.io.IOException

/**
 * 七牛云UploadManager管理类，以单例实现
 */
class QiNiuUploadManager private constructor() {

    companion object {
        private const val BLOCK_FOLDER_NAME = "qi_niu/image_chunk"
        private val mBlockDirPath: String
            get() {
                val file = Environment.getExternalStoragePublicDirectory(BLOCK_FOLDER_NAME)
                if (!file.exists() || !file.isDirectory) {
                    file.mkdirs()
                }
                return file.absolutePath
            }

        @JvmStatic
        val INSTANCE: UploadManager = UploadManager(FileRecorder(mBlockDirPath));
    }

}

