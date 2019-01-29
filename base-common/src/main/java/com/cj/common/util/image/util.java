package com.cj.common.util.image;

import android.text.TextUtils;


/**
 * Author:chris - jason
 * Date:2019/1/29.
 * Package:com.cj.common.util.image
 */

public class util {

    public static boolean verify(String url){

        if(TextUtils.isEmpty(url)){
            return false;
        }

        //只支持http和https的图片地址
        if(!url.startsWith("http")){
            return false;
        }


        return true;
    }

}
