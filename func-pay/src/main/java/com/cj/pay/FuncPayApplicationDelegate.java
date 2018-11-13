package com.cj.pay;

import android.content.Context;
import android.content.res.Configuration;

import com.cj.annontations.module.ModuleRegister;
import com.cj.manager.module.interfaces.IModuleApplicationDelegate;

import java.util.Map;

/**
 * Created by mayikang on 2018/9/30.
 */

@ModuleRegister(moduleName = "func-pay",delegateName = "com.cj.pay.FuncPayApplicationDelegate")
public class FuncPayApplicationDelegate implements IModuleApplicationDelegate {

    private Context mContext;


    private FuncPayApplicationDelegate(){

    }

    private static class Holder{
        private static final FuncPayApplicationDelegate instance=new FuncPayApplicationDelegate();
    }

    public static FuncPayApplicationDelegate getInstance(){
        return Holder.instance;
    }


    @Override
    public void onCreate(Context context) {
        mContext=context;
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

    public Context getContext() {
        return mContext;
    }

}
