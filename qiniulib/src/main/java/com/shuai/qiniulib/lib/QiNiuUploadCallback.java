package com.shuai.qiniulib.lib;

/**
 * 七牛云上传回调接口
 */
public interface QiNiuUploadCallback {

    void onStart(String key);

    void onProgress(String key, double percent);

    void onComplete(String key, String info);

    void onError(String key, int statusCode, String error);

    void onCancel(String key, int statusCode, String error);

    // QiNiuUploader回调接口，此回调仅限内部使用
    interface QiNiuUploadInnerCallback {

        void onProgress(String key, double percent);

        void onComplete(String key, String info);

        void onError(String key, int statusCode, String error);

        void onCancel(String key, int statusCode, String error);
    }
}