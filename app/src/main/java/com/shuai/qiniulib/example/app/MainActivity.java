package com.shuai.qiniulib.example.app;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.shuai.qiniulib.QiNiuAuth;
import com.shuai.qiniulib.QiNiuUploader;
import com.shuai.qiniulib.lib.QiNiuLog;
import com.shuai.qiniulib.lib.QiNiuUploadCallback;
import com.shuai.qiniulib.lib.QiNiuUploadFileHandler;
import com.shuai.qiniulib.lib.QiNiuUploadTokenLoader;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.util.UUID;

import io.reactivex.functions.Consumer;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mTest;

    private Button mBtnStart1, mBtnCancel1;
    private TextView mTvUploadProgress1;

    private Button mBtnStart2, mBtnCancel2;
    private TextView mTvUploadProgress2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initPermissions();
        initData();

    }


    private void initView() {

        mTest = findViewById(R.id.btn_test);
        mTest.setOnClickListener(this);

        mBtnStart1 = findViewById(R.id.btn_start1);
        mBtnCancel1 = findViewById(R.id.btn_cancel1);
        mTvUploadProgress1 = findViewById(R.id.tv_upload_progress1);
        mBtnStart1.setOnClickListener(this);
        mBtnCancel1.setOnClickListener(this);


        mBtnStart2 = findViewById(R.id.btn_start2);
        mBtnCancel2 = findViewById(R.id.btn_cancel2);
        mTvUploadProgress2 = findViewById(R.id.tv_upload_progress2);
        mBtnStart2.setOnClickListener(this);
        mBtnCancel2.setOnClickListener(this);

    }

    @SuppressLint("CheckResult")
    private void initPermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.setLogging(true);
        rxPermissions
                .request(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                )
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                        } else {

                        }
                    }
                });
    }

    private QiNiuUploader mUploader;
    private QiNiuUploadFileHandler mTask1;//上传任务1
    private QiNiuUploadFileHandler mTask2;//上传任务2

    private void initData() {
        mUploader = QiNiuUploader.create()
                .setTokenLoader(new QiNiuUploadTokenLoader() {
                    @Override
                    public void getUploadToken(QiNiuUploadTokenResult result) {
                        String token = QiNiuAuth.generateUploadToken(Constant.privateBucket);
                        result.onSuccess(token);
                    }
                })
                .build();
    }


    @Override
    public void onClick(View v) {

        int id = v.getId();

        //请将文件放入SDK内测试，传入文件名
        if (id == R.id.btn_test) {
            Toast.makeText(this, "测试", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.btn_start1) {
            mTask1 = uploadFile(/*"Android进阶之光.pdf"*/"testjpg.jpg", mTvUploadProgress1);
        } else if (id == R.id.btn_cancel1) {
            mTask1.cancel();
        } else if (id == R.id.btn_start2) {
            mTask2 = uploadFile("疯狂Java讲义第4版.pdf", mTvUploadProgress2);
        } else if (id == R.id.btn_cancel2) {
            mTask2.cancel();
        }
    }


    /**
     * 上传文件，请将文件放入SDK卡中，fileName传入文件名即可
     *
     * @param fileName
     * @param tvUploadProgress
     * @return
     */
    public QiNiuUploadFileHandler uploadFile(String fileName, TextView tvUploadProgress) {
        final String dir = Environment.getExternalStorageDirectory().toString() + File.separator + fileName;
        final File f = new File(dir);

        //开始上传
        return mUploader.upload(f.getAbsolutePath(), UUID.randomUUID().toString(), new MyQiNiuUploadCallback(fileName, tvUploadProgress));

    }


    /**
     * 上传结果回调
     */
    static class MyQiNiuUploadCallback implements QiNiuUploadCallback {

        String tag = "";
        TextView tvUploadProgress;

        public MyQiNiuUploadCallback(String tag, TextView tvUploadProgress) {
            this.tag = tag;
            this.tvUploadProgress = tvUploadProgress;
        }

        @Override
        public void onStart(String key) {
            QiNiuLog.d("[---" + tag + "---]  " + "onStart：" + ",key=" + key);
        }

        @Override
        public void onProgress(String key, double percent) {
            QiNiuLog.d("[---" + tag + "---]  " + "onProgress：" + ",key=" + key + ",percent=" + percent);
            tvUploadProgress.setText(percent + "");
        }

        @Override
        public void onComplete(String key, String info) {
            QiNiuLog.d("[---" + tag + "---]  " + "onComplete：" + key + ",info=" + info);
            String url = QiNiuAuth.generatePrivateDownloadUrl(Constant.baseUrl + key);
            QiNiuLog.d("完整的URL为：" + url);

        }

        @Override
        public void onError(String key, int statusCode, String error) {
            QiNiuLog.e("[---" + tag + "---]  " + "onError：" + key + ",error=" + error);

        }

        @Override
        public void onCancel(String key, int statusCode, String error) {
            QiNiuLog.e("[---" + tag + "---]  " + "onCancel：" + key + ",error=" + error);
        }
    }
}
