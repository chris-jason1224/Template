package com.cj.business.auth;

import com.cj.common.provider.fun$business.auth.AuthParams;
import com.cj.common.provider.fun$business.auth.IAuthResultCallback;

/**
 * Author:chris - jason
 * Date:2019/3/22.
 * Package:com.cj.business.auth
 */
public interface IAuth {
     void realAuth(AuthParams params, IAuthResultCallback callback);
}
