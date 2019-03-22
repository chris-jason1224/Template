package com.cj.business.auth;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cj.common.provider.fun$business.auth.AuthParams;
import com.cj.common.provider.fun$business.auth.IAuthProvider;
import com.cj.common.provider.fun$business.auth.IAuthResultCallback;

/**
 * Author:chris - jason
 * Date:2019/2/12.
 * Package:com.cj.business.auth
 */

@Route(path = "/fun_business/SEV/com.cj.business.auth.AuthService")
public class AuthService implements IAuthProvider {
    @Override
    public void invokeAuth(AuthParams params, IAuthResultCallback callback) {
        AuthCenter.getInstance().doAuth(params,callback);
    }

    @Override
    public void init(Context context) {

    }
}
