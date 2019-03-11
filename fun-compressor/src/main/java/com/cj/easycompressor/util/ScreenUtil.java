package com.cj.easycompressor.util;

import android.content.Context;

/**
 * Created by mayikang on 2018/9/12.
 */

public class ScreenUtil {

    //获取屏幕宽度
    public static int getScreenWidth(Context context){
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context){
        return context.getResources().getDisplayMetrics().heightPixels;
    }




}
