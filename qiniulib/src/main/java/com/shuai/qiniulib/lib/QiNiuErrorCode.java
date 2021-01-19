package com.shuai.qiniulib.lib;

/**
 * 错误码枚举
 */
public enum QiNiuErrorCode {

    OTHER_ERROR(-10000, "发生未知错误"),
    OBTAIN_TOKEN_ERROR(-10001, "获取UploadToken失败"),
    UPLOAD_CANT_READ_FILE_ERROR(-10002, "无法读取待上传文件");

    public int code;
    public String message;

    QiNiuErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
