package com.cj.login;

import android.arch.lifecycle.Observer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cj.annontations.module.ModuleRegister;
import com.cj.common.base.BaseActivity;
import com.cj.common.bus.DataBus;
import com.cj.common.bus.DataBusKey;
import com.cj.log.CJLog;
import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;
@Route(path="/biz_login/login")
public class LoginActivity extends BaseActivity {

    private Observer<String> observer=new Observer<String>() {
        @Override
        public void onChanged(@Nullable String s) {
            CJLog.getInstance().log_e("我接受到了数据："+s);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getLoadService().showSuccess();
            }
        },3000);

        //注册接收
        DataBus.get().with(DataBusKey.login.getKey(),DataBusKey.login.getT()).observe(this,observer);

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

        //移除注册
        DataBus.get().with(DataBusKey.login.getKey(),DataBusKey.login.getT()).removeObserver(observer);

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
    public void onReloadClick() {

    }

    @Override
    protected void initData() {

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


}
