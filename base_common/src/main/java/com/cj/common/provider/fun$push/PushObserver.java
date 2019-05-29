package com.cj.common.provider.fun$push;

/**
 * Author:chris - jason
 * Date:2019/4/15.
 * Package:com.cj.common.provider.fun$push
 * 回调推送消息的接口
 */
public interface PushObserver {

    /**
     *
     * @param message 消息主体
     * @param extras 附带数据
     */
    void onPush(String message,Object ...extras);

}
