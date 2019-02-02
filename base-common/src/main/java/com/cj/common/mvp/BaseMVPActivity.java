package com.cj.common.mvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cj.common.base.BaseActivity;
import com.cj.common.base.IBasePresenter;
import com.cj.common.base.IBaseView;
import com.cj.log.CJLog;

/**
 * Created by mayikang on 2018/8/3.
 */

//todo BaseMVP 架构层都需要绑定到生命周期上
public abstract class BaseMVPActivity<P extends IBasePresenter> extends BaseActivity implements IBaseView{

    protected P mPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //绑定Presenter
        if(mPresenter==null){
            mPresenter=createPresenter();
            mPresenter.attachView(this);
            //管理presenter层的生命周期
            getLifecycle().addObserver(mPresenter);
        }



    }

    protected abstract P createPresenter();


}
