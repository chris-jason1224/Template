package com.cj.common.provider.fun$push;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * Author:chris - jason
 * Date:2019/4/15.
 * Package:com.cj.common.provider.fun$push
 * fun_push对外提供服务的约束接口
 */
public interface IPushProvider extends IProvider {

    //注册推送消息回调
    void registerPushObserver(PushObserver observer);

    //打开推送
    void turnOn();

    //关闭推送
    void turnOff();

}
