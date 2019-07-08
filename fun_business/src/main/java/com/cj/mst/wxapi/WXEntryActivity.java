package com.cj.mst.wxapi;

import android.app.Activity;
import android.os.Bundle;

import com.cj.business.Config;
import com.cj.business.auth.wechat.WeChatAuthResult;
import com.cj.business.share.wechat.WeChatShareResult;
import com.cj.common.bus.DataBusKey;
import com.jeremyliao.liveeventbus.LiveEventBus;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.HashMap;


/**
 * Created by Chris-Jason on 2016/8/19.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = WXAPIFactory.createWXAPI(this, Config.WECHAT_APP_ID, false);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    //分享或申请授权，微信端回调结果到此
    @Override
    public void onResp(BaseResp baseResp) {

        if (baseResp == null) {
            finish();
            return;
        }

        //微信授权结果处理
        if (baseResp instanceof SendAuth.Resp) {
            dealAuthResult((SendAuth.Resp) baseResp);
            return;
        }

        //微信分享结果处理
        if (baseResp instanceof SendMessageToWX.Resp) {
            dealShareResult((SendMessageToWX.Resp) baseResp);
            return;
        }


    }

    /**
     * 处理微信授权结果
     *
     * @param resp
     */
    private void dealAuthResult(SendAuth.Resp resp) {

        HashMap<String,String> map = new HashMap<>();

        switch (resp.errCode) {

            //授权成功
            case BaseResp.ErrCode.ERR_OK:
                map.put("auth_result", WeChatAuthResult.SUCCESS);
                map.put("code",resp.code);
                LiveEventBus.get().with(DataBusKey.WeChatAuthResult.getKey(), DataBusKey.WeChatAuthResult.getT()).post(map);
                break;

            //取消授权
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                map.put("auth_result", WeChatAuthResult.CANCEL);
                map.put("code","");
                LiveEventBus.get().with(DataBusKey.WeChatAuthResult.getKey(), DataBusKey.WeChatAuthResult.getT()).post(map);
                break;

            //授权失败
            default:
                map.put("auth_result", WeChatAuthResult.FAILED);
                map.put("code","");
                LiveEventBus.get().with(DataBusKey.WeChatAuthResult.getKey(), DataBusKey.WeChatAuthResult.getT()).post(map);
                break;
        }

        finish();

    }

    /**
     * 处理微信分享结果
     *
     * @param resp
     */
    private void dealShareResult(SendMessageToWX.Resp resp) {

        switch (resp.errCode) {

            //分享成功
            //微信新规，分享成功或者失败，回调的结果都是 BaseResp.ErrCode.ERR_OK
            case BaseResp.ErrCode.ERR_OK:
                LiveEventBus.get().with(DataBusKey.WeChatShareResult.getKey(), DataBusKey.WeChatShareResult.getT()).post(WeChatShareResult.SUCCESS);
                break;

            //取消分享
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                LiveEventBus.get().with(DataBusKey.WeChatShareResult.getKey(), DataBusKey.WeChatShareResult.getT()).post(WeChatShareResult.CANCEL);
                break;

            //分享失败
            default:
                LiveEventBus.get().with(DataBusKey.WeChatShareResult.getKey(), DataBusKey.WeChatShareResult.getT()).post(WeChatShareResult.FAILED);
                break;
        }

        finish();

    }


}