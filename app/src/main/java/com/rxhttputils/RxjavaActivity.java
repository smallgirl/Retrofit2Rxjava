package com.rxhttputils;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;

import com.rxhttputils.fragment.BooleanFragment;
import com.rxhttputils.fragment.CombinationFragment;
import com.rxhttputils.fragment.ConditionFragment;
import com.rxhttputils.fragment.ConversionFragment;
import com.rxhttputils.fragment.CreateFragment;
import com.rxhttputils.fragment.ErrorFragment;
import com.rxhttputils.fragment.FilterFragment;
import com.rxhttputils.fragment.SupportFragment;
import com.rxhttputils.fragment.TransformFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class RxjavaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    @BindView(R.id.nv_menu)
    NavigationView nvMenu;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    private Unbinder unbinder;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;
    ActionBarDrawerToggle drawerToggle;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rxjava);
        unbinder = ButterKnife.bind(this);
        combinaToolBarAndNavigationView();
        //初始化Fragment
        initFragment();
    }
    private void combinaToolBarAndNavigationView() {
        setSupportActionBar(toolbar);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar , R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        nvMenu.setNavigationItemSelectedListener(this);
        setTitle(nvMenu.getMenu().getItem(0).getTitle());
    }

    protected Fragment switchItem(MenuItem item, Fragment fragment) {
        switch (item.getItemId()) {
            //创建操作符
            case R.id.item_create:
                fragment = new CreateFragment();
                break;
            //变换操作符
            case R.id.item_transform:
                fragment = new TransformFragment();
                break;
            //过滤操作符
            case R.id.item_filter:
                fragment = new FilterFragment();
                break;
            //组合操作符
            case R.id.item_combina:
                fragment = new CombinationFragment();
                break;
            //辅助操作符
            case R.id.item_support:
                fragment = new SupportFragment();
                break;
            //错误操作符
            case R.id.item_error:
                fragment = new ErrorFragment();
                break;
            //布尔操作符
            case R.id.item_boolean:
                fragment = new BooleanFragment();
                break;
            //条件操作符
            case R.id.item_condition:
                fragment = new ConditionFragment();
                break;
            //转换操作符
            case R.id.item_convertion:
                fragment = new ConversionFragment();
                break;
        }
        drawerLayout.closeDrawers();
        return fragment;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    private void initFragment() {
        mFragmentManager = getSupportFragmentManager();
        mFragmentTransaction = mFragmentManager.beginTransaction();
        CreateFragment createFragment = new CreateFragment();
        mFragmentTransaction.replace(R.id.fl_container, createFragment);
        mFragmentTransaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        Fragment fragment = null;
        @SuppressLint({"NewApi", "LocalSuppress"})
        Animator animator = ViewAnimationUtils.createCircularReveal(
                flContainer,
                0,
                0,
                0,
                flContainer.getWidth()
        );
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(500);
        animator.start();
        fragment = switchItem(item, fragment);
        item.setChecked(true);
        setTitle(item.getTitle());
        mFragmentTransaction.replace(R.id.fl_container, fragment);
        mFragmentTransaction.commit();
        return true;
    }
}
