package com.cj.mst.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.cj.business.Config;
import com.cj.business.pay.wechat.WeChatPayResult;
import com.cj.common.bus.DataBus;
import com.cj.common.bus.DataBusKey;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;



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
					DataBus.get().with(DataBusKey.WeChatPayResult.getKey(),DataBusKey.WeChatPayResult.getT()).setValue(WeChatPayResult.SUCCESS);
					break;
				//错误
				case -1:
					DataBus.get().with(DataBusKey.WeChatPayResult.getKey(),DataBusKey.WeChatPayResult.getT()).setValue(WeChatPayResult.FAILED);
					break;
				//取消
				case -2:
					DataBus.get().with(DataBusKey.WeChatPayResult.getKey(),DataBusKey.WeChatPayResult.getT()).setValue(WeChatPayResult.CANCEL);
					break;
			}

		}

		finish();
	}
}