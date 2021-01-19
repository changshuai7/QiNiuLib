package com.shuai.qiniulib.lib;

/**
 * 上传Token生成器接口
 */
public interface QiNiuUploadTokenLoader {

    void getUploadToken(QiNiuUploadTokenResult result);

    interface QiNiuUploadTokenResult {

        void onSuccess(String token);

        void onError(String error);
    }
}
