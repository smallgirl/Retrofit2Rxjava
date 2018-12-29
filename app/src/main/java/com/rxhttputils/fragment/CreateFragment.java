package com.rxhttputils.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rxhttputils.MyApplication;
import com.rxhttputils.R;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by wanghaitao on 2018/4/11.
 */

public class CreateFragment extends BaseFragment {
    @BindView(R.id.btn_create)
    Button btnCreate;
    @BindView(R.id.btn_just)
    Button btnJust;
    @BindView(R.id.btn_from)
    Button btnFrom;
    Unbinder unbinder;
    @BindView(R.id.btn_interval)
    Button btnInterval;
    @BindView(R.id.btn_range)
    Button btnRange;
    @BindView(R.id.btn_repeat)
    Button btnRepeat;

    @Override
    public int initView() {
        return R.layout.fragment_create;
    }

    Disposable subscribe;

    @OnClick({R.id.btn_create, R.id.btn_just, R.id.btn_from, R.id.btn_interval, R.id.btn_range, R.id.btn_repeat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //创建的基础用法（create操作符）
            case R.id.btn_create:
                //被观察者
                Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                        emitter.onNext(1);
                        emitter.onNext(2);
                        emitter.onComplete();
                    }
                });
                //被观察者和观察者建立订阅关系
                observable.subscribe(consumer);
                break;
            //just操作符，创建将逐个内容进行发送的Observable，其内部发送内容在内部以from的操作符的方式进行转换
            case R.id.btn_just:
                Observable.fromArray("one", "two").subscribe(consumer);
                break;
            //from操作符，创建以数组内容发送事件的Observable
            case R.id.btn_from:
                String[] observableArr = new String[]{"one", "two"};
                Observable.fromArray(observableArr).subscribe(consumer);
                break;
            //interval操作符,创建以1秒为事件间隔发送整数序列的Observable
            case R.id.btn_interval:
                subscribe = Observable.interval(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (aLong >= 3) {
                            subscribe.dispose();
                        }
                        Toast.makeText(MyApplication.getInstance(), "onNext:" + aLong, Toast.LENGTH_SHORT).show();
                    }
                });
                //Observable.interval(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread()).take(3).subscribe(consumer);
                break;
            //range操作符，创建以发送范围内的整数序列的Observable
            case R.id.btn_range:
                Observable.range(2, 5).subscribe(consumer);
                break;
            //repeat操作符,创建一个以N次重复发送数据的Observable
            case R.id.btn_repeat:
                Observable.range(2, 3).repeat(2).subscribe(consumer);
                break;
        }
    }
}
