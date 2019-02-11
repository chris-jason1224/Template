package com.cj.business.share;

import com.cj.common.provider.fun$business.share.IShareResultCallback;
import com.cj.common.provider.fun$business.share.WeChatShareParams;

/**
 * Author:chris - jason
 * Date:2019/2/3.
 * Package:com.cj.business.share
 */

public interface IShare{

    void realShare(WeChatShareParams params, IShareResultCallback callback);

}
