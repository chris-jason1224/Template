package com.cj.common.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.TypedValue;

import com.cj.common.R;
import com.cj.common.receiver.NetworkStateOBReceiver;
import com.cj.common.states.OnPlaceHolderCallback;
import com.cj.common.states.OnEmptyStateCallback;
import com.cj.common.states.OnErrorStateCallback;
import com.cj.common.states.OnLoadingStateCallback;
import com.cj.common.states.OnTimeoutStateCallback;
import com.cj.common.util.SPFUtil;
import com.cj.common.util.image.ImageLoader;
import com.cj.manager.basement.BaseApplication;
import com.kingja.loadsir.callback.SuccessCallback;
import com.kingja.loadsir.core.LoadSir;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;


/**
 * Created by mayikang on 2018/7/22.
 */

public class BaseApp extends BaseApplication {


    @Override
    public void onCreate() {

        super.onCreate();

        //初始化SharedPreferences工具类
        SPFUtil.init(this);

        //注册网络变化监听
        registerNetStateReceiver();

        //注册多布局
        registerLoadSir();

        //初始化图片加载框架
        ImageLoader.init(this);

    }



    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }

    /**
     * 初始化多状态布局全局策略
     */
    private void registerLoadSir() {
        //全局配置
        LoadSir.beginBuilder()
                .addCallback(new OnErrorStateCallback())//错误页面
                .addCallback(new OnEmptyStateCallback())//空页面
                .addCallback(new OnLoadingStateCallback())//加载中页面
                .addCallback(new OnTimeoutStateCallback())//连接超时页面
                .addCallback(new OnPlaceHolderCallback())//自定义页面
                .setDefaultCallback(SuccessCallback.class)//默认显示加载成功页面
                .commit();

        //某个页面单独配置可用以下方式
//        LoadSir loadSir = new LoadSir.Builder()
//                .addCallback(new OnErrorStateCallback())
//                .addCallback(new OnEmptyStateCallback())
//                .addCallback(new OnPlaceHolderCallback())
//                .build();
//        final LoadService loadService = loadSir.register(this, new Callback.OnReloadListener() {
//            @Override
//            public void onReload(View v) {
//                // 重新加载逻辑
//
//            }
//        });

    }

    /**
     * 动态注册网络状态变化监听广播
     */
    private void registerNetStateReceiver() {
        NetworkStateOBReceiver receiver = new NetworkStateOBReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
    }



}
