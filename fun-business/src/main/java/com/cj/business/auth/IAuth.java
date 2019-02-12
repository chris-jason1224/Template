package com.cj.business.auth;

import com.cj.common.provider.fun$business.auth.AuthParams;
import com.cj.common.provider.fun$business.auth.IAuthResultCallback;

/**
 * Author:chris - jason
 * Date:2019/2/12.
 * Package:com.cj.business.auth
 * 实际发起授权的约束接口
 */

public interface IAuth {

    void realAuth(AuthParams params, IAuthResultCallback callback);

}
