package com.rxhttputils.fragment;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.rxhttputils.R;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by wanghaitao on 2018/4/19.
 */

public class CombinationFragment extends BaseFragment {
    @BindView(R.id.btn_startWith)
    Button btnStartWith;
    @BindView(R.id.btn_merge)
    Button btnMerge;
    @BindView(R.id.btn_concat)
    Button btnConcat;
    @BindView(R.id.btn_zip)
    Button btnZip;
    @BindView(R.id.btn_combineLastest)
    Button btnCombineLastest;

    @Override
    public int initView() {
        return R.layout.fragment_combina;
    }

    @OnClick({R.id.btn_startWith, R.id.btn_merge, R.id.btn_concat, R.id.btn_zip, R.id.btn_combineLastest})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //startWith操作符，会在发送的数据之前插入数据
            case R.id.btn_startWith:
                Observable.range(1, 2).startWith(0).subscribe(consumer);
                //Observable.range(1, 2).startWithArray(-1,0).subscribe(consumer);
                break;
            //merge操作符，会将多个Observable对象合并到一个Observable对象中进行发送，merge可能会让合并的数据错乱
            case R.id.btn_merge:
                Observable<Integer> firstObservable = Observable.just(0, 1, 2).subscribeOn(Schedulers.io());
                Observable<Integer> secondObservable = Observable.just(3, 4, 5);
                Observable.merge(firstObservable, secondObservable).subscribe(consumer, onErrorAction);
                //Observable.mergeDelayError(firstObservable, secondObservable).subscribe(consumer, onErrorAction);
                break;
            //concat操作符，会将多个Observable对象合并到一个Observable对象中进行发送，严格按照顺序进行发送
            case R.id.btn_concat:
//                firstObservable = Observable.just(0, 1, 2).subscribeOn(Schedulers.io());
//                secondObservable = Observable.just(3, 4, 5);
//                Observable.concat(firstObservable, secondObservable)
//                        .subscribeOn(Schedulers.io())
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribe(consumer);


                Observable.concat(
                        Observable.create(new ObservableOnSubscribe<Integer>() {
                            @Override
                            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {

                                emitter.onNext(1);
                                emitter.onNext(2);
                                emitter.onNext(3);
                                emitter.onError(new NullPointerException()); // 发送Error事件，因为无使用concatDelayError，所以第2个Observable将不会发送事件
                                emitter.onComplete();
                            }
                        }),
                        Observable.just(4, 5, 6))
                        .subscribe(new Observer<Integer>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }
                            @Override
                            public void onNext(Integer value) {
                                Log.e("tag", "onNext"+ value  );
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("tag"," onError");
                            }

                            @Override
                            public void onComplete() {
                                Log.e("tag","onComplete");
                            }
                        });
                break;
            //zip操作符，会将多个Observable对象转换成一个Observable对象然后进行发送，转换关系可根据需求自定义
            case R.id.btn_zip:
                Observable<Integer> integerObservable = Observable.range(0, 4);
                Observable<Integer> integerObservable1 = Observable.range(0, 4);
                Observable<String> stringObservable = Observable.just("a", "b", "c", "d");
                Observable.zip(integerObservable, stringObservable, new BiFunction<Integer, String, Object>() {
                    @Override
                    public Object apply(Integer integer, String s) throws Exception {
                        return "数字为:" + integer + "……字符为：" + s;
                    }
                }).subscribe(consumer);
                break;
            //combineLastest操作符，会将多个Observable对象转换一个Observable对象然后进行发送，转换关系可以根据需求自定义
            //不同于zip操作符的是，会将最新发送的数据组合到一起
            case R.id.btn_combineLastest:
                integerObservable = Observable.just(1, 2, 3).delay(200, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread());
                stringObservable = Observable.just("a", "b", "c");
                Observable.combineLatest(integerObservable, stringObservable, new BiFunction<Integer, String, Object>() {
                    @Override
                    public Object apply(Integer num, String info) {
                        //在这里的转换关系为将数字与字串内容进行拼接
                        return "数字为:" + num + "……字符为：" + info;
                    }
                }).subscribe(consumer);
                break;
        }
    }
}
