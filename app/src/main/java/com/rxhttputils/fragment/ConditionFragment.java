package com.rxhttputils.fragment;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.rxhttputils.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Consumer;

/**
 * Created by wanghaitao on 2018/4/22.
 */

public class ConditionFragment extends BaseFragment {
    @BindView(R.id.btn_amb)
    Button btnAmb;
    @BindView(R.id.btn_defaultIfEmpty)
    Button btnDefaultIfEmpty;
    Unbinder unbinder;

    @Override
    public int initView() {
        return R.layout.fragment_condition;
    }

    @OnClick({R.id.btn_amb, R.id.btn_defaultIfEmpty})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //给定多个Observable，只让第一个发送数据的Observable发送数据
            case R.id.btn_amb:
                // 设置2个需要发送的Observable & 放入到集合中
                List<ObservableSource<Integer>> list= new ArrayList<>();
                // 第1个Observable延迟1秒发射数据
                list.add( Observable.just(1,2,3).delay(1,TimeUnit.SECONDS));
                // 第2个Observable正常发送数据
                list.add( Observable.just(4,5,6));

                // 一共需要发送2个Observable的数据
                // 但由于使用了amba（）,所以仅发送先发送数据的Observable
                // 即第二个（因为第1个延时了）
                Observable.amb(list).subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.e(TAG, "接收到了事件 "+integer);
                    }
                });

                break;
                //如果源Observable没有发送数据，则发送一个默认数据
            case R.id.btn_defaultIfEmpty:
                Observable.create(new ObservableOnSubscribe<Object>(){

                    @Override
                    public void subscribe(ObservableEmitter<Object> e) throws Exception {
                        e.onComplete();
                    }
                }).defaultIfEmpty(404).subscribe(consumer,onErrorAction);
                break;
        }
    }
}
