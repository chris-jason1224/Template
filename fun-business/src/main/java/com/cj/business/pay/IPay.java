package com.cj.business.pay;

import com.cj.common.provider.fun$business.pay.IPayResultCallback;

/**
 * Author:chris - jason
 * Date:2019/2/1.
 * Package:com.cj.business.pay
 */

public interface IPay {

    void realPay(String prePayInfo,IPayResultCallback callback);

}