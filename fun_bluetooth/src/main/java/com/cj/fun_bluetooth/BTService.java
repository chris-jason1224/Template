package com.cj.fun_bluetooth;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cj.common.provider.fun$bluetooth.BTStateObserver;
import com.cj.common.provider.fun$bluetooth.IBTProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:chris - jason
 * Date:2019/3/22.
 * Package:com.cj.fun_bluetooth
 * fun-bluetooth对外提供服务
 */
@Route(path = "/fun_bluetooth/SEV/com.cj.fun_bluetooth.BTService")
public class BTService implements IBTProvider {

    //BTStateObserver观察者集合
    private volatile List<BTStateObserver> observers;

    @Override
    public synchronized void registerBTStateObserver(BTStateObserver observer) {

        synchronized (BTService.this) {
            if (observer != null && !observers.contains(observer)) {
                observers.add(observer);
            }
        }

    }

    @Override
    public void scan() {
        BTCenter.getInstance().scan();
    }

    @Override
    public void connect(String address) {
        BTCenter.getInstance().connect(address);
    }

    @Override
    public void disConnect(String address) {
        //BTCenter.getInstance().disConnect(address);
    }

    @Override
    public void autoConnectTo() {
        BTCenter.getInstance().autoConnect();
    }

    @Override
    public void init(Context context) {
        observers = new ArrayList<>();
        BTCenter.init(observers);
    }


}
