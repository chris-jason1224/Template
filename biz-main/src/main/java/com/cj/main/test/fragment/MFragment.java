package com.cj.main.test.fragment;

import com.cj.common.mvp.BaseMVPFragment;
import com.cj.log.CJLog;
import com.cj.main.R;

/**
 * Author:chris - jason
 * Date:2019/2/28.
 * Package:com.cj.main.test.fragment
 */

public class MFragment extends BaseMVPFragment<IFragmentPresenter> implements IFragmentView {
    @Override
    protected IFragmentPresenter createPresenter() {
        return new FragmentPresenter();
    }

    @Override
    protected void initData() {

    }

    @Override
    protected boolean setLazyLod() {
        return false;
    }

    @Override
    protected int setLayoutResource() {
        return R.layout.base_common_normal_title_layout;
    }

    @Override
    public void onResume() {
        super.onResume();
        CJLog.getInstance().log_e("啦啦啦");
    }
}
