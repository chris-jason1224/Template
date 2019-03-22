package com.cj.business.auth.wechat;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.cj.business.Config;
import com.cj.business.auth.IAuth;
import com.cj.business.auth.lifecycle.AuthEventLifecycleObserver;
import com.cj.common.bus.DataBus;
import com.cj.common.bus.DataBusKey;
import com.cj.common.provider.fun$business.auth.AuthParams;
import com.cj.common.provider.fun$business.auth.IAuthResultCallback;
import com.cj.common.util.LooperUtil;
import com.cj.log.CJLog;
import com.cj.manager.basement.BaseApplication;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;

/**
 * Author:chris - jason
 * Date:2019/2/12.
 * Package:com.cj.business.auth.wechat
 */

public class WeChatAuth implements IAuth, LifecycleOwner {
    private LifecycleRegistry lifecycleRegistry;

    private IAuthResultCallback callback;

    private WeChatAuth() {
        lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        lifecycleRegistry.addObserver(new AuthEventLifecycleObserver());

        //注册DataBus接收器
        DataBus.get().with(DataBusKey.WeChatAuthResult.getKey(), DataBusKey.WeChatAuthResult.getT()).observe(this, observer);
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }

    private static class Holder {
        private static final WeChatAuth instance = new WeChatAuth();
    }

    public static WeChatAuth getInstance() {
        return Holder.instance;
    }


    @Override
    public void realAuth(AuthParams params, IAuthResultCallback callback) {

        this.callback = callback;

        if (TextUtils.isEmpty(Config.WECHAT_APP_ID)) {
            CJLog.getInstance().log_d("微信AppID为空!!!");
            return;
        }


        IWXAPI wxapi = WXAPIFactory.createWXAPI(BaseApplication.getInstance(), Config.WECHAT_APP_ID, true);
        if (wxapi == null) {
            CJLog.getInstance().log_d("WXAPI == null");
            return;
        }

        //监测是否安装微信app
        if (!wxapi.isWXAppInstalled()) {
            LooperUtil.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BaseApplication.getInstance(), "请先安装微信再使用微信登录", Toast.LENGTH_SHORT).show();
                }
            });

            return;
        }

        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";//授权域，用户获取个人信息
        req.state = "cj_wechat_sdk_auth_apply";//用于保持请求和回调的状态，防止csrf攻击
        wxapi.sendReq(req);

        //激活微信支付结果接收器
        //当观察者的生命周期处于STARTED或RESUMED状态时，LiveData会通知观察者数据变化；在观察者处于其他状态时，即使LiveData的数据变化了，也不会通知。
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);

    }

    /**
     * 微信授权结果接收器
     */
    private Observer<HashMap> observer = new Observer<HashMap>() {
        @Override
        public void onChanged(@Nullable HashMap map) {
            if (map != null && callback != null) {
                String auth_result = (String) map.get("auth_result");
                String code = (String) map.get("code");

                if (TextUtils.equals(auth_result, WeChatAuthResult.SUCCESS)) {
                    callback.onSuccess(code);
                } else if (TextUtils.equals(auth_result, WeChatAuthResult.CANCEL)) {
                    callback.onFailed();
                } else {
                    callback.onFailed();
                }

            }
            //暂时关闭数据接收
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        }
    };

}
