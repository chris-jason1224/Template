package com.cj.fun_media;
import android.content.Context;
import android.content.res.Configuration;
import com.cj.annontations.module.ModuleRegister;
import com.cj.manager.module.interfaces.IModuleApplicationDelegate;
import java.util.Map;

/**
 * Created by mayikang on 2018/9/7.
 */

@ModuleRegister(moduleName = "fun_media" , delegateName = "com.cj.fun_media.FunMediaApplicationDelegate")
public class FunMediaApplicationDelegate implements IModuleApplicationDelegate {

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