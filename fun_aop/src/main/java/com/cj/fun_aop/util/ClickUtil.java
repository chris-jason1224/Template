package com.cj.fun_aop.util;

public class ClickUtil {


    private ClickUtil(){

    }

    private static class Holder{
        public static final ClickUtil instance = new ClickUtil();
    }

    public static ClickUtil getInstance(){
        return Holder.instance;
    }

    //上次点击时间
    private static volatile long lastClickTime = 0;

    public  boolean isFastClick(int interval) {

        long currentTime = System.currentTimeMillis();

        boolean isClick2;
        if (currentTime - lastClickTime > interval) {
            isClick2 = false;
        } else {
            isClick2 = true;
        }
        lastClickTime = currentTime;

        return isClick2;
    }
}
