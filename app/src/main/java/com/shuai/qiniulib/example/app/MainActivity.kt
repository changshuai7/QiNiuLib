package com.shuai.qiniulib.example.app

import android.Manifest
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.shuai.qiniulib.QiNiuAuth

import com.shuai.qiniulib.QiNiuUploader
import com.shuai.qiniulib.lib.QiNiuLog
import com.shuai.qiniulib.lib.QiNiuUploadCallback
import com.shuai.qiniulib.lib.QiNiuUploadFileHandler
import com.shuai.qiniulib.lib.QiNiuUploadTokenLoader
import com.shuai.qiniulib.lib.QiNiuUploadTokenLoader.QiNiuUploadTokenResult
import java.io.File
import java.text.DecimalFormat
import java.util.*

class MainActivity : FragmentActivity(), View.OnClickListener {

    private lateinit var mTest: Button

    private lateinit var mBtnStart1: Button
    private lateinit var mBtnCancel1: Button
    private lateinit var mTvUploadProgress1: TextView

    private lateinit var mBtnStart2: Button
    private lateinit var mBtnCancel2: Button
    private lateinit var mTvUploadProgress2: TextView

    private var mUploader: QiNiuUploader? = null
    private var mTask1: QiNiuUploadFileHandler? = null//上传任务1
    private var mTask2: QiNiuUploadFileHandler? = null //上传任务2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initPermissions();
        initData()
    }

    private fun initView() {
        mTest = findViewById(R.id.btn_test)

        mBtnStart1 = findViewById(R.id.btn_start1)
        mBtnCancel1 = findViewById(R.id.btn_cancel1)
        mTvUploadProgress1 = findViewById(R.id.tv_upload_progress1)

        mBtnStart2 = findViewById(R.id.btn_start2)
        mBtnCancel2 = findViewById(R.id.btn_cancel2)
        mTvUploadProgress2 = findViewById(R.id.tv_upload_progress2)

        mBtnStart1.setOnClickListener(this)
        mBtnCancel1.setOnClickListener(this)
        mTest.setOnClickListener(this)
        mBtnStart2.setOnClickListener(this)
        mBtnCancel2.setOnClickListener(this)

    }

    private fun initPermissions() {
        val arr = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        ActivityCompat.requestPermissions(this, arr, 100)

    }

    private fun initData() {

        mUploader = QiNiuUploader.create()
                .setTokenLoader(object : QiNiuUploadTokenLoader {
                    override fun getUploadToken(result: QiNiuUploadTokenResult) {
                        val token = QiNiuAuth.generateUploadToken(Constant.privateBucket)
                        result.onSuccess(token)
                    }
                })
                .build()

    }

    override fun onClick(v: View) {

        //请将文件放入SDK内测试，传入文件名
        when (v.id) {
            R.id.btn_test -> {
                Toast.makeText(this, "测试", Toast.LENGTH_SHORT).show()
            }
            R.id.btn_start1 -> {
                mTask1 = uploadFile( /*"Android进阶之光.pdf"*/"testjpg.jpg", mTvUploadProgress1 as TextView)
            }
            R.id.btn_cancel1 -> {
                mTask1!!.cancel()
            }
            R.id.btn_start2 -> {
                mTask2 = uploadFile("疯狂Java讲义第4版.pdf", mTvUploadProgress2 as TextView)
            }
            R.id.btn_cancel2 -> {
                mTask2!!.cancel()
            }
        }
    }

    /**
     * 上传文件，请将文件放入SDK卡中，fileName传入文件名即可
     *
     * @param fileName
     * @param tvUploadProgress
     * @return
     */
    private fun uploadFile(fileName: String, tvUploadProgress: TextView): QiNiuUploadFileHandler {
        val dir = Environment.getExternalStorageDirectory().toString() + File.separator + fileName
        val f = File(dir)

        //开始上传
        return mUploader!!.upload(f.absolutePath, UUID.randomUUID().toString(), MyQiNiuUploadCallback(fileName, tvUploadProgress))
    }

    /**
     * 上传结果回调
     */
    internal class MyQiNiuUploadCallback(private var tag: String, private var tvUploadProgress: TextView) : QiNiuUploadCallback {

        override fun onStart(key: String?) {
            QiNiuLog.d("[---$tag---]  onStart：,key=$key")
        }

        override fun onProgress(key: String?, percent: Double?) {
            QiNiuLog.d("[---$tag---]  onProgress：,key=$key,percent=$percent")
            tvUploadProgress.text = DecimalFormat("0.00").format(percent)
        }

        override fun onComplete(key: String?, info: String?) {
            QiNiuLog.d("[---$tag---]  onComplete：$key,info=$info")
            val url = QiNiuAuth.generatePrivateDownloadUrl(Constant.baseUrl + key)
            QiNiuLog.d("完整的URL为：$url")
        }

        override fun onError(key: String?, statusCode: Int, error: String?) {
            QiNiuLog.e("[---$tag---]  onError：$key,error=$error")
        }

        override fun onCancel(key: String?, statusCode: Int, error: String?) {
            QiNiuLog.e("[---$tag---]  onCancel：$key,error=$error")
        }

    }
}