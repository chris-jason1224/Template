package com.cj.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cj.common.base.BaseApp;
import com.cj.common.provider.fun$bluetooth.BTStateObserver;
import com.cj.common.provider.fun$bluetooth.IBTProvider;
import com.cj.ui.notify.Alerter.AlertManager;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Author:chris - jason
 * Date:2019/3/22.
 * Package:com.cj.bluetooth
 * fun-bluetooth对外提供服务
 */
@Route(path = "/fun_bluetooth/SEV/com.cj.bluetooth.BTService")
public class BTService implements IBTProvider {

    private Context context;
    private final int REQUESTCODE_LOCATION_PERMISSION =1;

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


    //发起扫描
    @Override
    public void scan() {


        Activity activity = BaseApp.getInstance().getCurrentActivity();


        //判断设备是否支持蓝牙
        if(!BTCenter.getInstance().isSupportBT()){
            AlertManager.create(activity).setTitle("").setMessage("当前设备不支持蓝牙功能").setAutoCollapse(true).show();
            return;
        }



        //定位权限集合
        String[] permissions_location = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        //1.扫描附近蓝牙需要动态授予蓝牙权限，关于权限的申请和授予之后处理逻辑，放在具体页面，这里只做判断
        if (!EasyPermissions.hasPermissions(context,permissions_location)) {

            if (activity != null) {

                AlertManager.create(activity).setTitle("").setMessage("请先开启定位权限，才能扫描附近可用蓝牙").setAutoCollapse(true).show();

            }

            return;
        }




        //调用开启蓝牙
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
    public int getBTState() {
        return BTCenter.getInstance().getBTState();
    }

    @Override
    public void init(Context context) {
        this.context = context;
        observers = new ArrayList<>();
        BTCenter.init(observers);
    }


    private Consumer consumer = new Consumer() {
        @Override
        public void accept(Object o) throws Exception {

        }
    };





}
