package com.cj.common.provider.fun$lbs;

import com.cj.common.provider.fun$lbs.locate.LocationInfoEntity;

/**
 * Author:chris - jason
 * Date:2019-05-16.
 * Package:com.cj.common.provider.fun$lbs
 * 定位回调接口
 */
public interface ILocateResultCallback {
    void onLocation(LocationInfoEntity info);
}
