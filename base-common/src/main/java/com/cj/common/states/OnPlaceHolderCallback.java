package com.cj.common.states;

import com.cj.common.R;
import com.kingja.loadsir.callback.Callback;

/**
 * Created by mayikang on 2018/7/26.
 */
//place holder 页面
public class OnPlaceHolderCallback extends Callback {
    @Override
    protected int onCreateView() {
        return R.layout.base_common_on_place_holder_state_layout;
    }
}
