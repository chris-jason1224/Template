package com.cj.main;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;

import com.cj.annontations.module.ModuleRegister;
import com.cj.log.CJLog;
import com.cj.manager.manager.ModuleManager;
import com.cj.manager.module.interfaces.IModuleApplicationDelegate;

import java.util.List;
import java.util.Map;

/**
 * Created by mayikang on 2018/8/23.
 */
@ModuleRegister(moduleName = "biz-main",delegateName = "com.cj.main.BizMainApplicationDelegate")
public class BizMainApplicationDelegate  implements IModuleApplicationDelegate{
    private Context context;
    @Override
    public void onCreate(Context context) {
        this.context=context;

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
