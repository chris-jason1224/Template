package com.cj.common.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.alibaba.android.arouter.launcher.ARouter;
import com.cj.common.R;

import com.cj.common.receiver.NetworkStateOBReceiver;
import com.cj.common.states.OnEmptyStateCallback;
import com.cj.common.states.OnPlaceHolderCallback;
import com.cj.common.states.OnTimeoutStateCallback;
import com.cj.common.states.StateEntity;
import com.cj.ui.tip.UITipDialog;
import com.gyf.barlibrary.ImmersionBar;
import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.callback.SuccessCallback;
import com.kingja.loadsir.core.Convertor;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;
import com.kingja.loadsir.core.Transport;
import com.orhanobut.logger.LogAdapter;

/**
 * Created by mayikang on 2018/7/24.
 * Activity最基础的基类
 */

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {

    private LoadService loadService;
    protected ImmersionBar immersionBar;
    protected View mContentView;

    private TextView mTVEmpty, mTVTimeOut;

    /**
     * 判断 activity 是否处于激活状态
     **/
    private boolean isActivityReady = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //为activity指定全屏的主题，隐藏掉actionbar
        setTheme(R.style.base_common_Theme);
        //加载布局
        mContentView = LayoutInflater.from(this).inflate(resourceLayout(), null);
        setContentView(mContentView);

        initView();

        //ARouter注入
        //ARouter注入服务，子类中可以直接使用 @Autowired注解来获得服务
        ARouter.getInstance().inject(this);

        //沉浸式处理
        if (useImmersionBar()) {
            initImmersionBar();
        }

        //多布局初始化
        initLoadSir(initStatusLayout());

        //注入网络变化监听回调
        NetworkStateOBReceiver.setOnNetworkChangedListener(new NetworkStateOBReceiver.OnNetworkChangedListener() {
            @Override
            public void onNetworkChanged(boolean isConnected, int type) {
                //每一个继承自BaseActivity的activity都会回调这个方法，避免重复提醒，需要判断当前activity是否在栈顶
                if (isActivityReady) {
                    //有网络
                    if (!isConnected) {
                        final UITipDialog tipDialog = new UITipDialog.Builder(BaseActivity.this)
                                .setIconType(UITipDialog.Builder.ICON_TYPE_INFO)
                                .setTipWord("网络断开")
                                .create();
                        tipDialog.show();
                    }
                }
            }
        });

        initData();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActivityReady = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActivityReady = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (immersionBar != null)
            immersionBar.destroy();
    }

    //子类该方法，传入布局文件
    protected abstract int resourceLayout();

    public View getContentView() {
        return mContentView;
    }

    //注册多状态缺省页面
    public abstract View initStatusLayout();

    //初始化LoadSir
    private void initLoadSir(View view) {

        loadService = LoadSir.getDefault().register(view == null ? this : view, new Callback.OnReloadListener() {

            @Override
            public void onReload(View v) {

            }
        }, new Convertor<StateEntity>() {
            @Override
            public Class<? extends Callback> map(StateEntity stateEntity) {
                //默认是success
                Class<? extends Callback> result = SuccessCallback.class;
                switch (stateEntity.getState()) {

                    //显示成功页面 --> 原始页面
                    case StateEntity.StateCode.SUCCESS_LAYOUT:
                        result = SuccessCallback.class;
                        break;

                    //显示空数据页面 --> OnEmptyStateCallback
                    case StateEntity.StateCode.EMPTY_LAYOUT:
                        result = OnEmptyStateCallback.class;

                        //修改文字
                        if (!TextUtils.isEmpty(stateEntity.getMessage())) {
                            if (mTVEmpty != null) {
                                mTVEmpty.setText(stateEntity.getMessage());
                            }
                        }

                        break;

                    //显示占位图页面 --> onPlaceHolderCallback
                    case StateEntity.StateCode.PLACEHOLDER_LAYOUT:
                        result = OnPlaceHolderCallback.class;
                        break;

                    //显示连接超时页面 --> onTimeoutStateLayout
                    case StateEntity.StateCode.TIMEOUT_LAYOUT:
                        result = OnTimeoutStateCallback.class;
                        if (!TextUtils.isEmpty(stateEntity.getMessage())) {
                            if (mTVTimeOut != null) {
                                mTVTimeOut.setText(stateEntity.getMessage());
                            }
                        }
                        break;
                }

                return result;
            }
        });

        //动态修改Callback
        loadService.setCallBack(OnEmptyStateCallback.class, new Transport() {
            @Override
            public void order(Context context, View view) {
                if (view != null) {
                    mTVEmpty = view.findViewById(R.id.tv_empty);
                }
            }
        });

        loadService.setCallBack(OnTimeoutStateCallback.class, new Transport() {
            @Override
            public void order(Context context, View view) {
                if (view != null) {
                    mTVTimeOut = view.findViewById(R.id.tv_timeout);
                }
            }
        });

    }

    @Deprecated
    public LoadService getLoadService() {
        return loadService;
    }

    //显示加载成功布局
    protected void showSuccessLayout() {
        if (loadService == null) {
            return;
        }
        loadService.showWithConvertor(new StateEntity(1));
    }

    //显示空数据页面
    protected void showEmptyLayout(@Nullable String message) {
        if (loadService == null) {
            return;
        }
        loadService.showWithConvertor(new StateEntity(2, message));
    }

    //显示PlaceHolder页面
    protected void showPlaceHolderLayout() {
        if (loadService == null) {
            return;
        }
        loadService.showWithConvertor(new StateEntity(3));
    }

    //显示连接超时页面数据
    protected void showTimeoutLayout(@Nullable String message) {
        if (loadService == null) {
            return;
        }
        loadService.showWithConvertor(new StateEntity(4, message));
    }

    //是否使用沉浸式状态栏，默认不使用
    protected boolean useImmersionBar() {
        return false;
    }

    //初始化沉浸式状态栏
    protected void initImmersionBar() {
        //@link https://github.com/gyf-dev/ImmersionBar
        immersionBar = ImmersionBar.with(this);
        immersionBar.
                transparentStatusBar().
                statusBarColor(R.color.base_common_color_fafafa).
                statusBarDarkFont(true).
                keyboardEnable(true).//解决软键盘与底部输入框冲突问题，默认为false，还有一个重载方法，可以指定软键盘mode
                navigationBarWithKitkatEnable(false).//是否可以修改安卓4.4和emui3.1手机导航栏颜色，默认为true

                init();
    }

    //子类请求数据入口
    protected abstract void initData();

    protected abstract void initView();

    //获取 actionBarHeight
    public int getActionBarHeight() {
        int actionbarHeight = 0;
        TypedValue typedValue = new TypedValue();

        if (getTheme().resolveAttribute(R.attr.actionBarSize, typedValue, true)) {
            actionbarHeight = TypedValue.complexToDimensionPixelSize(typedValue.data, getResources().getDisplayMetrics());
        }

        return actionbarHeight;
    }

    //获取沉浸式状态栏高度
    public int getStatusBarHeight() {
        int statusBarHeight = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = getResources().getDimensionPixelSize(resourceId);
        }

        return statusBarHeight;
    }

    //绑定控件
    protected <T extends View> T fb(@IdRes int resID) {
        return (T) findViewById(resID);
    }


}
