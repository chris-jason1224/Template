package com.cj.db;


import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;

import com.cj.annontations.module.ModuleRegister;
import com.cj.common.util.AndroidSystemUtil;

import com.cj.db.helper.DBManager;
import com.cj.manager.module.interfaces.IModuleApplicationDelegate;


import java.util.List;
import java.util.Map;

/**
 * Created by mayikang on 2018/10/12.
 * func-db组件初始化入口类
 */
@ModuleRegister(moduleName = "func-db" , delegateName = "com.cj.db.FuncDbApplicationDelegate")
public class FuncDbApplicationDelegate implements IModuleApplicationDelegate {

    private Context context;

    @Override
    public void onCreate(Context context) {
        this.context=context;

        //初始化数据库组件
        DBManager.getInstance().initDB(context);

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
