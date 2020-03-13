package com.cj.common.util.async;

import androidx.annotation.NonNull;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Author:chris - jason
 * Date:2019/4/8.
 * Package:com.cj.common.util.async
 * 用于执行异步任务的管理中心，尽量不要直接使用野线程，通过该类来执行异步任务，自动切换线程
 */

/**
 * 1、java.util.concurrent.ExecutorService 是最底层的接口
 * 2、java.util.concurrent.ThreadPoolExecutor 实现了ExecutorService，是最基础的线程池
 * 3、预设的四种线程池，FixedThreadPool、CachedThreadPool、ScheduledThreadPool、SingleThreadExecutor四种线程池是对ThreadPoolExecutor的封装
 */
public class AsyncCenter {

    //线程池
    private ScheduledExecutorService executor;

    private AsyncCenter() {
        //初始化一个最大核心线程数为5的线程池
        //它有数量固定的核心线程，且有数量无限多的非核心线程，但是它的非核心线程超时时间是0s，所以非核心线程一旦空闲立马就会被回收
        executor = Executors.newScheduledThreadPool(5);
    }

    private static class Holder {
        private static final AsyncCenter instance = new AsyncCenter();
    }

    public static AsyncCenter getInstance() {
        return Holder.instance;
    }

    public  <T> void submit(Exec<T> exec, @NonNull final IAsyncCallback<T> callback) {

        if (callback == null) {
            return;
        }

        getResult(exec).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<T>() {
            @Override
            public void accept(T o) throws Exception {
                if (callback != null) {
                    callback.onSuccess(o);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if (callback != null) {
                    callback.onFailed(throwable);
                }
            }
        },new Action() {
            @Override
            public void run() throws Exception {
                if(callback!=null){
                    callback.onComplete();
                }
            }
        });

    }

    //切换到子线程中来执行实际的耗时操作
    private  <T> Observable getResult(final Exec<T> exec) {
        return Observable.create(new ObservableOnSubscribe<T>() {
            @Override
            public void subscribe(ObservableEmitter<T> emitter) throws Exception {

                try{
                    T t = exec.execute();
                    emitter.onNext(t);
                    emitter.onComplete();
                }catch (Exception e){
                    emitter.onError(e);
                }

            }
        }).subscribeOn(Schedulers.from(executor));
    }

}
