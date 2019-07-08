package com.cj.common.provider.fun$business.share;

import androidx.annotation.Nullable;

/**
 * Author:chris - jason
 * Date:2019/2/3.
 * Package:com.cj.common.provider.fun$business.share
 * 分享结果回调接口
 */

public interface IShareResultCallback {

    void onSuccess();

    void onFailed(@Nullable Throwable throwable);

    void onCancel();

}
