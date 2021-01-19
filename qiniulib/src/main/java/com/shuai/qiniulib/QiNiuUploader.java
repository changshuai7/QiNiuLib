package com.shuai.qiniulib;



import com.shuai.qiniulib.lib.QiNiuUploadCallback;
import com.shuai.qiniulib.lib.QiNiuUploadFileHandler;
import com.shuai.qiniulib.lib.QiNiuUploadFileTask;
import com.shuai.qiniulib.lib.QiNiuUploadTokenLoader;
import com.shuai.qiniulib.lib.QiNiuUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class QiNiuUploader {

    //管理线程池
    private final ExecutorService mUploadFileExecutor = Executors.newFixedThreadPool(3);

    private final QiNiuUploadTokenLoader mTokenLoader;
    private transient String mToken;//保存Token，下次无特殊情况，不需要重新通过QiNiuUploadTokenLoader获取(仅存于调用者的内存中)

    public static Build create() {
        return new Build();
    }

    private QiNiuUploader(QiNiuUploadTokenLoader mTokenLoader, String mToken) {
        this.mTokenLoader = mTokenLoader;
        this.mToken = mToken;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String token) {
        this.mToken = token;
    }

    public QiNiuUploadTokenLoader getTokenLoader() {
        return mTokenLoader;
    }

    public QiNiuUploadFileHandler upload(String filePath, QiNiuUploadCallback callback) {
        return upload(filePath, null, callback);
    }

    /**
     * 执行上传
     *
     * @param filePath 文件路径
     * @param key      文件key（名称）
     * @param callback 上传回调
     * @return
     */
    public QiNiuUploadFileHandler upload(String filePath, String key, final QiNiuUploadCallback callback) {

        if (QiNiuUtil.isStrNullOrEmpty(key)) {
            key = QiNiuUtil.getFileName(filePath);
        }

        if (callback != null) {
            callback.onStart(key);
        }

        final QiNiuUploadFileTask task = new QiNiuUploadFileTask(filePath, key, this, callback);
        //mUploadFileExecutor.execute(task);
        final Future<?> submit = mUploadFileExecutor.submit(task);

        return new QiNiuUploadFileHandler() {

            @Override
            public void cancel() {
                try {
                    task.cancel();//取消正在上传的任务
                    if (submit != null) submit.cancel(true);//取消pending中的任务
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    /////////////////////////////// Build 用于传递相关配置 ///////////////////////////////

    public static class Build {
        private QiNiuUploadTokenLoader mTokenLoader;

        public Build setTokenLoader(QiNiuUploadTokenLoader mTokenLoader) {
            this.mTokenLoader = mTokenLoader;
            return this;
        }

        public QiNiuUploader build() {
            return new QiNiuUploader(mTokenLoader, null);
        }
    }

}
