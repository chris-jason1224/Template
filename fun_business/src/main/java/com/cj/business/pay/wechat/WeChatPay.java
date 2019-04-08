package com.cj.business.pay.wechat;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;
import com.cj.business.Config;
import com.cj.business.pay.lifecycle.PayEventLifecycleObserver;
import com.cj.business.pay.IPay;
import com.cj.common.bus.DataBus;
import com.cj.common.bus.DataBusKey;
import com.cj.common.util.JSONUtils;
import com.cj.common.provider.fun$business.pay.IPayResultCallback;
import com.cj.common.util.LooperUtil;
import com.cj.log.CJLog;
import com.cj.manager.basement.BaseApplication;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Author:chris - jason
 * Date:2019/2/1.
 * Package:com.cj.business.pay
 */

public class WeChatPay implements IPay,LifecycleOwner {

    private IPayResultCallback callback;
    private LifecycleRegistry lifecycleRegistry;

    private WeChatPay(){
        //手动实现LifeCycle生命周期管理
        lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        lifecycleRegistry.addObserver(new PayEventLifecycleObserver());

        //注册DataBus接收器
        DataBus.get().with(DataBusKey.WeChatPayResult.getKey(),DataBusKey.WeChatPayResult.getT()).observe(this,observer);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    private static class Holder{
        private static final WeChatPay instance = new WeChatPay();
    }

    public static WeChatPay getInstance(){
        return Holder.instance;
    }


    @Override
    public void realPay(@NonNull String prePayInfoStr, final IPayResultCallback callback) {

        this.callback = callback;

        if(TextUtils.isEmpty(Config.WECHAT_APP_ID)){
            CJLog.getInstance().log_d("微信AppID为空!!!");
            return;
        }

        if(TextUtils.isEmpty(prePayInfoStr)){
            CJLog.getInstance().log_e("微信预付单信息为空，支付失败");
            return;
        }

        IWXAPI wxapi = WXAPIFactory.createWXAPI(BaseApplication.getInstance(), Config.WECHAT_APP_ID, true);
        if(wxapi == null){
            CJLog.getInstance().log_d("WXAPI == null");
            return;
        }
        //监测是否安装微信app
        if(!wxapi.isWXAppInstalled()){
            LooperUtil.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BaseApplication.getInstance(), "请先安装微信再继续支付", Toast.LENGTH_SHORT).show();
                }
            });

            return;
        }

        //监测是否支持微信支付
        if (wxapi.getWXAppSupportAPI() < Build.PAY_SUPPORTED_SDK_INT) {
            LooperUtil.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BaseApplication.getInstance(),"当前微信版本过低，请升级微信后再继续支付",Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        wxapi.registerApp(Config.WECHAT_APP_ID);

        PayReq req = new PayReq();
        PrePayInfo prePayInfo = JSONUtils.jsonString2JavaObject(prePayInfoStr, com.cj.business.pay.wechat.PrePayInfo.class);

        if(prePayInfo!=null){
            req.appId = prePayInfo.getAppid();
            req.partnerId = prePayInfo.getPartnerid();
            req.prepayId = prePayInfo.getPrepayid();
            req.packageValue = prePayInfo.getPackageValue();
            req.nonceStr = prePayInfo.getNoncestr();
            req.timeStamp = prePayInfo.getTimestamp();
            req.sign = prePayInfo.getSign();

            // 发送支付请求：跳转到微信客户端
            wxapi.sendReq(req);

            //激活微信支付结果接收器
            //当观察者的生命周期处于STARTED或RESUMED状态时，LiveData会通知观察者数据变化；在观察者处于其他状态时，即使LiveData的数据变化了，也不会通知。
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);

        }

    }


    /**
     * 微信支付结果接收器
     */
    private Observer<String> observer = new Observer<String>() {
        @Override
        public void onChanged(@Nullable String msg) {
            if(!TextUtils.isEmpty(msg) && callback!=null){
                switch (msg){
                    case WeChatPayResult.SUCCESS:
                        callback.onSuccess();
                        break;
                    case WeChatPayResult.FAILED:
                        callback.onFailed();
                        break;
                    case WeChatPayResult.CANCEL:
                        callback.onCancel();
                        break;
                }
            }
            //暂时关闭数据接收
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        }
    };


}