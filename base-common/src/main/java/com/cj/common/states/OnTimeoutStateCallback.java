package com.cj.common.states;

import com.cj.common.R;
import com.kingja.loadsir.callback.Callback;

/**
 * Created by mayikang on 2018/7/26.
 */

public class OnTimeoutStateCallback extends Callback {
    @Override
    protected int onCreateView() {
        return R.layout.base_common_on_timeout_state_layout;
    }
}
