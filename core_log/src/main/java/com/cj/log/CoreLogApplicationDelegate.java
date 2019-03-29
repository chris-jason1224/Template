package com.cj.log;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import com.cj.annontations.module.ModuleRegister;
import com.cj.log.util.CrashHandler;
import com.cj.manager.module.interfaces.IModuleApplicationDelegate;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mayikang on 2018/9/7.
 */

@ModuleRegister(moduleName = "core_log" , delegateName = "com.cj.log.CoreLogApplicationDelegate")
public class CoreLogApplicationDelegate implements IModuleApplicationDelegate {

    private Context context;

    @Override
    public void onCreate(Context context) {
        this.context=context;
        //注入统一的java层异常捕获
        Thread.setDefaultUncaughtExceptionHandler(CrashHandler.getInstance(context));
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
