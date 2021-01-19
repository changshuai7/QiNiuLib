package com.shuai.qiniulib.lib;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadOptions;

import org.json.JSONObject;

/**
 * 七牛云上传器，利用QiNiu的SDK,执行上传功能核心代码
 */
public class QiNiuUploadCore {

    private volatile boolean mUploadIsCancelled = false;

    private QiNiuUploadCore() {

    }

    public static QiNiuUploadCore create() {
        return new QiNiuUploadCore();
    }

    public void upload(String filePath, String key, String token, final QiNiuUploadCallback.QiNiuUploadInnerCallback callback) {

        //上传进度 配置
        UploadOptions options = new UploadOptions(null, null, true, new UpProgressHandler() {
            @Override
            public void progress(String key, double percent) {
                if (callback != null) {
                    callback.onProgress(key, percent);
                }
            }

        }, new UpCancellationSignal() {
            @Override
            public boolean isCancelled() {
                // 内部代码会检测 UpCancellationSignal##isCancelled() 的返回值
                // 当其返回 true 时，将停止上传。
                // 所以可外部维护一个变量 mUploadCancelled，当点击取消按钮时，设置 mUploadCancelled = true;
                return mUploadIsCancelled;
            }
        });

        //上传结束 配置
        UpCompletionHandler upCompletionHandler = new UpCompletionHandler() {
            @Override
            public void complete(String key, ResponseInfo info, JSONObject jsonData) {

                if (info.isOK()) {
                    //String fileKey = jsonData.optString("key");
                    //String fileHash = jsonData.optString("hash");
                    if (callback != null) {
                        callback.onComplete(key, jsonData != null ? jsonData.toString() : "");
                    }
                } else if (info.isCancelled()) {
                    if (callback != null) {
                        callback.onCancel(key, info.statusCode, info.error);
                    }

                } else {
                    if (callback != null) {
                        callback.onError(key, info.statusCode, info.error);
                    }
                }
            }
        };
        QiNiuUploadManager.getUploadManagerInstance().put(filePath, key, token, upCompletionHandler, options);
    }

    // 点击取消按钮，让 UpCancellationSignal##isCancelled() 方法返回 true，以停止上传
    public void cancel() {
        mUploadIsCancelled = true;
    }

    // 任务是否已经取消
    public boolean isCancelled() {
        return mUploadIsCancelled;
    }

}
