package com.cj.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cj.bluetooth.util.BluetoothService;
import com.cj.common.base.BaseApp;
import com.cj.common.provider.fun$bluetooth.BTStateObserver;
import com.cj.common.provider.fun$bluetooth.IBTProvider;
import com.cj.ui.notify.Alerter.AlertManager;

import java.io.UnsupportedEncodingException;
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
 * BTService对象非单例
 */
@Route(path = "/fun_bluetooth/SEV/com.cj.bluetooth.BTService")
public class BTService implements IBTProvider {

    private Context context;

    //注册回调接口，方便页面接收 蓝牙状态变化
    @Override
    public synchronized void registerBTStateObserver(BTStateObserver observer) {
        synchronized (BTService.this) {
            if (observer != null) {
                BTCenter.getInstance().register(observer);
            }
        }
    }

    //发起扫描
    @Override
    public void scan() {

        Activity activity = BaseApp.getInstance().getCurrentActivity();

        //判断设备是否支持蓝牙
        if (!BTCenter.getInstance().isSupportBT()) {
            AlertManager.create(activity).setTitle("").setMessage("当前设备不支持蓝牙功能").setAutoCollapse(true).show();
            return;
        }


        //定位权限集合
        String[] permissions_location = new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        //1.扫描附近蓝牙需要动态授予蓝牙权限，关于权限的申请和授予之后处理逻辑，放在具体页面，这里只做判断
        if (!EasyPermissions.hasPermissions(context, permissions_location)) {

            if (activity != null) {
                AlertManager.create(activity).setTitle("").setMessage("请先开启定位权限，才能扫描附近可用蓝牙").setAutoCollapse(true).show();
            }
            return;
        }

        //调用开启蓝牙
        BTCenter.getInstance().scan();
    }

    //连接指定的远程设备
    @Override
    public void connect(String address) {
        BTCenter.getInstance().connect(address);
    }

    //断开连接
    @Override
    public void disConnect(String address) {
        //BTCenter.getInstance().disConnect(address);
    }

    //自动连接上次设备
    @Override
    public void autoConnectTo() {
        BTCenter.getInstance().autoConnect();
    }

    //获取当前蓝牙连接状态
    @Override
    public int getBTState() {
        return BTCenter.getInstance().getBTState();
    }

    @Override
    public void init(Context context) {
        this.context = context;
    }

    /**
     * 打印具体信息
     */
    @Override
    public void printMessage(@NonNull String message) {
        BTCenter.getInstance().print(message);
    }

    /******打印格式控制****/
    @Override
    public void printLeft(){
        BTCenter.getInstance().prinLeft();
    }

    @Override
    public void printRight(){
        BTCenter.getInstance().prinRight();
    }

    @Override
    public void printCenter(){
        BTCenter.getInstance().printCenter();
    }

    @Override
    public void printSize(int size){
        BTCenter.getInstance().printSize(size);
    }

}
