package com.cj.business.share;

import android.content.Context;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.cj.common.provider.fun$business.share.IShareProvider;
import com.cj.common.provider.fun$business.share.IShareResultCallback;
import com.cj.common.provider.fun$business.share.ShareParams;

/**
 * Author:chris - jason
 * Date:2019/2/3.
 * Package:com.cj.business.share
 */
@Route(path = "/fun_business/SEV/com.cj.business.share.ShareService")
public class ShareService implements IShareProvider {

    @Override
    public void init(Context context) {

    }

    @Override
    public void invokeShare(ShareParams shareParams, IShareResultCallback callback) {
        ShareCenter.getInstance().doShare(shareParams,callback);
    }
}
