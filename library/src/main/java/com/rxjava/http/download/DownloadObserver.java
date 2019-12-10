package com.rxjava.http.download;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

/**
 * Created by wanghaitao on 2018/2/28.
 * <p>
 * 下载文件
 */


public abstract class DownloadObserver implements Observer<ResponseBody> {

    private String fileDir;
    private String fileName;

    public DownloadObserver(String fileDir, String fileName) {
        this.fileDir = fileDir;
        this.fileName = fileName;
    }


    @Override
    public void onComplete() {

    }

    public static final String errorMsg_SocketTimeoutException = "网络链接超时，请检查您的网络状态，稍后重试！";
    public static final String errorMsg_ConnectException = "网络链接异常，请检查您的网络状态";
    public static final String errorMsg_UnknownHostException = "网络异常，请检查您的网络状态";
    @Override
    public void onError(@NonNull Throwable e) {
        if (e instanceof SocketTimeoutException) {
            onError(0,errorMsg_SocketTimeoutException);
        } else if (e instanceof ConnectException) {
            onError(0,errorMsg_ConnectException);
        } else if (e instanceof UnknownHostException) {
            onError(0,errorMsg_UnknownHostException);
        } else {
            onError(0,e.getMessage().toString());
        }
    }
    @Override
    public void onNext(@NonNull ResponseBody responseBody) {
        Observable
                .just(responseBody)
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(@NonNull ResponseBody responseBody) throws Exception {
                        try {
                            new DownloadManager().saveFile(responseBody, fileDir,fileName, new ProgressListener() {
                                @Override
                                public void onResponseProgress(final long bytesRead, final long contentLength, final int progress, final boolean done, final String filePath) {
                                    Observable
                                            .just(progress)
                                            .distinctUntilChanged()
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Consumer<Integer>() {
                                                @Override
                                                public void accept(@NonNull Integer integer) throws Exception {

                                                    onProgress(bytesRead, contentLength, progress);
                                                    if (done){
                                                        onSuccess(filePath);
                                                    }
                                                }
                                            });
                                }

                            });

                        } catch (IOException e) {
                            Observable
                                    .just(e)
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<IOException>() {
                                        @Override
                                        public void accept(@NonNull IOException e) throws Exception {
                                            onError(0,e.getMessage());
                                        }
                                    });
                            //;
                        }
                    }
                });

    }

    public void onSubscribe(@NonNull Disposable d) {

    }
    public abstract void onError(int code, String s) ;
    public abstract void onProgress(long bytesRead, long contentLength,int progress) ;
    public abstract void onSuccess(String filePath) ;
}
