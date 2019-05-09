package com.cj.fun_push;

import android.content.Context;
import android.content.res.Configuration;

import com.cj.annontations.module.ModuleRegister;
import com.cj.fun_push.jpush.JPushService;
import com.cj.manager.module.interfaces.IModuleApplicationDelegate;

import java.util.Map;

import cn.jpush.android.api.JPushInterface;

/**
 * Author:chris - jason
 * Date:2019/4/15.
 * Package:com.cj.fun_push
 */
@ModuleRegister(moduleName = "fun_push",delegateName = "com.cj.fun_push.FunPushApplicationDelegate")
public class FunPushApplicationDelegate implements IModuleApplicationDelegate {

    @Override
    public void onCreate(Context context) {
        //初始化极光推送
        if(BuildConfig.DEBUG){
            JPushInterface.setDebugMode(true);
        }
        JPushInterface.init(context.getApplicationContext());

    }

    @Override
    public void enterBackground() {

    }

    @Override
    public void enterForeground() {

    }

    @Override
    public void receiveRemoteNotification(Map<String, String> var1) {

    }

    @Override
    public void onTerminate() {

    }

    @Override
    public void onConfigurationChanged(Configuration var1) {

    }

    @Override
    public void onLowMemory() {

    }

    @Override
    public void onTrimMemory(int var1) {

    }
}
