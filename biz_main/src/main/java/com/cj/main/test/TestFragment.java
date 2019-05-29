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


    @Override
    protected void initView() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showTimeoutLayout("ahahah");
            }
        },2000);
    }

    @Override
    protected void initData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               showTimeoutLayout("暂时没有数据啦啦啦啦~");
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
