package com.shuai.qiniulib.lib;



import com.shuai.qiniulib.QiNiuUploader;

import java.util.concurrent.CountDownLatch;

/**
 * 单文件上传任务Runnable
 */
public class QiNiuUploadFileTask implements Runnable, QiNiuUploadFileHandler {

    private final String mFilePath;
    private final QiNiuUploadCallback mCallback;
    private final QiNiuUploader mUploader;
    private final String mKey;
    private QiNiuUploadCore mQiNiuUploadCore;

    private transient CountDownLatch mDoneSignal;

    private final int RETRY_TIMES = 3;
    private int mRetryCount = 0;//重试次数统计

    public QiNiuUploadFileTask(String filePath, String key, QiNiuUploader uploader, QiNiuUploadCallback callback) {
        this.mFilePath = filePath;
        this.mKey = key;
        this.mUploader = uploader;
        this.mCallback = callback;
    }

    @Override
    public void run() {
        this.mDoneSignal = new CountDownLatch(1);//只允许单线程访问Task
        doUpload(false, mFilePath, mKey, mCallback);
        try {
            mDoneSignal.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void doUpload(boolean obtainTokenFromTokenLoader, final String filePath, final String key, final QiNiuUploadCallback callback) {

        QiNiuLog.d("doUpload执行->" + "mRetryCount = " + mRetryCount);
        if (!QiNiuUtil.isFileCanRead(filePath)) {
            if (callback != null) {
                callback.onError(key, QiNiuErrorCode.UPLOAD_CANT_READ_FILE_ERROR.code, QiNiuErrorCode.UPLOAD_CANT_READ_FILE_ERROR.message);
            }
            return;
        }
        /*1、获取Token*/
        obtainToken(obtainTokenFromTokenLoader, new ObtainTokenCallback() {
            @Override
            public void onSuccess(String token) {
                /*1、开始上传*/
                if (mQiNiuUploadCore == null) {
                    mQiNiuUploadCore = QiNiuUploadCore.create();
                }

                mQiNiuUploadCore.upload(filePath, key, token, new QiNiuUploadCallback.QiNiuUploadInnerCallback() {

                    @Override
                    public void onProgress(String key, double percent) {
                        if (callback != null) {
                            callback.onProgress(key, percent);
                        }
                    }

                    @Override
                    public void onComplete(String key, String info) {
                        mRetryCount = 0;
                        if (callback != null) {
                            callback.onComplete(key, info);
                        }
                        releaseLatch();//上传完成，释放锁
                    }

                    //上传失败
                    @Override
                    public void onError(String key, int statusCode, String error) {
                        //从TokenLoader重新获取Token以重试
                        mRetryCount++;
                        if (mRetryCount < RETRY_TIMES) {
                            doUpload(true, filePath, key, callback);
                        } else {
                            if (callback != null) {
                                callback.onError(key, statusCode, error);
                            }
                            releaseLatch();//上传失败（超过最大重试次数），释放锁
                        }
                    }

                    @Override
                    public void onCancel(String key, int statusCode, String error) {
                        if (callback != null) {
                            callback.onCancel(key, statusCode, error);
                        }
                        releaseLatch();//上传取消，释放锁
                    }
                });
            }

            //获取Token失败
            @Override
            public void onError(String error) {
                if (callback != null) {
                    callback.onError(key, QiNiuErrorCode.OBTAIN_TOKEN_ERROR.code, QiNiuErrorCode.OBTAIN_TOKEN_ERROR.message + ":" + error);
                }
                releaseLatch();//释放锁
            }
        });

    }


    /**
     * 获取Token
     *
     * @param obtainTokenFromTokenLoader 是否从TokenLoader中从获取Token，false的话，使用已经保存的token
     * @param callback                   获取结果
     */
    private void obtainToken(boolean obtainTokenFromTokenLoader, final ObtainTokenCallback callback) {

        //当getTokenFromTokenLoader为true，或者token 为空的时候，则重新获取
        if (obtainTokenFromTokenLoader || QiNiuUtil.isStrNullOrEmpty(mUploader.getToken())) {
            if (mUploader.getTokenLoader() != null) {
                mUploader.getTokenLoader().getUploadToken(new QiNiuUploadTokenLoader.QiNiuUploadTokenResult() {
                    @Override
                    public void onSuccess(String token) {
                        //更新QiNiuUploadFileHelper中的Token
                        mUploader.setToken(token);
                        callback.onSuccess(token);
                    }

                    @Override
                    public void onError(String error) {
                        callback.onError(error);
                    }
                });
            } else {
                callback.onError("QiNiuUploadTokenLoader不可以为空");
            }
        } else {
            callback.onSuccess(mUploader.getToken());
        }
    }

    interface ObtainTokenCallback {
        void onSuccess(String token);

        void onError(String error);
    }


    @Override
    public void cancel() {
        if (mQiNiuUploadCore != null) {
            mQiNiuUploadCore.cancel();//取消上传任务，会直接回调到onCancel()
        }
    }

    private void releaseLatch() {
        try {
            mDoneSignal.countDown();//释放锁
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
