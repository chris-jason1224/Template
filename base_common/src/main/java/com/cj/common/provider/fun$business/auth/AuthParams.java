package com.cj.common.provider.fun$business.auth;

/**
 * Author:chris - jason
 * Date:2019/2/12.
 * Package:com.cj.common.provider.fun$business.auth
 * 申请第三方app授权的参数
 */

public class AuthParams<T> {

    public int platform;//授权平台 字典值为AuthCenter.AuthPlatform

    public T data;//发起授权的参数


    public AuthParams() {
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }
}
