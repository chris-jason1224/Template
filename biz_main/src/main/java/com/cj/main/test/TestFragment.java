package com.cj.main.test;

import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;

import com.cj.common.base.BaseFragment;
import com.cj.common.states.OnTimeoutStateCallback;
import com.cj.main.R;

/**
 * Author:chris - jason
 * Date:2019/3/29.
 * Package:com.cj.main.test
 */
public class TestFragment extends BaseFragment {

    private LinearLayout mLL;

    @Override
    public View initStatusLayout() {
        return mLL;
    }

    @Override
    protected void initView() {
        mLL = fb(R.id.ll_bb);
    }

    @Override
    protected void initData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               showPlaceHolderLayout();
            }
        },2000);
    }

    @Override
    protected boolean setLazyLod() {
        return false;
    }

    @Override
    protected int setLayoutResource() {
        return R.layout.biz_main_test_fragment_layout;
    }

    @Override
    public void onClick(View v) {

    }
}
