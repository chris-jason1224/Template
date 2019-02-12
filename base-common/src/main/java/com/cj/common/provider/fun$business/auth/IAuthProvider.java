package com.cj.common.provider.fun$business.auth;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * Author:chris - jason
 * Date:2019/2/12.
 * Package:com.cj.common.provider.fun$business.auth
 */

public interface IAuthProvider extends IProvider {

    void invokeAuth(AuthParams params,IAuthResultCallback callback);

}
