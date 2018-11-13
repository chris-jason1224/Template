package com.cj.pay.alipay;

/**
 * Created by mayikang on 2018/9/30.
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.cj.log.CJLog;
import com.cj.pay.FuncPayApplicationDelegate;
import com.cj.pay.config.AlipayConfig;
import com.cj.ui.notify.Alerter.AlertManager;
import com.cj.ui.notify.Alerter.Alerter;
import com.cj.ui.notify.Alerter.AlerterListener;

import java.util.Map;

/**
 * 支付宝支付调用类
 */
public class AlipayUtil {

    private Context context;
    private Activity activity;

    /** 支付宝支付业务：入参app_id */
    private static  String APPID ;

    private static  String RSA2_PRIVATE;

    private static String RSA_PRIVATE;


    private static final int SDK_PAY_FLAG = 1;


    private AlipayUtil(){
        context= FuncPayApplicationDelegate.getInstance().getContext();
        APPID= AlipayConfig.APPID;
        RSA2_PRIVATE=AlipayConfig.RSA2_PRIVATE;
        RSA_PRIVATE=AlipayConfig.RSA_PRIVATE;
    }

    private static class Holder{
        private static final AlipayUtil instance=new AlipayUtil();
    }

    public AlipayUtil getInstance(Activity act){
        activity=act;
        return Holder.instance;
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为9000则代表支付成功

                    //支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {


                    } else {
                        //支付失败


                    }
                    break;
                }
                default:

                    break;
            }
        };
    };



    public void payV2() {

        //是否配置支付宝参数
        if ( TextUtils.isEmpty(APPID) || ( TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE)) ) {
            AlertManager.create(activity).setTitle("配置错误").setMessage("未配置支付宝 APPID 和 商户私钥").setAlertListener(new AlerterListener() {
                @Override
                public void onShow() {

                }

                @Override
                public void onHide() {

                }
            }).show();
            return;
        }



        /**
         * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
         * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
         * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
         *
         * orderInfo的获取必须来自服务端；
         */
        boolean rsa2 = (RSA2_PRIVATE.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(APPID, rsa2);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);

        String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        final String orderInfo = orderParam + "&" + sign;

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(activity);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                CJLog.getInstance().log_e("支付宝支付结果："+ result.toString());
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }






}
