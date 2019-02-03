package com.cj.business.pay.service;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cj.business.pay.PayCenter;
import com.cj.common.provider.fun$business.IPayProvider;
import com.cj.common.provider.fun$business.IPayResultCallback;
import com.cj.common.provider.fun$business.PayParams;

/**
 * Author:chris - jason
 * Date:2019/2/2.
 * Package:com.cj.business.pay.service
 * fun-business 对外提供支付能力的服务
 */

@Route(path = "/fun_business/SEV/com.cj.business.pay.service.PayService")
public class PayService implements IPayProvider {


    @Override
    public void init(Context context) {

    }

    @Override
    public void invokePay(PayParams payParams, IPayResultCallback callback) {
        PayCenter.getInstance().doPay(payParams,callback);
    }

}