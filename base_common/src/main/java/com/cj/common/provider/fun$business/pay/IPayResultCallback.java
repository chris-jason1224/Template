package com.cj.common.provider.fun$business.pay;

/**
 * Author:chris - jason
 * Date:2019/2/2.
 * Package:com.cj.common.provider.fun$business
 * 支付结果回调接口
 */

public interface IPayResultCallback {

    void onSuccess();

    void onFailed();

    void onCancel();

}