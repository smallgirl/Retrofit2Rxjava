package com.rxjava.http.transformer;


import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by wanghaitao on 2018/2/28.
 * <p>
 * DefaultTransformer
 */

public class DefaultTransformer<T> implements ObservableTransformer<Response<T>, T> {


    @Override
    public ObservableSource<T> apply(@NonNull Observable<Response<T>> upstream) {

        return   upstream
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .map(new Function<Response<T>, T>() {
                    @Override
                    public T apply(@NonNull Response<T> response) throws Exception {
                        return response.getData();
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

}