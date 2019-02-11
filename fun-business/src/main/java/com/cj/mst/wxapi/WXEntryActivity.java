package com.cj.mst.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.cj.business.Config;
import com.cj.business.pay.wechat.WeChatPayResult;
import com.cj.business.share.wechat.WeChatShare;
import com.cj.business.share.wechat.WeChatShareResult;
import com.cj.common.bus.DataBus;
import com.cj.common.bus.DataBusKey;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;


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

        switch (baseResp.errCode) {

            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                break;

            case BaseResp.ErrCode.ERR_COMM:
                break;

            //分享成功
            //微信新规，分享成功或者失败，回调的结果都是 BaseResp.ErrCode.ERR_OK
            case BaseResp.ErrCode.ERR_OK:
                DataBus.get().with(DataBusKey.WeChatShareResult.getKey(),DataBusKey.WeChatShareResult.getT()).setValue(WeChatShareResult.SUCCESS);
                break;

            case BaseResp.ErrCode.ERR_SENT_FAILED:
                break;

            case BaseResp.ErrCode.ERR_UNSUPPORT:
                break;

            //取消分享
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                DataBus.get().with(DataBusKey.WeChatShareResult.getKey(),DataBusKey.WeChatShareResult.getT()).setValue(WeChatShareResult.CANCEL);
                break;

            default:

                break;
        }

        finish();


    }


}