package com.shuai.qiniulib.lib;

import android.os.Environment;

import com.qiniu.android.storage.FileRecorder;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UploadManager;

import java.io.File;
import java.io.IOException;

/**
 * 七牛云UploadManager管理类，以单例实现
 */
public class QiNiuUploadManager {

    private static UploadManager mInstance;

    public static UploadManager getUploadManagerInstance() {
        if (mInstance == null) {
            synchronized (QiNiuUploadManager.class) {
                if (mInstance == null) {
                    mInstance = createUploadManager();
                }
            }
        }
        return mInstance;
    }

    //断点记录文件保存的文件夹位置
    private static final String BLOCK_FOLDER_NAME = "qi_niu/image_chunk";

    private static UploadManager createUploadManager() {
        File blockDirPath = Environment.getExternalStoragePublicDirectory(BLOCK_FOLDER_NAME);
        if (!blockDirPath.exists() || !blockDirPath.isDirectory()) {
            boolean mkdirs = blockDirPath.mkdirs();
        }
        try {
            // 分片上传中，可将各个已上传的块记录下来，再次上传时，已上传的部分不用再次上传。
            // 断点记录类需实现 com.qiniu.android.storage.Recorder 接口。已提供保存到文件的 FileRecorder 实现。
            Recorder mDefaultRecorder = new FileRecorder(blockDirPath.getAbsolutePath());
            return new UploadManager(mDefaultRecorder);
        } catch (IOException e) {
            e.printStackTrace();
            return new UploadManager();
        }
    }

}
