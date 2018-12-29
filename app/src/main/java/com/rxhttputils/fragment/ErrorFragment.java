package com.rxhttputils.fragment;

import android.view.View;
import android.widget.Button;

import com.rxhttputils.R;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Function;


/**
 * Created by wanghaitao on 2018/4/21.
 */

public class ErrorFragment extends BaseFragment {
    @BindView(R.id.btn_onErrorReturn)
    Button btnOnErrorReturn;
    @BindView(R.id.btn_onErrorResumeNext)
    Button btnOnErrorResumeNext;
    @BindView(R.id.btn_onExceptionResumeNext)
    Button btnOnExceptionResumeNext;
    @BindView(R.id.btn_retry)
    Button btnRetry;

    @Override
    public int initView() {
        return R.layout.fragment_error;
    }

    @OnClick({R.id.btn_onErrorReturn, R.id.btn_onErrorResumeNext, R.id.btn_onExceptionResumeNext, R.id.btn_retry})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //onErrorReturn，会在遇到错误时，
            // 停止源Observable的，并调用用户自定义的返回请求
            // 实质上就是调用一次OnNext方法进行内容发送后，停止消息发送
            case R.id.btn_onErrorReturn:
                Observable.create(new ObservableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                        for (int i = 0; i < 5; i++) {
                            if (i > 3) {
                                emitter.onError(new Throwable("User Alex Defined Error"));
                            }
                            emitter.onNext(i);
                        }
                    }
                }).onErrorReturn(new Function<Throwable, Object>() {
                    @Override
                    public Object apply(Throwable throwable) throws Exception {
                        return 404;
                    }
                }).subscribe(consumer, onErrorAction);
                break;
            //onErrorResumeNext，会在源Observable遇到错误时，立即停止源Observable的数据发送
            //并取用新的Observable对象进行新的数据发送
            case R.id.btn_onErrorResumeNext:
                Observable.create(new ObservableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                        for (int i = 0; i < 5; i++) {
                            if (i > 3) {
                                emitter.onError(new Throwable("User Alex Defined Error"));
                            }
                            emitter.onNext(i);
                        }
                    }
                }).onErrorResumeNext(new Function<Throwable, ObservableSource<?>>() {
                    @Override
                    public ObservableSource<?> apply(Throwable throwable) throws Exception {
                        return Observable.just(100,101,102);
                    }
                }).subscribe(consumer, onErrorAction);
                break;
            //onExceptionResumeNext，会将错误发给Observer，而不会调用备用的Observable
            case R.id.btn_onExceptionResumeNext:
                Observable.create(new ObservableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                        for (int i = 0; i < 5; i++) {
                            if (i > 3) {
                                emitter.onError(new Throwable("User Alex Defined Error"));
                            }
                            emitter.onNext(i);
                        }
                    }
                }).onExceptionResumeNext(Observable.just(100,101,102)).subscribe(consumer, onErrorAction);
                break;
            case R.id.btn_retry:
                //retry操作符，当遇到exception时会进行重试，重试次数可以由用户进行定义
                Observable.create(new ObservableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                        for (int i = 0; i < 5; i++) {
                            if (i > 1) {
                                emitter.onError(new Throwable("User Alex Defined Error"));
                            }
                            emitter.onNext(i);
                        }
                    }
                }).retry(2).subscribe(consumer, onErrorAction);
                break;
        }
    }
}
