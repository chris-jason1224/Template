package com.cj.login;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import android.view.View;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.cj.common.base.BaseActivity;
import com.cj.common.bus.DataBusKey;
import com.cj.log.CJLog;
import com.gyf.barlibrary.ImmersionBar;
import com.jeremyliao.liveeventbus.LiveEventBus;

@Route(path="/biz_login/ACT/com.cj.login.LoginActivity")
public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        LiveEventBus.get().with(DataBusKey.ProcessSubDataEvent.getKey(),DataBusKey.ProcessSubDataEvent.getT()).observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                CJLog.getInstance().log_e("LoginAct "+ s);
            }
        });
    }

    @Override
    protected void initView() {
        fb(R.id.send1).setOnClickListener(this);
        fb(R.id.send2).setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if(vid == R.id.send1){

        }

        if(vid == R.id.send2){
            startActivity(new Intent(this,PActivity.class));
        }

    }



}
