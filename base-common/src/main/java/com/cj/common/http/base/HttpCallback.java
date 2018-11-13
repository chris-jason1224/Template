package com.cj.common.http.base;

/**
 * Created by mayikang on 2018/8/20.
 */

public interface HttpCallback<M> {

    void onSuccess(M m);

    void onFailed(String msg);

}
