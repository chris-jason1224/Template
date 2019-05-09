package com.cj.fun_push;

import android.content.Context;
import android.support.annotation.IntDef;

import com.cj.common.provider.fun$push.PushObserver;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

import static com.cj.fun_push.PushCenter.Platform.HUA_WEI;
import static com.cj.fun_push.PushCenter.Platform.JPUSH;
import static com.cj.fun_push.PushCenter.Platform.XIAO_MI;

/**
 * Author:chris - jason
 * Date:2019/4/15.
 * Package:com.cj.fun_push
 */
public class PushCenter {

    //各大推送平台的字典值
    @IntDef({JPUSH,HUA_WEI,XIAO_MI})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Platform {

        int JPUSH = 1;//极光推送

        int HUA_WEI = 2;//华为推送

        int XIAO_MI = 3;//小米推送

    }

    private static List<PushObserver> observerList;

    private PushCenter() {
        observerList = new ArrayList<>();
    }

    private static class Holder {
        private static final PushCenter instance = new PushCenter();
    }

    public static PushCenter getInstance() {
        return Holder.instance;
    }

    public void register(PushObserver observer) {
        if (!observerList.contains(observer)) {
            observerList.add(observer);
        }
    }

    public void dispatchPush(String object) {
        for (PushObserver observer : observerList) {
            observer.onPush(object);
        }
    }


    public void turnOff(Context context){
        JPushInterface.stopPush(context);
    }

    public void turnOn(Context context){
        if(JPushInterface.isPushStopped(context)){
            JPushInterface.resumePush(context);
        }
    }

}
