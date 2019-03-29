package com.cj.main.test;

import android.app.FragmentTransaction;
import android.os.Bundle;

import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cj.common.mvp.BaseMVPActivity;
import com.cj.main.R;


@Route(path = "/biz_main/ACT/com.cj.main.test.TestActivity")
public class TestActivity extends BaseMVPActivity<ITestPresenter> implements ITestView {

    private FrameLayout mBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int resourceLayout() {
        return R.layout.biz_main_activity_test;
    }

    @Override
    public View initStatusLayout() {
        return null;
    }

    @Override
    protected void initData() {
        TestFragment fragment = new TestFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.box,fragment).show(fragment).commit();
    }

    @Override
    protected void initView() {
        mBox = fb(R.id.box);
    }

    @Override
    protected ITestPresenter createPresenter() {
        return new TestPresenter();
    }

    @Override
    public void onClick(View v) {

    }
}
