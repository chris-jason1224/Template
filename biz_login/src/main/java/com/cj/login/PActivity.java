package com.cj.login;

import android.os.Bundle;
import android.view.View;
import com.cj.common.base.BaseActivity;
import com.cj.common.bus.DataBus;
import com.cj.common.bus.DataBusKey;

public class PActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int resourceLayout() {
        return R.layout.activity_p;
    }

    @Override
    public View initStatusLayout() {
        return null;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        fb(R.id.send3).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int vid = v.getId();
        if(vid == R.id.send3){
            DataBus.get().with(DataBusKey.ProcessMainReceiveDataEvent.getKey(),DataBusKey.ProcessMainReceiveDataEvent.getT()).setValue("xxxxxxxx");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
