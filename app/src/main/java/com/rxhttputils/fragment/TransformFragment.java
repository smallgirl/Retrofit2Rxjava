package com.rxhttputils.fragment;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rxhttputils.MyApplication;
import com.rxhttputils.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;


/**
 * Created by wanghaitao on 2018/4/11.
 */

public class TransformFragment extends BaseFragment {
    @BindView(R.id.btn_map)
    Button btnMap;
    @BindView(R.id.btn_flatMap)
    Button btnFlatMap;
    @BindView(R.id.btn_cast)
    Button btnCast;
    @BindView(R.id.btn_concatMap)
    Button btnConcatMap;
    @BindView(R.id.btn_flatMapIterable)
    Button btnFlatMapIterable;
    @BindView(R.id.btn_buffer)
    Button btnBuffer;
    Unbinder unbinder;

    @Override
    public int initView() {
        return R.layout.fragment_transform;
    }

    @OnClick({R.id.btn_map, R.id.btn_flatMap, R.id.btn_cast, R.id.btn_concatMap, R.id.btn_flatMapIterable, R.id.btn_buffer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //map操作符，通过指定一个Func对象，将Observable转换为另一个Observable对象并发送
            case R.id.btn_map:
            Observable.just(1)
                    .map(new Function<Integer, String>() {
                        @Override
                        public String apply(Integer s) throws Exception {
                            return "My Name is" + s;
                        }
                    }).subscribe(consumer);
                break;
            //flatMap操作符，将Observable发送的数据集合转换为Observable集合
            //flatMap的合并运行允许交叉，允许交错的发送事件
            case R.id.btn_flatMap:
                Observable.fromArray(1, 2, 3).flatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Exception {
                        if (2 == integer) {
                            return Observable.just("My Name is:" + integer);
                        }
                        return Observable.just("My Name is:" + integer).delay(200, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread());
                    }
                }).subscribe(consumer);
                break;
            //cast操作符，将类对象进行转换
            // 需强调的一点是只能由父类对象转换为子类对象
            case R.id.btn_cast:
                Object[] objectsArr = {"1", "2", "3"};
                Observable.fromArray(objectsArr).cast(String.class).subscribe(consumer);
                break;
            //concatMap操作符，将Observable发送的数据集合转换为Observable集合
            //解决了flatMap的交叉问题，将发送的数据连接发送
            case R.id.btn_concatMap:
                Observable.fromArray(1, 2, 3).concatMap(new Function<Integer, ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(Integer integer) throws Exception {
                        if (2 == integer) {
                            return Observable.just("My Name is:" + integer);
                        }
                        return Observable.just("My Name is:" + integer).delay(200, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread());
                    }
                }).subscribe(consumer);
                break;
            //将数据集合转换为Iterable，在Iterable中对数据进行处理
            case R.id.btn_flatMapIterable:
                Observable.just(1, 2, 3).flatMapIterable(new Function<Integer, Iterable<Integer>>() {
                @Override
                public Iterable<Integer> apply(Integer integer) throws Exception {
                    ArrayList<Integer> mList = new ArrayList<>();
                    mList.add(1000 + integer);
                    return mList;
                }
            }).subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {

                    }
                });

                break;
            //buffer操作符，将原有Observable转换为一个新的Observable，这个新的Observable每次发送一组值，而不是一个个进行发送
            case R.id.btn_buffer:

                Observable.just(1, 2, 3, 4, 5)
                        .buffer(3,1).subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> list) throws Exception {
                        for (Integer i : list) {
                            Toast.makeText(MyApplication.getInstance(), "new Number i is:" + i, Toast.LENGTH_SHORT).show();
                            Log.e("tag","new Number i is:" + i);
                        }
                        Toast.makeText(MyApplication.getInstance(), "Another request is called", Toast.LENGTH_SHORT).show();
                        Log.e("tag","Another request is called");
                    }
                });
                break;

        }
    }
}
