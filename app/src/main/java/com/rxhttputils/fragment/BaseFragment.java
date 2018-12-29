package com.rxhttputils.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.rxhttputils.MyApplication;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.functions.Consumer;


/**
 * Created by wanghaitao on 2018/4/11.
 */

public abstract class BaseFragment<T> extends Fragment {
    Unbinder unbinder;
    public static final String TAG = "RxJava";

    Consumer consumer = new Consumer<T>() {
        @Override
        public void accept(T t) throws Exception {
            Log.e("tag","" +t);
            Toast.makeText(MyApplication.getInstance(), "onNext:" + t, Toast.LENGTH_SHORT).show();
        }
    };
    Consumer onErrorAction = new Consumer<T>() {
        @Override
        public void accept(T t) throws Exception {
            Toast.makeText(MyApplication.getInstance(), "nError:" + t, Toast.LENGTH_SHORT).show();
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(initView(), container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    //设置视图
    public abstract int initView();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
