package com.cj.manager.basement;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.cj.manager.manager.ModuleManager;
import com.cj.manager.module.interfaces.IModuleApplicationDelegate;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class BaseApplication extends Application {

    /**
     * 多进程app会重复启动Application
     */

    private  final String TAG = getClass().getSimpleName();

    private static BaseApplication sInstance;
    private List<IModuleApplicationDelegate> mAppDelegateList;

    private int mCurrentCount = 0;
    private WeakReference<Activity> mCurrentActivity = null;
    private boolean isCurrent = false;
    private List<Activity> mActivityList = new ArrayList();
    private List<Activity> mResumeActivity = new ArrayList();

    public BaseApplication() {}

    public static BaseApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        this.mCurrentCount = 0;

        this.registerActivityLifecycleCallbacks(new BaseApplication.ActivityLifecycleCallbacksImpl());
        //加载各个组件
        ModuleManager.getInstance().loadModules();

        mAppDelegateList = ModuleManager.getInstance().getAppDelegateList();
        for (IModuleApplicationDelegate delegate : mAppDelegateList) {
            delegate.onCreate(this);
        }
    }

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        //开启多分包
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        for (IModuleApplicationDelegate delegate : mAppDelegateList) {
            delegate.onTerminate();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        for (IModuleApplicationDelegate delegate : mAppDelegateList) {
            delegate.onConfigurationChanged(configuration);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        for (IModuleApplicationDelegate delegate : mAppDelegateList) {
            delegate.onLowMemory();
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        for (IModuleApplicationDelegate delegate : mAppDelegateList) {
            delegate.onTrimMemory(level);
        }
    }

    public Activity getCurrentActivity() {
        return this.mCurrentActivity != null ? (Activity)this.mCurrentActivity.get(): null;
    }

    public boolean isAppRunningBackground() {
        boolean result = false;
        if(this.mCurrentCount == 0) {
            result = true;
        }
        return result;
    }

    //Activity生命周期监测回调
    private class ActivityLifecycleCallbacksImpl implements ActivityLifecycleCallbacks {
        private ActivityLifecycleCallbacksImpl() {}

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            BaseApplication.this.mActivityList.add(0, activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

            if(BaseApplication.this.mCurrentCount == 0 && !BaseApplication.this.isCurrent) {
                BaseApplication.this.isCurrent = true;
                for (IModuleApplicationDelegate delegate : ModuleManager.getInstance().getAppDelegateList()) {
                    delegate.enterForeground();
                }
                Log.d("BaseApplication", "The App go to foreground");
            }
            BaseApplication.this.mCurrentCount++;
        }

        @Override
        public void onActivityResumed(Activity activity) {

            if(!BaseApplication.this.mResumeActivity.contains(activity)) {
                BaseApplication.this.mResumeActivity.add(activity);
            }
            BaseApplication.this.mCurrentActivity = new WeakReference(activity);
            //回调当前所在activity
            for (IModuleApplicationDelegate delegate : ModuleManager.getInstance().getAppDelegateList()) {
                delegate.onCurrentActivity(activity);
                delegate.onActivityTask(mActivityList);
            }
        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

            BaseApplication.this.mResumeActivity.remove(activity);
            BaseApplication.this.mCurrentCount--;
            if(BaseApplication.this.mCurrentCount == 0 && BaseApplication.this.isCurrent) {
                BaseApplication.this.isCurrent = false;
                for (IModuleApplicationDelegate delegate : ModuleManager.getInstance().getAppDelegateList()) {
                    delegate.enterBackground();
                }
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            BaseApplication.this.mActivityList.remove(activity);
        }
    }

}
