package com.cj.fun_push;

import android.support.annotation.IntDef;

import com.cj.common.provider.fun$push.PushObserver;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import static com.cj.fun_push.PushCenter.Platform.JPUSH;

/**
 * Author:chris - jason
 * Date:2019/4/15.
 * Package:com.cj.fun_push
 */
public class PushCenter {

    //各大推送平台的字典值
    @IntDef({JPUSH})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Platform {

        int JPUSH = 1;//极光推送

        int HUA_WEI = 2;//华为推送

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

    private void dispatchPush(Object object) {
        for (PushObserver observer : observerList) {
            observer.onPush(object);
        }
    }


}
