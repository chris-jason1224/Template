package com.cj.common.provider.fun$business;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * Author:chris - jason
 * Date:2019/2/2.
 * Package:com.cj.common.provider.fun$business
 * fun-business 对外提供的支付服务约束接口
 */

public interface IPayProvider extends IProvider{

    void invokePay(PayParams payParams ,IPayResultCallback callback);

}
