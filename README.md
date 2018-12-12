# Retrofit2Rxjava 封装
------
## 功能
> * 1、解析固定格式的json
> * 2、添加 上传/下载 文件 进度回调
> * 3、统一错误处理，log，cache 等


## 1、解析固定格式的json

通常服务器接口返回的都是固定的json 如下
data 里面才是 正真的实体类，

```json
{
    "code": int,   //请求返回状态码 0表示成功
    "msg":String, // 存放错误信息
    "data": {} || "data": [] 
}
```

对于这种数据类型 本框架 提供两种 解决方案
### 方案1 自定义 Converter.Factory 对请求结果json 统一解析
代码片段如下
```java
    @Override
    public T convert( ResponseBody value) throws IOException {
        try {
            String result = value.string();
            JSONObject response = new JSONObject(result);
            int code = response.optInt("code");
            if (ErrorType.SUCCESS==code) {
               // return adapter.fromJson(result);
                return adapter.fromJson(response.getString("data"));//解析data数据
            }else {
                String message = response.optString("msg", "");
                throw new ServerException(code, message);
            }

        } catch (JSONException e) {
            throw new ServerException(-1, "JSON解析异常");

        }
    }

```
这种方法 就不用每次建立实体 都不用在最外层再套一个 实体
下面是例子
```java

public class User {

    /**
     * nickName : 用户001
     * sex : 0
     */

    private String nickName;
    private int sex;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }
}
public interface ApiService {

    @GET("http://zzuli.gitee.io/api/user.html")
    Observable <User> getUser();
    
    @GET("http://zzuli.gitee.io/api/user.html")
    Observable <Response<User>> getUserResponse();

}

RetrofitClient
        .newRetofit()
        .showLog(true)
        .creatApiService(ApiService.class)
        .getUser()
        .compose(Transformer.<User>switchSchedulers(loading_dialog))
        .subscribe(new BaseObserver<User>() {
            @Override
            public void onError(ApiException exception) {
                responseTv.setText("onError-->"+exception.code+ "-->"+exception.message);
                loading_dialog.dismiss();
            }

            @Override
            public void onSuccess(User user) {
                responseTv.setText(user.getNickName());
                loading_dialog.dismiss();
            }
        });

```
我们的User 就是data 里面的数据，完全不用在建立一个Response类包裹着User
### 方案2 同过操作符 对对象进行转换

```java
public class DefaultTransformer<T> implements ObservableTransformer<Response<T>, T> {


    @Override
    public ObservableSource<T> apply(@NonNull Observable<Response<T>> upstream) {

        return upstream
                .subscribeOn(Schedulers.io())
                .map(new Function<Response<T>, T>() {
                    @Override
                    public T apply(@NonNull Response<T> response) throws Exception {
                        return response.getData();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread());
    }

}

RetrofitClient
    .getApiService(ApiService.class)
    .getUserResponse()
    .compose(new DefaultTransformer<User>())
    .subscribe(new BaseObserver<User>() {
        @Override
        public void onError(ApiException exception) {
            responseTv.setText("onError"+exception.message+exception.code);
            loading_dialog.dismiss();
        }

        @Override
        public void onSuccess(User user) {
            responseTv.setText(user.getNickName());
            loading_dialog.dismiss();
        }
    });
```
如上 DefaultTransformer 中将Response<T>装换成T
## 2、添加 上传/下载 文件 进度回调
上传代码
```java
String uploadUrl = "http://api.vd.cn/info/getbonusnotice/";
String file = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"2.jpg";
upload_http.setEnabled(false);
Map<String, Object> map =new HashMap<String, Object>();
map.put("file",new File(file));
map.put("name","test");
RetrofitClient
        .creatUploadService(ApiService.class, new UploadListener() {
            @Override
            public void onRequestProgress(long bytesWritten, long contentLength, int progress) {
                upload_http.setText( progress+"%");
                if (bytesWritten==contentLength){
                    upload_http.setText( "上传完毕");
                    Log.e("tag","上传完毕");
                }
            }
        })
        .uploadFile(uploadUrl,RetrofitClient.getUploadParam(map))
        .compose(Transformer.<ResponseBody>switchSchedulers())
        .doFinally(new Action() {
            @Override
            public void run() throws Exception {
                upload_http.setEnabled(true);
            }
        })
        .subscribe(new Consumer<ResponseBody>() {
            @Override
            public void accept(@NonNull ResponseBody responseBody) throws Exception {
                upload_http.setText( "上传完毕");
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                upload_http.setText( "上传失败"+throwable);
            }
        });

```
下载代码

```java
String url = "https://t.alipayobjects.com/L1/71/100/and/alipay_wap_main.apk";
final String fileName = "alipay.apk";
String fileDir = getExternalFilesDir(null) + File.separator;
RetrofitClient
        .downloadFile(url)
        .subscribe(new DownloadObserver(fileDir,fileName) {
            @Override
            public void onError(int code, String s) {

            }
            @Override
            public void onProgress(long bytesRead, long contentLength, int progress) {
                download_http.setText("下载中：" + progress + "%");
            }

            @Override
            public void onSuccess(String filePath) {
                download_http.setEnabled(true);
                download_http.setText("文件下载");
                responseTv.setText("下载文件路径："+filePath);
            }
        });

download_http.setEnabled(false);
```
## 3、统一错误处理，log，cache 等

具体见代码

作者 [@wanghaitao]     
2018 年 03月 01日    

