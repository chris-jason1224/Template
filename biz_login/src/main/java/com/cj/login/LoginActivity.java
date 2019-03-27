package com.cj.login;

import android.arch.lifecycle.Observer;
import android.os.Handler;
import android.support.annotation.Nullable;

import android.os.Bundle;
import android.view.View;


import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cj.annontations.module.ModuleRegister;
import com.cj.common.base.BaseActivity;
import com.cj.common.bus.DataBus;
import com.cj.common.bus.DataBusKey;
import com.cj.log.CJLog;
import com.gyf.barlibrary.ImmersionBar;

@Route(path="/biz_login/ACT/com.cj.login.LoginActivity")
public class LoginActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getLoadService().showSuccess();
            }
        },3000);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected int resourceLayout() {
        return R.layout.activity_login;
    }

    @Override
    public View initStatusLayout() {
        return null;
    }


    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected boolean useImmersionBar() {
        return true;
    }

    @Override
    protected void initImmersionBar() {
        //super.initImmersionBar();
        immersionBar = ImmersionBar.with(this);
        immersionBar.
                transparentStatusBar().
                statusBarDarkFont(false).//顶部状态栏是否为深色字体
                keyboardEnable(true).//解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
                navigationBarWithKitkatEnable(false).//是否可以修改安卓4.4和emui3.1手机导航栏颜色，默认为true
                init();
    }

    //重写点击事件
    @Override
    public void onClick(View v) {

    }


}
