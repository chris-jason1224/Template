package com.cj.mst.wxapi;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.cj.business.Config;
import com.cj.business.pay.wechat.WeChatPayResult;
import com.cj.common.bus.ModuleBus;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import gen.com.cj.bus.Gen$fun_business$Interface;


public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    	api = WXAPIFactory.createWXAPI(this, Config.WECHAT_APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {

	}

	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

			//支付结果判定
			switch (resp.errCode){
				//成功
				case 0:
					ModuleBus.getInstance().of(Gen$fun_business$Interface.class).Gen$WeChatPayResult$Method().post(WeChatPayResult.SUCCESS);
					break;
				//错误
				case -1:
					ModuleBus.getInstance().of(Gen$fun_business$Interface.class).Gen$WeChatPayResult$Method().post(WeChatPayResult.FAILED);
					break;
				//取消
				case -2:
					ModuleBus.getInstance().of(Gen$fun_business$Interface.class).Gen$WeChatPayResult$Method().post(WeChatPayResult.CANCEL);
					break;
			}

		}

		finish();
	}
}