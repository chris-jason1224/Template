package com.cj.fun_orm;

import android.content.Context;
import android.content.res.Configuration;
import com.cj.annontations.module.ModuleRegister;
import com.cj.manager.module.interfaces.IModuleApplicationDelegate;
import java.util.Map;


/**
 * Author:chris - jason
 * Date:2019/4/4.
 * Package:com.cj.fun_orm
 */

@ModuleRegister(moduleName = "fun_orm",delegateName = "com.cj.fun_orm.FunOrmApplicationDelegate")
public class FunOrmApplicationDelegate implements IModuleApplicationDelegate {

    @Override
    public void onCreate(Context context) {

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
