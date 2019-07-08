package com.cj.business.pay;

import androidx.annotation.IntDef;

import com.cj.business.pay.alipay.AliPay;
import com.cj.business.pay.wechat.WeChatPay;
import com.cj.common.provider.fun$business.pay.IPayResultCallback;
import com.cj.common.provider.fun$business.pay.PayParams;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.cj.business.pay.PayCenter.PayWay.ALIPAY;
import static com.cj.business.pay.PayCenter.PayWay.UNION;
import static com.cj.business.pay.PayCenter.PayWay.WECHAT;

/**
 * Author:chris - jason
 * Date:2019/2/1.
 * Package:com.cj.business.pay
 * 支付渠道分发中心
 */

public class PayCenter {

    //支付方式字典值
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({ALIPAY,WECHAT,UNION})
    public @interface PayWay{
        int ALIPAY = 1;//支付宝
        int WECHAT = 2;//微信
        int UNION = 3;//银联
    }

    private PayCenter(){

    }

    private static class Holder{
        private static final PayCenter instance = new PayCenter();
    }

    public static PayCenter getInstance(){
        return Holder.instance;
    }



    //发起支付
    public void doPay(PayParams params, IPayResultCallback callback){

        if(params==null){
            return;
        }

        //分发支付渠道
        switch (params.getPayWay()){

            //调用支付宝支付
            case ALIPAY:
                AliPay.getInstance().realPay((String)params.getExtra(),callback);
                break;

            //调用微信支付
            case PayWay.WECHAT:
                WeChatPay.getInstance().realPay((String)params.getExtra(),callback);
                break;

            //调用银联支付
            case PayWay.UNION:

                break;

                default:
                    break;
        }


    }


}