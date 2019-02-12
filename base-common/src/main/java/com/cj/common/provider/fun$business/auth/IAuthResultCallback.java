package com.cj.common.provider.fun$business.auth;

/**
 * Author:chris - jason
 * Date:2019/2/12.
 * Package:com.cj.common.provider.fun$business.auth
 */

public interface IAuthResultCallback {

    //授权成功返回临时票据
    void onSuccess(String code);

    void onFailed();

    void onCancel();

}
