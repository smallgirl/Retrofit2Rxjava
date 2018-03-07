package com.rxhttputils;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rxhttputils.api.ApiService;
import com.rxhttputils.bean.User;
import com.rxjava.http.RetrofitClient;
import com.rxjava.http.download.DownloadObserver;
import com.rxjava.http.exception.ApiException;
import com.rxjava.http.observer.BaseObserver;
import com.rxjava.http.transformer.Transformer;
import com.rxjava.http.upload.UploadListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button  single_http, global_http,  download_http, upload_http;
    private Dialog loading_dialog;
    private TextView responseTv;

    private List<Disposable> disposables = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loading_dialog = new AlertDialog.Builder(this).setMessage("loading...").create();
        setContentView(R.layout.activity_main);
        responseTv = (TextView) findViewById(R.id.response_tv);
        single_http = (Button) findViewById(R.id.single_http);
        single_http.setOnClickListener(this);
        global_http = (Button) findViewById(R.id.global_http);
        global_http.setOnClickListener(this);
        download_http = (Button) findViewById(R.id.download_http);
        download_http.setOnClickListener(this);
        upload_http = (Button) findViewById(R.id.upload_http);
        upload_http.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        responseTv.setText("");
        switch (v.getId()) {

            case R.id.single_http:
                RetrofitClient
                        .getInstance()
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
//                RetrofitClient
//                        .getApiService(ApiService.class)
//                        .getUserResponse()
//                        .compose(new DefaultTransformer<User>())
//                        .subscribe(new BaseObserver<User>() {
//                            @Override
//                            public void onError(ApiException exception) {
//                                responseTv.setText("onError-->"+exception.code+ "-->"+exception.message);
//                                loading_dialog.dismiss();
//                            }
//
//                            @Override
//                            public void onSuccess(User user) {
//                                responseTv.setText(user.getNickName());
//                                loading_dialog.dismiss();
//                            }
//                        });
               // 是用该方法 先将 CustomGoonResponseBodyConvector 中的
               //return adapter.fromJson(response.getString("data")); 换成  return adapter.fromJson(result);
                break;
            case R.id.global_http:
                RetrofitClient
                        .getApiService(ApiService.class)
                        .getUserList()
                        .compose(Transformer.<List<User>>switchSchedulers(loading_dialog))
                        .subscribe(new BaseObserver<List<User>>() {
                            @Override
                            public void onError(ApiException exception) {
                                responseTv.setText("onError-->"+exception.code+ "-->"+exception.message);
                                loading_dialog.dismiss();
                            }

                            @Override
                            public void onSuccess(List<User> userList) {
                                String name="";
                                for ( User user:userList){
                                    name=name+"   "+user.getNickName();
                                }
                                responseTv.setText(name);
                                loading_dialog.dismiss();
                            }
                        });
                break;

            case R.id.download_http:
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
                break;
            case R.id.upload_http:
                String uploadUrl = "http://server.jeasonlzy.com/OkHttpUtils/upload";
                String file = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"2.jpg";
                if (!new File(file).exists()){
                    Toast.makeText(this,"文件不存在",Toast.LENGTH_SHORT).show();
                }
                upload_http.setEnabled(false);
                RetrofitClient
                        .uploadImg(uploadUrl, file, new UploadListener() {
                            @Override
                            public void onRequestProgress(long bytesWritten, long contentLength, int progress) {
                                upload_http.setText( progress+"%");
                            }
                        })
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

                break;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposables != null) {
            for (Disposable disposable : disposables) {
                disposable.dispose();
            }
            disposables.clear();
        }
    }
}
