package com.cj.mst.app;


import com.alibaba.android.arouter.launcher.ARouter;
import com.cj.common.base.BaseApp;
import com.cj.common.BuildConfig;

/**
 * Created by mayikang on 2018/7/22.
 */
public class CJApplication extends BaseApp {
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {   // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(this);
    }
}
