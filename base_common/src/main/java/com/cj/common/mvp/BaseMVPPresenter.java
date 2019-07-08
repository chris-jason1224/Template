package com.cj.common.mvp;

import androidx.lifecycle.LifecycleOwner;
import androidx.annotation.NonNull;
import com.cj.common.base.IBasePresenter;
import com.cj.common.base.IBaseView;
import com.cj.common.http.repository.APIStore;
import com.cj.common.http.repository.RetrofitFactory;
import java.lang.ref.WeakReference;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mayikang on 2018/8/3.
 */

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


    private void detachView() {

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
