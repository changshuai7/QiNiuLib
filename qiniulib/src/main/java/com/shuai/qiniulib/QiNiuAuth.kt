package com.shuai.qiniulib

class QiNiuAuth {

    companion object {
        /**
         * 生成下载签名（自定义有效时长）
         *
         * @param baseUrl 待签名文件url，如 http://img.domain.com/u/3.jpg 、
         * http://img.domain.com/u/3.jpg?imageView2/1/w/120
         * @param expires 有效时长，单位秒。默认3600s
         * @return
         */
        @JvmStatic
        fun generatePrivateDownloadUrl(baseUrl: String?, expires: Long): String? {
            return QiNiuConfig.config?.auth?.privateDownloadUrl(baseUrl, expires)
        }

        /**
         * 生成下载签名（默认有效时长：3600s）
         *
         * @param baseUrl
         * @return
         */
        @JvmStatic
        fun generatePrivateDownloadUrl(baseUrl: String?): String? {
            return QiNiuConfig.config?.auth?.privateDownloadUrl(baseUrl)
        }

        /**
         * 生成上传Token
         *
         * @param bucket
         * @return
         */
        @JvmStatic
        fun generateUploadToken(bucket: String?): String? {
            return QiNiuConfig.config?.auth?.uploadToken(bucket)
        }
    }

}