package com.cj.common.base;

/**
 * Created by mayikang on 2018/8/3.
 */

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Presenter层的基础约束
 * @param <V> View层泛型
 * @Link{https://blog.csdn.net/mq2553299/article/details/79029657}
 */
public interface IBasePresenter<V>  extends LifecycleObserver{

    public void attachView(V v);

    public void detachView();

    //View层执行onDestroy时会回调到此方法
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(@NonNull LifecycleOwner owner);

}
