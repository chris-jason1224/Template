package com.cj.fun_lbs.location;

import android.content.Context;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.cj.common.exception.NotInitException;
import com.cj.common.provider.fun$lbs.ILocateResultCallback;
import com.cj.common.provider.fun$lbs.LocationInfoEntity;
import com.cj.log.CJLog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Author:chris - jason
 * Date:2019-05-16.
 * Package:com.cj.fun_lbs.location
 * 处理定位逻辑的工具类
 */
public class LocationCenter {

    private static Context context;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private List<ILocateResultCallback> callbackList;


    private LocationCenter() {
    }

    private static class Holder {
        private static final LocationCenter instance = new LocationCenter();
    }

    public static LocationCenter getInstance() {
        return Holder.instance;
    }

    public void register(Context con) {
        if (context == null) {
            context = con;
        }
        callbackList = new ArrayList<>();
        initLocation();
    }

    //初始化定位参数
    private void initLocation() {

        if (context == null) {
            throw new NotInitException(LocationCenter.class);
        }

        mLocationClient = new AMapLocationClient(context);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setNeedAddress(true);
        //设置定位监听
        mLocationClient.setLocationListener(aMapLocationListener);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置只定位一次
        mLocationOption.setOnceLocation(true);
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setMockEnable(false);
        mLocationOption.setWifiScan(true);
        mLocationOption.setGpsFirst(false);
        //设置定位场景倾向于签到场景，单次定位即可
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn);
        //设置定位间隔,单位毫秒,默认为1000ms
        mLocationOption.setInterval(1000);
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    private AMapLocationListener aMapLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {

            if (aMapLocation == null) {
                dispatchLocationInfo(null);
                return;
            }

            if (aMapLocation.getErrorCode() != 0) {
                dispatchLocationInfo(null);
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                CJLog.getInstance().log_e("定位错误码：" + aMapLocation.getErrorCode());
                CJLog.getInstance().log_e("定位错误信息：" + aMapLocation.getErrorInfo());
                return;
            }

            /**定位成功**/
            LocationInfoEntity entity = new LocationInfoEntity();
            entity.setLocationType(aMapLocation.getLocationType());
            entity.setLatitude(aMapLocation.getLatitude());
            entity.setLongitude(aMapLocation.getLongitude());
            entity.setAccuracy(aMapLocation.getAccuracy());
            entity.setAddress(aMapLocation.getAddress());
            entity.setCountry(aMapLocation.getCountry());
            entity.setProvince(aMapLocation.getProvince());
            entity.setCity(aMapLocation.getCity());
            entity.setDistrict(aMapLocation.getDistrict());
            entity.setStreet(aMapLocation.getStreet());
            entity.setStreetNum(aMapLocation.getStreetNum());//街道门牌号信息
            entity.setCityCode(aMapLocation.getCityCode());//城市编码
            entity.setAdCode(aMapLocation.getAdCode());//地区编码
            entity.setAoiName(aMapLocation.getAoiName());//获取当前定位点的AOI信息
            entity.setBuildingId(aMapLocation.getBuildingId());//获取当前室内定位的建筑物Id
            entity.setFloor(aMapLocation.getFloor());//获取当前室内定位的楼层
            entity.setGpsAccuracyStatus(aMapLocation.getGpsAccuracyStatus());//获取GPS的当前状态
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//获取定位时间
            Date date = new Date(aMapLocation.getTime());
            entity.setDate(df.format(date));

            dispatchLocationInfo(entity);

        }
    };

    private void dispatchLocationInfo(LocationInfoEntity entity) {
        for (ILocateResultCallback callback : callbackList) {
            callback.onLocation(entity);
        }
    }


    //开始定位
    public synchronized void startLocate(ILocateResultCallback locateResultCallback) {
        if (!callbackList.contains(locateResultCallback)) {
            callbackList.add(locateResultCallback);
        }
        mLocationClient.startLocation();
    }

    //停止定位
    public void stopLocate() {
        if (mLocationClient.isStarted()) {
            mLocationClient.stopLocation();
        }
    }

}
