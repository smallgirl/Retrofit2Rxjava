package com.rxhttputils;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.rxhttputils.api.ApiService;
import com.rxhttputils.bean.User;
import com.rxjava.http.RetrofitClient;
import com.rxjava.http.download.DownloadObserver;
import com.rxjava.http.exception.ApiException;
import com.rxjava.http.gsonconverter.CustomGoonConvertFactory;
import com.rxjava.http.observer.BaseObserver;
import com.rxjava.http.transformer.Transformer;
import com.rxjava.http.upload.UploadListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import okhttp3.ResponseBody;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button single_http, global_http, download_http, upload_http, rxjava;
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
        rxjava = (Button) findViewById(R.id.rxjava);
        single_http.setOnClickListener(this);
        rxjava.setOnClickListener(this);
        global_http = (Button) findViewById(R.id.global_http);
        global_http.setOnClickListener(this);
        download_http = (Button) findViewById(R.id.download_http);
        download_http.setOnClickListener(this);
        upload_http = (Button) findViewById(R.id.upload_http);
        upload_http.setOnClickListener(this);

//        final Observable<List<User> > cache=  Observable.create(new ObservableOnSubscribe<List<User>>() {
//
//
//            @Override
//            public void subscribe(ObservableEmitter<List<User> > e) throws Exception {
//                Log.e("tag","cache---subscribe");
//                e.onNext(new ArrayList<User>());
//                e.onComplete();
//            }
//        });
//
//
//        final Observable<List<User> > netWork=  RetrofitClient
//                .getApiService(ApiService.class)
//                .getUserList();
//
//        Observable.concat(cache,netWork)
//                .compose(Transformer.<List<User>>switchSchedulers(loading_dialog))
//                .subscribe(new BaseObserver<List<User>>() {
//                    @Override
//                    public void onError(ApiException exception) {
//                        responseTv.setText("onError-->"+exception.code+ "-->"+exception.msg);
//                        loading_dialog.dismiss();
//                    }
//
//                    @Override
//                    public void onSuccess(List<User> userList) {
//                        String name="";
//                        for ( User user:userList){
//                            name=name+"   "+user.getNickName();
//                        }
//                        responseTv.setText(name);
//                        loading_dialog.dismiss();
//                    }
//                });

//        RetrofitClient
//                .getApiService(ApiService.class)
//                .getUser()
//                .flatMap(new Function<User, ObservableSource<List<User>>>() {
//                    @Override
//                    public ObservableSource<List<User>> apply(@NonNull User user) throws Exception {
//                        return  RetrofitClient
//                                .getApiService(ApiService.class)
//                                .getUserList();
//                    }
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new BaseObserver<List<User>>() {
//                    @Override
//                    public void onError(ApiException exception) {
//                        Log.e("tag","error"+exception.msg);
//                    }
//
//                    @Override
//                    public void onSuccess(List<User> userList) {
//                        Log.e("tag","onSuccess");
//                    }
//                });
    }

    @Override
    public void onClick(View v) {
        responseTv.setText("");
        switch (v.getId()) {

            case R.id.single_http:
//                RetrofitClient
//                        .newRetrofit()
//                        .showLog(true)
//                        .factory(CustomGoonConvertFactory.create(true))
//                        .createApiService(ApiService.class)
//                        .getUser()
//                        .compose(Transformer.<User>switchSchedulers(loading_dialog))
//                        .subscribe(new BaseObserver<User>() {
//                            @Override
//                            public void onError(ApiException exception) {
//                                responseTv.setText("onError-->"+exception.code+ "-->"+exception.msg);
//                                loading_dialog.dismiss();
//                            }
//
//                            @Override
//                            public void onSuccess(User user) {
//                                Log.e("tag","onSuccess"+user.getNickName());
//                                responseTv.setText(user.getNickName());
//                                loading_dialog.dismiss();
//                            }
//                        });
                RetrofitClient
                        .newRetrofit()
                        .showLog(true)
                        .factory(CustomGoonConvertFactory.create(false))
                        .createApiService(ApiService.class)
                        .getUserString()
                        .compose(Transformer.<String>switchSchedulers(loading_dialog))
                        .subscribe(new BaseObserver<String>() {
                            @Override
                            public void onError(ApiException exception) {
                                responseTv.setText("onError-->" + exception.code + "-->" + exception.msg);
                                loading_dialog.dismiss();
                            }

                            @Override
                            public void onSuccess(String user) {
                                responseTv.setText(user);
                                loading_dialog.dismiss();
                            }
                        });
                break;
            case R.id.global_http:
                RetrofitClient
                        .getApiService(ApiService.class)
                        .getUserList()
                        .doOnNext(new Consumer<List<User>>() {
                            @Override
                            public void accept(List<User> users) throws Exception {
                                Log.e("tag", Thread.currentThread().getName());
                            }
                        })
                        .compose(Transformer.<List<User>>switchSchedulers(loading_dialog))
                        .subscribe(new BaseObserver<List<User>>() {
                            @Override
                            public void onError(ApiException exception) {
                                responseTv.setText("onError-->" + exception.code + "-->" + exception.msg);
                                loading_dialog.dismiss();
                            }

                            @Override
                            public void onSuccess(List<User> userList) {
                                String name = "";
                                for (User user : userList) {
                                    name = name + "   " + user.getNickName();
                                }
                                responseTv.setText(name);
                                loading_dialog.dismiss();
                            }
                        });
                break;

            case R.id.download_http:
                String url = "http://ucdl.25pp.com/fs08/2017/07/11/3/2_666a04ad4b4058e9b7500436c9f55417.apk";
                final String fileName = "test.apk";
                String fileDir = Environment.getExternalStorageDirectory() + File.separator;
                RetrofitClient
                        .createDownloadService(ApiService.class)
                        .downloadFile(url)
                        .compose(Transformer.<ResponseBody>switchSchedulers())
                        .subscribe(new DownloadObserver(fileDir, fileName) {
                            @Override
                            public void onError(int code, String s) {
                                download_http.setEnabled(true);
                                download_http.setText("下载失败");
                            }

                            @Override
                            public void onProgress(long bytesRead, long contentLength, int progress) {
                                download_http.setText("下载中：" + progress + "%");
                            }

                            @Override
                            public void onSuccess(String filePath) {
                                download_http.setEnabled(true);
                                download_http.setText("文件下载");
                                responseTv.setText("下载文件路径：" + filePath);
                            }
                        });

                download_http.setEnabled(false);
                break;
            case R.id.upload_http:
                String uploadUrl = "http://api.vd.cn/info/getbonusnotice/";
                String file = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test.jpg";
                if (!new File(file).exists()) {
                    Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show();
                }
                upload_http.setEnabled(false);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("file", new File(file));
                map.put("name", "test");
                RetrofitClient
                        .createUploadService(ApiService.class, new UploadListener() {
                            @Override
                            public void onRequestProgress(long bytesWritten, long contentLength, int progress) {
                                upload_http.setText(progress + "%");
                                Log.e("tag-progress","progress"+progress);
                            }
                        })
                        .uploadFile(uploadUrl, RetrofitClient.getUploadParam(map))
                        .compose(Transformer.<String>switchSchedulers())
                        .doFinally(new Action() {
                            @Override
                            public void run() throws Exception {
                                upload_http.setEnabled(true);
                            }
                        })
                        .subscribe(new BaseObserver<String>() {
                            @Override
                            public void onSuccess(String s) {
                                upload_http.setText("上传完毕");
                                Log.e("tag", "responseBody" +s);
                            }

                            @Override
                            public void onError(ApiException exception) {
                                upload_http.setText("上传失败");
                                Log.e("tag", "responseBody" + exception.response);
                            }

                        });


                break;
            case R.id.rxjava:
                startActivity(new Intent(this, RxjavaActivity.class));
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
