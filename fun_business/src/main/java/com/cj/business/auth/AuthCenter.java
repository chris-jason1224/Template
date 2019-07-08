package com.cj.business.auth;

import androidx.annotation.IntDef;

import com.cj.business.auth.wechat.WeChatAuth;
import com.cj.common.provider.fun$business.auth.AuthParams;
import com.cj.common.provider.fun$business.auth.IAuthResultCallback;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.cj.business.auth.AuthCenter.AuthPlatform.AliPay;
import static com.cj.business.auth.AuthCenter.AuthPlatform.QQ;
import static com.cj.business.auth.AuthCenter.AuthPlatform.SinaWeiBo;
import static com.cj.business.auth.AuthCenter.AuthPlatform.WeChat;

/**
 * Author:chris - jason
 * Date:2019/2/12.
 * Package:com.cj.business.auth
 */

public class AuthCenter {

    //授权平台字典值
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({WeChat,QQ,SinaWeiBo,AliPay})
    public @interface AuthPlatform{

        int WeChat = 1;//微信授权

        int QQ = 2;//QQ授权

        int SinaWeiBo = 3;//新浪微博授权

        int AliPay = 4;//支付宝授权
    }


    private AuthCenter(){

    }

    private static class Holder{
        private static final AuthCenter instance = new AuthCenter();
    }

    public static AuthCenter getInstance(){
        return Holder.instance;
    }

    public void doAuth(AuthParams params , IAuthResultCallback callback){

        if(params == null){
            return;
        }

        //分发授权平台
        switch(params.getPlatform()){

            case AuthPlatform.WeChat:
                WeChatAuth.getInstance().realAuth(params,callback);
                break;

            case AuthPlatform.QQ:
                break;

            case AuthPlatform.SinaWeiBo:
                break;

            case AuthPlatform.AliPay:
                break;
        }


    }

}
