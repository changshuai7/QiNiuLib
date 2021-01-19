package com.shuai.qiniulib;

public class QiNiuAuth {

    /**
     * 生成下载签名（自定义有效时长）
     *
     * @param baseUrl 待签名文件url，如 http://img.domain.com/u/3.jpg 、
     *                http://img.domain.com/u/3.jpg?imageView2/1/w/120
     * @param expires 有效时长，单位秒。默认3600s
     * @return
     */
    public static String generatePrivateDownloadUrl(String baseUrl, long expires) {
        if (QiNiuConfig.getConfig() != null && QiNiuConfig.getConfig().getAuth() != null) {
            return QiNiuConfig.getConfig().getAuth().privateDownloadUrl(baseUrl, expires);
        }
        return null;
    }

    /**
     * 生成下载签名（默认有效时长：3600s）
     *
     * @param baseUrl
     * @return
     */
    public static String generatePrivateDownloadUrl(String baseUrl) {
        if (QiNiuConfig.getConfig() != null && QiNiuConfig.getConfig().getAuth() != null) {
            return QiNiuConfig.getConfig().getAuth().privateDownloadUrl(baseUrl);
        }
        return null;
    }

    /**
     * 生成上传Token
     *
     * @param bucket
     * @return
     */
    public static String generateUploadToken(String bucket) {
        if (QiNiuConfig.getConfig() != null && QiNiuConfig.getConfig().getAuth() != null) {
            return QiNiuConfig.getConfig().getAuth().uploadToken(bucket);
        }
        return null;
    }


}
