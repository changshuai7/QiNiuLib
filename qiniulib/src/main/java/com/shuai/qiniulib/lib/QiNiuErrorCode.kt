package com.shuai.qiniulib.lib

/**
 * 错误码枚举
 */
enum class QiNiuErrorCode(var code: Int, var message: String) {
    OTHER_ERROR(-10000, "发生未知错误"),
    OBTAIN_TOKEN_ERROR(-10001, "获取UploadToken失败"),
    OBTAIN_TOKEN_EMPTY(-10002, "获取到的UploadToken为空"),
    UPLOAD_CANT_READ_FILE_ERROR(-10003, "无法读取待上传文件"),
    UPLOAD_FILE_KEY_EMPTY_ERROR(-10004, "FileKey为空");

}