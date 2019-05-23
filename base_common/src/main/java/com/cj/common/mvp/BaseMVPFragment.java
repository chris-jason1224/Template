package com.cj.common.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.cj.common.base.BaseFragment;
import com.cj.common.base.IBasePresenter;
import com.cj.common.base.IBaseView;

/**
 * Created by mayikang on 2018/8/3.
 */

public  abstract class BaseMVPFragment<P extends IBasePresenter> extends BaseFragment implements IBaseView{

    private P mPresenter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        if(mPresenter==null){
            mPresenter=createPresenter();
            //管理presenter层生命周期
            getLifecycle().addObserver(mPresenter);
            mPresenter.attachView(this);
        }
        super.onCreate(savedInstanceState);
    }

    protected abstract P createPresenter();

}
