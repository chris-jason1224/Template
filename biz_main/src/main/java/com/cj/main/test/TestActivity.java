package com.cj.main.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cj.common.mvp.BaseMVPActivity;
import com.cj.main.R;


@Route(path = "/biz_main/ACT/com.cj.main.test.TestActivity")
public class TestActivity extends BaseMVPActivity<ITestPresenter> implements ITestView {

    Fragment map;

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
        map = (Fragment) ARouter.getInstance().build("/fun_lbs/FRG/com.cj.fun_lbs.map.base.BaseMapFragment").navigation();
        getSupportFragmentManager().beginTransaction().add(R.id.box, map).show(fragment).commit();
    }

    @Override
    protected void initView() {

    }

    @Override
    protected ITestPresenter createPresenter() {
        return new TestPresenter();
    }

    @Override
    public void onClick(View v) {

    }
}
