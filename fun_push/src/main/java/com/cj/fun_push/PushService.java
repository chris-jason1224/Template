package com.cj.fun_push;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cj.common.provider.fun$push.IPushProvider;
import com.cj.common.provider.fun$push.PushObserver;

/**
 * Author:chris - jason
 * Date:2019/4/15.
 * Package:com.cj.fun_push
 * fun_push对外提供服务的类
 */

@Route(path = "/fun_push/SEV/com.cj.fun_push.PushService")
public class PushService implements IPushProvider {

    @Override
    public void registerPushObserver(PushObserver observer) {
        synchronized (PushService.class){
            if(observer!=null){
                PushCenter.getInstance().register(observer);
            }
        }
    }

    @Override
    public void turnOn() {

    }

    @Override
    public void turnOff() {

    }

    @Override
    public void init(Context context) {

    }

}
