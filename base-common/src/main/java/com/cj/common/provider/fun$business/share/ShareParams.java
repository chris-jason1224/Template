package com.cj.common.provider.fun$business.share;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.cj.common.provider.fun$business.share.ShareParams.Platform.WECHAT_SESSION;
import static com.cj.common.provider.fun$business.share.ShareParams.Platform.WECHAT_TIMELINE;
import static com.cj.common.provider.fun$business.share.ShareParams.Type.IMAGE;
import static com.cj.common.provider.fun$business.share.ShareParams.Type.TEXT;
import static com.cj.common.provider.fun$business.share.ShareParams.Type.WEBPAGE;

/**
 * Author:chris - jason
 * Date:2019/2/3.
 * Package:com.cj.common.provider.fun$business.share
 * 基础分享参数
 */

public abstract class ShareParams {

    //分享平台定义
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({WECHAT_SESSION,WECHAT_TIMELINE})
    public @interface Platform{

        int WECHAT_SESSION = 1;//微信消息

        int WECHAT_TIMELINE=2;//微信朋友圈

    }

    //分享类型定义
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TEXT,WEBPAGE,IMAGE})
    public @interface Type{

        int TEXT = 1;//纯文本分享

        int WEBPAGE = 2;//网页分享

        int IMAGE =3;//图片分享
    }


    private int sharePlatform;//分享到的平台

    private int shareType;//分享的方式

    public ShareParams() {
    }

    public int getSharePlatform() {
        return sharePlatform;
    }

    public void setSharePlatform(int sharePlatform) {
        this.sharePlatform = sharePlatform;
    }

    public int getShareType() {
        return shareType;
    }

    public void setShareType(int shareType) {
        this.shareType = shareType;
    }



}
