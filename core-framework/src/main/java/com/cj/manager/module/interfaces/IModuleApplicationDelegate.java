package com.cj.manager.module.interfaces;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.Map;

/**
 * 各个组件的Application接口类
 */
public interface IModuleApplicationDelegate {

    void onCreate(Context context);

    void enterBackground();

    //app进入前台，
    void enterForeground();

    void receiveRemoteNotification(Map<String, String> var1);

    void onTerminate();

    void onConfigurationChanged(Configuration var1);

    void onLowMemory();

    void onTrimMemory(int var1);


}
