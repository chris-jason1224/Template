package com.cj.main.test;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;

import com.cj.common.mvp.BaseMVPActivity;

import com.cj.main.R;
import com.cj.main.R2;
import com.cj.ui.tip.UITipDialog;
import butterknife.OnClick;

@Route(path = "/biz_main/test")
public class TestActivity extends BaseMVPActivity<ITestPresenter> implements ITestView {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getLoadService().showSuccess();
            }
        },1500);

    }

    @Override
    protected int resourceLayout() {
        return R.layout.biz_main_activity_test;
    }


    @Override
    public void onReloadClick() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected ITestPresenter createPresenter() {
        return new TestPresenter();
    }



    @OnClick({R2.id.http,R2.id.pop_tip})
    public void onClick(View v){
        int id=v.getId();

        if(R.id.http==id){
            mPresenter.doTest("123");
        }

        if(R.id.pop_tip==id){

            final UITipDialog tipDialog = new UITipDialog.Builder(this)
                    .setIconType(UITipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord("发送失败")
                    .create();
            tipDialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tipDialog.dismiss();
                }
            },1500);
        }





    }








}
