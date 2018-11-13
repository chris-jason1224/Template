package com.cj.share_login;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;

import com.cj.annontations.module.ModuleRegister;
import com.cj.log.CJLog;
import com.cj.manager.module.interfaces.IModuleApplicationDelegate;
import com.mob.MobSDK;

import java.util.List;
import java.util.Map;

/**
 * Created by mayikang on 2018/8/23.
 */
@ModuleRegister(moduleName = "func-share-login",delegateName = "com.cj.share_login.FuncShareLoginApplicationDelegate")
public class FuncShareLoginApplicationDelegate implements IModuleApplicationDelegate{
    private Context context;
    @Override
    public void onCreate(Context context) {
        this.context=context;
        //初始化ShareSDk
        MobSDK.init(context,ShareConfig.MOB_APP_KEY,ShareConfig.MOB_APP_SECRET);
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

    @Override
    public void onCurrentActivity(Activity activity) {

    }

    @Override
    public void onActivityTask(List<Activity> activityList) {

    }
}
