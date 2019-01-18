package com.cj.main;


import android.os.Handler;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cj.common.base.BaseActivity;
import com.cj.common.bus.DataBus;
import com.cj.common.util.AndroidSystemUtil;
import com.cj.ui.notify.Alerter.AlertManager;
import com.cj.ui.notify.Alerter.AlerterListener;
import com.cj.utils.list.ListUtil;

import java.util.ArrayList;
import butterknife.BindView;
import butterknife.OnClick;


@Route(path = "/biz_main/main")
public class MainActivity extends BaseActivity {

    String str="dfefeffeeeeeeeeeeeeeee3wwwwww122221!!!!!!";
    @BindView(R2.id.base_common_toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //进入页面默认是加载占位页面，这里模拟加载数据，两秒后切换成功页面
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getLoadService().showSuccess();
            }
        }, 2000);

    }

    @Override
    protected int resourceLayout() {
        return R.layout.biz_main_activity_main;
    }


    @Override
    public void onReloadClick() {
        Toast.makeText(this, "main click retry", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void initData() {

    }

    @OnClick({ R2.id.goto_biz_login,R2.id.alert,R2.id.goto_test,R2.id.make_crash,R2.id.foreground,R2.id.encrypt,R2.id.decrypt})
    public void onClick(View v) {

        int vid = v.getId();

        //加密测试
        if(R.id.encrypt == vid){

        }
        //解密测试
        if(R.id.decrypt == vid){


        }


        if(R.id.foreground ==vid){
            AndroidSystemUtil.getInstance().isAppForeground(this);
        }


        //人造一个crash
        if(R.id.make_crash == vid){
            TextView tv=null;
            tv.setText("fffff");
            return;
        }

        //打开biz-login module
        if (R.id.goto_biz_login == vid) {
            //通过数据总线发送给 biz_login
            DataBus.get().with("login").setValue("发送数据，注意接收");
            ArrayList<Integer> list=new ArrayList<>();
            for (int i=0;i<10;i++){
                list.add(i);
            }
            ARouter.getInstance().build("/biz_login/login").navigation();
            return;
        }

        if (R.id.alert == vid) {
            AlertManager.create(MainActivity.this).
                    setTitle("杨二狗").
                    setMessage("嘻嘻嘻嘻").
                    setAutoCollapse(true).
                    setAlertListener(new AlerterListener() {
                        @Override
                        public void onShow() {
                            Log.e("myk", "onShow");
                        }

                        @Override
                        public void onHide() {
                            Log.e("myk", "onHide");
                        }

                    }).show();

            return;
        }

        //跳转TestActivity
        if(R.id.goto_test == vid){
            ARouter.getInstance().build("/biz_main/test").navigation();
        }


    }

    @Override
    protected boolean useImmersionBar() {
        return true;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        immersionBar.
                statusBarDarkFont(true).
                titleBar(toolbar);//解决实际布局顶到statusBar
    }
}
