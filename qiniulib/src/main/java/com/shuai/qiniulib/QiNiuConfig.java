package com.shuai.qiniulib;

import com.qiniu.util.Auth;

public class QiNiuConfig {

    private static QiNiuConfig.Config sConfig;

    public static Config getConfig() {
        return sConfig;
    }

    public static QiNiuConfig.Config init(String accessKey, String secretKey) {
        if (sConfig == null) {
            synchronized (QiNiuConfig.class) {
                if (sConfig == null) {
                    sConfig = new QiNiuConfig.Config(accessKey, secretKey);
                }
            }
        }
        return sConfig;
    }

    public static class Config {
        private final Auth auth;

        public Config(String accessKey, String secretKey) {
            this.auth = Auth.create(accessKey, secretKey);
        }

        public Auth getAuth() {
            return auth;
        }
    }
}
