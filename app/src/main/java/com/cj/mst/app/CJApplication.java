package com.cj.mst.app;
import android.os.Debug;

import com.cj.common.base.BaseApp;
import com.squareup.leakcanary.ActivityRefWatcher;
import com.squareup.leakcanary.AndroidRefWatcherBuilder;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;


/**
 * Created by mayikang on 2018/7/22.
 */
public class CJApplication extends BaseApp {
    private RefWatcher refWatcher;
    @Override
    public void onCreate() {
        super.onCreate();
        refWatcher = LeakCanary.install(this);
    }

}
