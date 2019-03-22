package com.cj.common.provider.fun$business.share;

import com.alibaba.android.arouter.facade.template.IProvider;

/**
 * Author:chris - jason
 * Date:2019/2/3.
 * Package:com.cj.common.provider.fun$business.share
 * fun-business 对外提供分享服务的约束接口
 */

public interface IShareProvider extends IProvider {

    void invokeShare(ShareParams shareParams , IShareResultCallback callback);

}
