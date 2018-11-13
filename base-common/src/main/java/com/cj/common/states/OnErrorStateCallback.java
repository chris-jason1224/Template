package com.cj.common.states;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.cj.common.R;
import com.cj.common.base.BaseActivity;
import com.kingja.loadsir.callback.Callback;

/**
 * Created by mayikang on 2018/7/26.
 */

public class OnErrorStateCallback extends Callback {
    @Override
    protected int onCreateView() {
        return R.layout.base_common_on_error_state_layout;
    }

    //重写点击事件
    /**
     *     当前Callback的点击事件，如果返回true则覆盖注册时的onReload()，
     *     如果返回false则两者都执行，先执行onReloadEvent()。
     */
    @Override
    protected boolean onReloadEvent(final Context context, View view) {
        return super.onReloadEvent(context,view);
    }

    //是否在显示Callback视图的时候显示原始图(SuccessView)，返回true显示，false隐藏
    @Override
    public boolean getSuccessVisible() {
        return false;
    }

    //将Callback添加到当前视图时的回调，View为当前Callback的布局View
    @Override
    public void onAttach(Context context, View view) {
        super.onAttach(context, view);
    }

    //将Callback从当前视图删除时的回调，View为当前Callback的布局View
    @Override
    public void onDetach() {
        super.onDetach();
    }
}
