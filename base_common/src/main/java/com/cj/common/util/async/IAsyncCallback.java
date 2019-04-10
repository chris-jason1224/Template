package com.cj.common.util.async;

/**
 * Author:chris - jason
 * Date:2019/4/9.
 * Package:com.cj.common.util.async
 * 异步执行结果回调接口
 */
public interface IAsyncCallback<T> {

    //异步执行成功，并取得结果
    void onSuccess(T object);

    //异步执行失败
    void onFailed(Throwable throwable);

    //异步执行完全结束
    void onComplete();

}
