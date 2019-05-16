package com.cj.common.provider.fun$lbs;
import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * Author:chris - jason
 * Date:2019-05-15.
 * Package:com.cj.common.provider.fun$lbs
 */
public interface ILBSProvider extends IProvider {

    //开始定位
    void startLocate(ILocateResultCallback locateResultCallback);

    //停止定位
    void stopLocate();

}
