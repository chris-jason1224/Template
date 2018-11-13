package com.cj.common.util.pkg;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Chris-Jason on 2016/10/4.
 */
public class PackageUtil {

    private String TAG="PackageUtil";
    private static Context context;
    private static PackageUtil instance;

    public static PackageUtil getInstance(Context cont){
        if(instance==null){
            instance=new PackageUtil();
            context=cont;
        }
        return instance;
    }

    /**
     * 传入相应的包名，判断是否有安装
     * @param name
     * @return
     */
    public  boolean isXInstalled(String name){
        final PackageManager packageManager = context.getPackageManager();
        List<android.content.pm.PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals(name)) {
                    return true;
                }
            }
        }
        return false;

    }

    public void getAllPackage(){
        ArrayList<PackageInfo> appList = new ArrayList<PackageInfo>(); //用来存储获取的应用信息数据
        List<android.content.pm.PackageInfo> packages = context.getPackageManager().getInstalledPackages(0);

        for(int i=0;i<packages.size();i++) {
            android.content.pm.PackageInfo packageInfo = packages.get(i);
            PackageInfo tmpInfo =new PackageInfo();
            tmpInfo.appName = packageInfo.applicationInfo.loadLabel(context.getPackageManager()).toString();
            tmpInfo.packageName = packageInfo.packageName;
            tmpInfo.versionName = packageInfo.versionName;
            //非系统应用
            if((packageInfo.applicationInfo.flags& ApplicationInfo.FLAG_SYSTEM)==0)
            {
                appList.add(tmpInfo);//如果非系统应用，则添加至appList
                Log.e(TAG,"appName:"+tmpInfo.appName+"packageName:"+tmpInfo.packageName);
            }

        }


    }

}
