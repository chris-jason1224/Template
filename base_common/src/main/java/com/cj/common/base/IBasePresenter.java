package com.cj.common.base;

/**
 * Created by mayikang on 2018/8/3.
 */


import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * Presenter层的基础约束
 * @param <V> View层泛型
 * @Link{https://blog.csdn.net/mq2553299/article/details/79029657}
 */
public interface IBasePresenter<V>  extends LifecycleObserver {

     void attachView(V v);

    //View层执行onDestroy时会回调到此方法
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy(@NonNull LifecycleOwner owner);

}
