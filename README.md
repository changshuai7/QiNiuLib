# QiNiuLib:七牛云文件上传库

## 写在前边

本框架基于七牛云SDK,封装了七牛云文件上传的功能。


### 1、集成方式

```
dependencies {
        implementation 'com.shuai:qiniulib:x.x.x'
}

版本号 一般采用Tag中最新的版本。
```


### 2、使用。

1、创建QiNiuUploader
```
QiNiuUploader mUploader = QiNiuUploader
        .create()
        .setTokenLoader(new QiNiuUploadTokenLoader() {
            @Override
            public void getUploadToken(QiNiuUploadTokenResult result) {

                //这里可通过网络请求异步获取上传Token，通过result.onSuccess("token")回传给SDK

            }
        })
        .build();
```

* QiNiuUploader内部维护了一个线程池，可并发同时上传多个文件而没有性能上的影响。一般来说，QiNiuUploader可在项目中设置成单例使用。
* setTokenLoader为必传内容，须传入Token的生成器，即QiNiuUploadTokenLoader的实现类，并且在getUploadToken方法中，通过异步网络请求获取上传Token将其回传。Token具有复用机制，Token在有效期内不会重复获取，以减少不必要的网络请求。


2、上传文件
```
/**
 * filePath:文件路径
 * key：文件名
 */
QiNiuUploadFileHandler task = mUploader.upload("filePath", "key", new QiNiuUploadCallback() {
        @Override
        public void onStart(String key) {
            //上传开始
        }

        @Override
        public void onProgress(String key, double percent) {
            //上传进度
        }

        @Override
        public void onComplete(String key, String info) {
            //上传成功结束
        }

        @Override
        public void onError(String key, int statusCode, String error) {
            //上传失败
        }

        @Override
        public void onCancel(String key, int statusCode, String error) {
            //上传取消

        }
    });

```

* upload方法返回为一个QiNiuUploadFileHandler对象，可通过QiNiuUploadFileHandler.cancel()来取消正在上传的任务
* upload方法内部具有重试机制，如果上传发生失败，会重试三次（此时每次都会获取最新的上传Token），三次都失败，则回调onError()


### 3、高级

**说明：**

对于上传文件，我们建议的做法是：

1. 通过服务端生成七牛云上传的Token传给客户端
2. 客户端通过此Token上传文件到七牛云服务器。
3. 上传成功后，返回key（文件名），key要传递给服务端，服务端通过key将拼接好的下载地址回传。


强烈建议按照上述的流程来执行文件的上传。避免七牛云秘钥的泄露，造成数据风险。

**客户端操作秘钥：**

如果想在客户端完成生成上传Token、获取下载地址的功能（虽然我们强烈不建议这么做），本库也开放了相关的Api

首先需要在Applicaiton中初始化accessKey和secretKey：
```
QiNiuConfig.init(String accessKey, String secretKey);
```

由此可使用相关的功能：

1、获取上传Token的API：
```
String token = QiNiuAuth.generateUploadToken(String bucket);
```
bucket需要在七牛云申请


2、根据key生成私有下载地址的Api：
```
String url = QiNiuAuth.generatePrivateDownloadUrl(String baseUrl);
```
baseUrl为待签名的文件url，形如："http://img.domain.com/u/3.jpg"、"http://img.domain.com/u/3.jpg?imageView2/1/w/120"


## 4、混淆
```
-keep class com.qiniu.**{*;}
-keep class com.qiniu.**{public <init>();}
-ignorewarnings

```
## 5、更多

更多请查阅官网开发者文档：https://developer.qiniu.com/kodo/1236/android

