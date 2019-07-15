package com.cj.common.base;

/**
 * Created by mayikang on 2018/7/30.
 */

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.cj.common.R;
import com.cj.common.states.OnEmptyStateCallback;
import com.cj.common.states.OnPlaceHolderCallback;
import com.cj.common.states.OnTimeoutStateCallback;
import com.cj.common.states.StateEntity;
import com.kingja.loadsir.callback.Callback;
import com.kingja.loadsir.callback.SuccessCallback;
import com.kingja.loadsir.core.Convertor;
import com.kingja.loadsir.core.LoadService;
import com.kingja.loadsir.core.LoadSir;
import com.kingja.loadsir.core.Transport;

import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 封装的 Fragment 基类
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    private LoadService loadService;
    private TextView mTVEmpty, mTVTimeOut;
    private Unbinder unbinder;

    //是否懒加载
    private boolean isLazyLoad = true;
    /**
     * 宿主的activity
     **/
    protected FragmentActivity mActivity;

    /**
     * 根view
     **/
    protected View mRootView;

    /**
     * Frg是否对用户可见
     **/
    protected boolean mIsVisible;

    /**
     * 是否加载完成
     * 当执行完onCreateView方法后即为true，标识碎片加载完成
     */
    protected boolean mIsPrepare;


    /**
     * 碎片和 Activity 绑定
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        //Activity被回收后，Fragment不会被回收
        mActivity = getActivity();
    }

    /**
     * 加载碎片
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //1.加载布局 XML 文件
        mRootView = inflater.inflate(setLayoutResource(), container, false);
        //注册butterknife
        unbinder = ButterKnife.bind(this,mRootView);
        //注册loadSir
        loadService = LoadSir.getDefault().register(mRootView, new Callback.OnReloadListener() {

            @Override
            public void onReload(View v) {
                // 重新加载逻辑
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

        return loadService.getLoadLayout();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIsPrepare = true;
        isLazyLoad = setLazyLod();

        //绑定控件，初始化布局
        initView();

        //非懒加载模式，直接初始化数据
        if (!isLazyLoad) {
            initData();
        }

    }

    protected abstract void initView();

    protected abstract void initData();

    protected abstract boolean setLazyLod();

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        this.mIsVisible = isVisibleToUser;

        if (isVisibleToUser) {
            onVisibleToUser();
        }
    }

    /**
     * 用户可见时执行的操作
     */
    protected void onVisibleToUser() {
        //只有碎片加载完成和可见的时候，执行懒加载数据
        if (mIsPrepare && mIsVisible) {
            //只需要懒加载一次
            mIsPrepare = false;
            initData();
        }
    }

    /**
     * 设置根布局资源id
     *
     * @return
     */
    protected abstract int setLayoutResource();


    @SuppressWarnings("unchecked")
    protected <T extends View> T fb(int id) {
        if (mRootView == null) {
            return null;
        }

        return (T) mRootView.findViewById(id);
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(unbinder!=null){
            unbinder.unbind();
        }
    }
}
