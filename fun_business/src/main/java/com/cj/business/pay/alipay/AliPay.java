package com.cj.business.pay.alipay;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.cj.business.pay.IPay;
import com.cj.common.provider.fun$business.pay.IPayResultCallback;
import com.cj.common.util.LooperUtil;
import com.cj.common.util.pkg.AndroidSystemUtil;
import com.cj.log.CJLog;
import com.cj.manager.basement.BaseApplication;

import java.util.Map;

/**
 * Author:chris - jason
 * Date:2019/2/2.
 * Package:com.cj.business.pay.alipay
 */

public class AliPay implements IPay {

    private IPayResultCallback callback;

    private static final int SDK_PAY_FLAG = 1;

    private AliPay() {

    }

    private static class Holder {
        private static final AliPay instance = new AliPay();
    }

    public static AliPay getInstance() {
        return Holder.instance;
    }


    /**
     * @param prePayInfo 支付宝签名串
     * @param callback
     */
    @Override
    public void realPay(final String prePayInfo, IPayResultCallback callback) {

        this.callback = callback;

        if (TextUtils.isEmpty(prePayInfo)) {
            CJLog.getInstance().log_e("支付宝支付签名信息为空");
            return;
        }

        if (!AndroidSystemUtil.getInstance(BaseApplication.getInstance()).isXInstalled("com.eg.android.AlipayGphone")) {
            LooperUtil.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BaseApplication.getInstance(), "请先安装支付宝再继续支付", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        final Activity activity = BaseApplication.getInstance().getCurrentActivity();

        if (activity != null) {
            Runnable payRunnable = new Runnable() {

                @Override
                public void run() {
                    PayTask tast = new PayTask(activity);
                    Map<String, String> result = tast.payV2(prePayInfo, true);
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


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    AliPayResult payResult = new AliPayResult((Map<String, String>) msg.obj);
                    /**
                     对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();

                    if (TextUtils.isEmpty(resultStatus)) {
                        return;
                    }

                    if (callback == null) {
                        return;
                    }

                    switch (resultStatus) {
                        /**
                         9000	订单支付成功
                         8000	正在处理中，支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                         4000	订单支付失败
                         5000	重复请求
                         6001	用户中途取消
                         6002	网络连接出错
                         6004	支付结果未知（有可能已经支付成功），请查询商户订单列表中订单的支付状态
                         其它 其它支付错误
                         */

                        case "9000":
                            callback.onSuccess();
                            break;

                        case "4000":
                            callback.onFailed();
                            break;

                        case "6001":
                            callback.onFailed();
                            break;

                        default:
                            callback.onFailed();
                            break;
                    }


                    break;
                }

                default:
                    break;
            }
        }

        ;
    };

}