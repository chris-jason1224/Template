package com.cj.common.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;


import com.alibaba.android.arouter.launcher.ARouter;
import com.cj.common.R;

import com.cj.common.receiver.NetworkStateOBReceiver;
import com.cj.ui.tip.UITipDialog;
import com.gyf.barlibrary.ImmersionBar;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by mayikang on 2018/7/24.
 * Activity最基础的基类
 */

public abstract class BaseActivity extends AppCompatActivity {

    private Unbinder unbinder;
    private LoadService loadService;
    protected ImmersionBar immersionBar;
    protected View mContentView;


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

        //注册Butterknife
        unbinder = ButterKnife.bind(this);

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
        //解绑butterKnife
        if (unbinder != null) {
            unbinder.unbind();
        }

        if (immersionBar != null)
            immersionBar.destroy();

    }


    //子类该方法，传入布局文件
    protected abstract int resourceLayout();

    public View getMContentView() {
        return mContentView;
    }


    public LoadService getLoadService() {
        return loadService;
    }


    //注册多状态缺省页面
    public abstract View initStatusLayout();

    private void initLoadSir(View view) {

        //注册loadSir
        loadService = LoadSir.getDefault().register(view == null ? this : view);

    }


    //todo 权限申请的工作,考虑之后还是决定放到具体的页面来申请，毕竟大部分的页面不需要这项工作，所以不放在基类里面了

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


}
