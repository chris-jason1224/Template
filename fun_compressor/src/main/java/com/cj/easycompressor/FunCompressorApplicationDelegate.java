package com.cj.easycompressor;

import android.content.Context;
import android.content.res.Configuration;

import com.cj.annontations.module.ModuleRegister;
import com.cj.easycompressor.core.EasyCompressor;
import com.cj.manager.module.interfaces.IModuleApplicationDelegate;

import java.util.Map;

/**
 * Author:chris - jason
 * Date:2019/3/11.
 * Package:com.cj.easycompressor
 */
@ModuleRegister(moduleName = "fun_compressor",delegateName = "com.cj.easycompressor.FunCompressorApplicationDelegate")
public class FunCompressorApplicationDelegate implements IModuleApplicationDelegate {
    @Override
    public void onCreate(Context context) {
        //初始化EasyCompressor
        EasyCompressor.init(context);
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
