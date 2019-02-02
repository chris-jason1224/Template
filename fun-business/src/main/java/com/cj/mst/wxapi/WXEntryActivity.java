package com.cj.mst.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.cj.business.Config;
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

                finish();
                break;

            case BaseResp.ErrCode.ERR_COMM:

                finish();
                break;
            //分享成功
            case BaseResp.ErrCode.ERR_OK:

                break;

            case BaseResp.ErrCode.ERR_SENT_FAILED:

                finish();
                break;

            case BaseResp.ErrCode.ERR_UNSUPPORT:

                finish();
                break;

            case BaseResp.ErrCode.ERR_USER_CANCEL:

                finish();
                break;

            default:

                finish();
                break;
        }


    }


}
