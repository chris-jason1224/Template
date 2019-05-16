package com.cj.fun_lbs;

import android.content.Context;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.cj.common.provider.fun$lbs.ILBSProvider;
import com.cj.common.provider.fun$lbs.ILocateResultCallback;
import com.cj.fun_lbs.location.LocationCenter;

/**
 * Author:chris - jason
 * Date:2019-05-16.
 * Package:com.cj.fun_lbs
 * fun_lbs对外提供定位服务的服务
 */
@Route(path = "/fun_lbs/SEV/com.cj.fun_lbs.LBSService")
public class LBSService implements ILBSProvider {

    private Context context;

    @Override
    public void startLocate(ILocateResultCallback locateResultCallback) {
        //定位权限调用之前在外部判断
        LocationCenter.getInstance().startLocate(locateResultCallback);
    }

    @Override
    public void stopLocate() {
        LocationCenter.getInstance().stopLocate();
    }

    @Override
    public void init(Context context) {
        this.context = context;
        //this method must be invoked firstly
        LocationCenter.getInstance().register(context);
    }

}
