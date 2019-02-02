package com.cj.common.mvp;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.NonNull;

import com.cj.common.base.IBasePresenter;
import com.cj.common.base.IBaseView;
import com.cj.common.http.repository.APIStore;
import com.cj.common.http.repository.RetrofitFactory;
import com.cj.log.CJLog;

import java.lang.ref.WeakReference;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mayikang on 2018/8/3.
 */

    //todo P需要和V绑定生命周期
    //todo 定义统一的网络数据返回模型
    //todo 定义线程切换控制器
    //todo 定义统一的网络请求观察者模型

public class BaseMVPPresenter<V extends IBaseView> implements IBasePresenter<V> {

    protected V mView;
    protected APIStore mAPIStore;

    WeakReference<V> reference;//弱引用持有view层，避免内存泄漏

    protected CompositeDisposable mDisposable=new CompositeDisposable();//RxJava订阅管理，避免内存泄漏

    @Override
    public void attachView(V v) {
        reference=new WeakReference<V>(v);
        mView=reference.get();
        mAPIStore= RetrofitFactory.getInstance().createApi(APIStore.class);
    }

    @Override
    public void detachView() {

        if(reference!=null){
            reference.clear();
            reference=null;
        }

        //解除presenter中所有的Observable订阅关系
        if(mDisposable!=null){
            mDisposable.clear();
        }

        mView=null;

    }


    //当View层onDestroy时，销毁presenter
    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        this.detachView();
    }


}
