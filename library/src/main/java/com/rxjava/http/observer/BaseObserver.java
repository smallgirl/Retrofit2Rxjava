package com.rxjava.http.observer;

import com.rxjava.http.exception.ApiException;
import com.rxjava.http.exception.ExceptionEngine;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by wanghaitao on 2018/2/28.
 * <p>
 * BaseObserver
 */
public abstract class BaseObserver<T> implements Observer<T> {

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(@NonNull T t) {
        onSuccess(t);
    }

    @Override
    public void onError(@NonNull Throwable e) {
        onError(ExceptionEngine.handleException(e));
    }

    @Override
    public void onComplete() {
    }

    public abstract void onSuccess(T t);

    public abstract void onError(ApiException exception);
}
