package com.cj.business.share;

import com.cj.business.share.wechat.WeChatShare;
import com.cj.common.provider.fun$business.share.IShareResultCallback;
import com.cj.common.provider.fun$business.share.ShareParams;
import com.cj.common.provider.fun$business.share.WeChatShareParams;
import com.cj.log.CJLog;

/**
 * Author:chris - jason
 * Date:2019/2/3.
 * Package:com.cj.business.share
 * 分享中心 分发各种分享功能
 */

public class ShareCenter{

    private ShareCenter(){

    }

    private static class Holder{
        private static final ShareCenter instance = new ShareCenter();
    }

    public static ShareCenter getInstance(){

        return Holder.instance;
    }

    //分发各个平台分享功能
    public void doShare(ShareParams shareParams, IShareResultCallback callback){

        if(shareParams==null){
            CJLog.getInstance().log_e("分享参数为空");
        }

        if(callback==null){
            CJLog.getInstance().log_e("分享回调为空");
        }

        switch (shareParams.getSharePlatform()){

            //分发到微信
            case ShareParams.Platform.WECHAT_SESSION:
            case ShareParams.Platform.WECHAT_TIMELINE:
                WeChatShare.getInstance().realShare((WeChatShareParams) shareParams,callback);
                break;
        }

    }


}
