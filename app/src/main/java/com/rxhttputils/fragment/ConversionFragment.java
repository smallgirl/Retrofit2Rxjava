package com.rxhttputils.fragment;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.rxhttputils.MyApplication;
import com.rxhttputils.R;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by wanghaitao on 2018/4/22.
 */

public class ConversionFragment extends BaseFragment {
    @BindView(R.id.btn_toList)
    Button btnToList;
    @BindView(R.id.btn_toSortedList)
    Button btnToSortedList;
    @BindView(R.id.btn_toMap)
    Button btnToMap;
    Unbinder unbinder;

    @Override
    public int initView() {
        return R.layout.fragment_conversion;
    }

    @OnClick({R.id.btn_toList, R.id.btn_toSortedList, R.id.btn_toMap})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            //toList操作符，将源Observable发送的数据组合为一个List集合
            //然后再次在onNext方法中将转换完的List集合进行传递
            case R.id.btn_toList:
                Observable.just(1, 2, 3).toList().subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> numList) {
                        for (Integer i : numList) {
                            Toast.makeText(MyApplication.getInstance(), "i:" + i, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            //toSortedList操作符,会将源Observable发送的数据组合为一个List集合,并会按照升序的方式进行排序
            //然后再次在onNext方法中将转换完的List集合进行传递
            case R.id.btn_toSortedList:
                Observable.just(40, 10, 80, 30).toSortedList().subscribe(new Consumer<List<Integer>>() {
                    @Override
                    public void accept(List<Integer> numList) {
                        for (Integer i : numList) {
                            Toast.makeText(MyApplication.getInstance(), "i:" + i, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;
            //toMap操作符，将源Observable发送的数据作为Map集合中的值，需要值进行键的定义
            //将转换完毕的Map集合在onNext方法中进行发送
            case R.id.btn_toMap:
              Observable.just("one","two").toMap(new Function<String, Integer>() {
                  @Override
                  public Integer apply(String s) {
                      return s.equals("one")?0:1;
                  }
              }).subscribe(new Consumer<Map<Integer, String>>() {
                               @Override
                               public void accept(Map<Integer, String> convertMap) {
                                   for (int i = 0; i < convertMap.size(); i++) {
                                       Toast.makeText(MyApplication.getInstance(), convertMap.get(i), Toast.LENGTH_SHORT).show();
                                   }
                               }
                           }
              );
                break;
        }
    }


}
