package com.cj.common.states;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.View;

import com.cj.common.R;
import com.kingja.loadsir.callback.Callback;

/**
 * Created by mayikang on 2018/7/26.
 */

//服务器连接超时，网络错误 缺省页面
public class OnTimeoutStateCallback extends Callback {

    @Override
    protected int onCreateView() {
        return R.layout.base_common_on_timeout_state_layout;
    }

    @Override
    protected boolean onReloadEvent(final Context context, View view) {

        if(view!=null){
            view.findViewById(R.id.base_common_ll_timeout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //直接进入手机中设置界面
                    context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            });
        }

        return super.onReloadEvent(context, view);
    }
}
