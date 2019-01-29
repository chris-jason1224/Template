package com.cj.manager.basement;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.cj.manager.manager.ModuleManager;
import com.cj.manager.module.interfaces.IModuleApplicationDelegate;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class BaseApplication extends Application {

    //application context 实例
    private static BaseApplication sInstance;

    //module代理类名称列表
    private List<IModuleApplicationDelegate> mAppDelegateList;

    private int mCurrentCount = 0;//onStart之后的activity数量

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

        /**
         * 多进程app会重复启动Application，只在主进程中执行一次即可
         */
        if (isCurrentMainProcess()) {


            this.mCurrentCount = 0;

            this.registerActivityLifecycleCallbacks(new BaseApplication.ActivityLifecycleCallbacksImpl());
            //加载各个组件
            ModuleManager.getInstance().loadModules();

            mAppDelegateList = ModuleManager.getInstance().getAppDelegateList();
            for (IModuleApplicationDelegate delegate : mAppDelegateList) {
                delegate.onCreate(this);
            }
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

    //获取当前activity
    public Activity getCurrentActivity() {
        return this.mCurrentActivity != null ? (Activity) this.mCurrentActivity.get() : null;
    }

    //获取整个任务栈
    public List<Activity> getActivityTaskStack(){
        return mActivityList;
    }

    //Activity生命周期监测回调
    private class ActivityLifecycleCallbacksImpl implements ActivityLifecycleCallbacks {
        private ActivityLifecycleCallbacksImpl() {
        }

        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            mActivityList.add(0, activity);
        }

        //app第一次进入前台
        @Override
        public void onActivityStarted(Activity activity) {

            if (mCurrentCount == 0 && !isCurrent) {
                isCurrent = true;
                for (IModuleApplicationDelegate delegate : ModuleManager.getInstance().getAppDelegateList()) {
                    delegate.enterForeground();
                }
                Log.d("BaseApplication", "The App go to foreground");
            }
            mCurrentCount++;
        }

        @Override
        public void onActivityResumed(Activity activity) {

            if (!mResumeActivity.contains(activity)) {
                mResumeActivity.add(activity);
            }
            mCurrentActivity = new WeakReference(activity);

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

            if(mResumeActivity.contains(activity)){
                mResumeActivity.remove(activity);
                mCurrentCount--;
            }

            if (mCurrentCount == 0 && isCurrent) {
                isCurrent = false;
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

            if(mActivityList.contains(activity)){
                mActivityList.remove(activity);
            }

        }
    }

    //判断当前进程是否是主进程
    private boolean isCurrentMainProcess() {

        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            //获取运行的进程列表
            List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfoList = manager.getRunningAppProcesses();

            if (runningAppProcessInfoList != null && runningAppProcessInfoList.size() > 0) {

                for (ActivityManager.RunningAppProcessInfo info : runningAppProcessInfoList) {
                    //当前进程 id = 运行进程 id，默认主进程名等于包名
                    if (info.pid == android.os.Process.myPid()) {
                        if (TextUtils.equals(info.processName, getPackageName())) {
                            return true;
                        }
                    }
                }

            }
        }

        return false;
    }

}
