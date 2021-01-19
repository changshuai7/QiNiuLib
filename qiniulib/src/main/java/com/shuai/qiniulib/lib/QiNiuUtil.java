package com.shuai.qiniulib.lib;

import android.text.TextUtils;

import java.io.File;

/**
 * 工具类
 */
public class QiNiuUtil {

    public static boolean isStrNullOrEmpty(String string) {
        return string == null || string.trim().length() == 0;
    }

    public static boolean isFileCanRead(String filePath) {
        if (TextUtils.isEmpty(filePath)) return false;
        File file = new File(filePath);
        if (!file.isFile() || !file.canRead()) return false;
        return true;
    }

    public static String getFileName(String filePath) {
        if (TextUtils.isEmpty(filePath)) return null;
        File file = new File(filePath);
        return file.getName();
    }
}
