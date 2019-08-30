package com.cj.common.base;

import android.app.ActivityManager;
import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.cj.common.BuildConfig;
import com.cj.common.bus.ModuleBus;
import com.cj.common.db.DBCenter;
import com.cj.common.receiver.NetworkStateOBReceiver;
import com.cj.common.states.OnPlaceHolderCallback;
import com.cj.common.states.OnEmptyStateCallback;
import com.cj.common.states.OnTimeoutStateCallback;
import com.cj.common.util.kv.SPFUtil;
import com.cj.common.util.image.ImageLoader;
import com.cj.manager.basement.BaseApplication;
import com.kingja.loadsir.callback.SuccessCallback;
import com.kingja.loadsir.core.LoadSir;

import java.util.List;


/**
 * Created by mayikang on 2018/7/22.
 */

public class BaseApp extends BaseApplication {


    @Override
    public void onCreate() {
        //优先执行各个DelegateApplication的onCreate
        super.onCreate();

        //初始化ARouter路由
        if (BuildConfig.DEBUG) {   // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }

        ARouter.init(this);

        //初始化SharedPreferences工具类
        SPFUtil.init(this);

        //注册网络变化监听
        registerNetStateReceiver();

        //注册多布局
        registerLoadSir();

        //初始化图片加载框架
        ImageLoader.init(this);

        //初始化DBCenter
        DBCenter.init(this);

        //初始化ModuleBus
        ModuleBus.getInstance().init(this);

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
                .addCallback(new OnEmptyStateCallback())//空数据页面
                .addCallback(new OnTimeoutStateCallback())//连接超时、网络错误页面
                .addCallback(new OnPlaceHolderCallback())//占位页面
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

    private boolean isCurrentMainProcess() {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            //获取运行的进程列表
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = manager.getRunningAppProcesses();

            if (runningAppProcessInfoList != null && runningAppProcessInfoList.size() > 0) {

                for (ActivityManager.RunningAppProcessInfo info : runningAppProcessInfoList) {
                    //当前进程 id = 运行进程 id，默认主进程名等于包名
                    if (info.pid == android.os.Process.myPid()) {
                        if (TextUtils.equals(info.processName, getPackageName())) {
                            return true;
                        }
                    }
                }

            }
        }

        return false;
    }



}
