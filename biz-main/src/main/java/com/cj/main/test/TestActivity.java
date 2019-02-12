package com.cj.main.test;

import android.os.Bundle;

import android.view.View;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.cj.common.mvp.BaseMVPActivity;
import com.cj.main.R;


import butterknife.OnClick;

@Route(path = "/biz-main/ACT/com.cj.main.test.TestActivity")
public class TestActivity extends BaseMVPActivity<ITestPresenter> implements ITestView {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int resourceLayout() {
        return R.layout.biz_main_activity_test;
    }

    @Override
    public void onReloadClick() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected ITestPresenter createPresenter() {
        return new TestPresenter();
    }

    @OnClick({})
    public void onClick(View v){
        int id=v.getId();



    }





}
