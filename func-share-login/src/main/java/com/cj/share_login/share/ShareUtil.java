package com.cj.share_login.share;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;

import com.cj.common.util.AndroidSystemUtil;
import com.cj.common.util.pkg.PackageUtil;
import com.cj.log.CJLog;
import com.cj.share_login.R;
import com.cj.share_login.share.entity.ShareEntity;
import com.cj.ui.notify.Alerter.AlertManager;
import com.cj.ui.notify.Alerter.AlerterListener;
import java.util.HashMap;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by mayikang on 2018/3/14.
 */

public class ShareUtil {

    /*** app包名 ****/
    private String WE_CHAT_PKN = "com.tencent.mm";
    private String QQ_PKN = "com.tencent.mobileqq";
    private String QZONE_PKN = "com.qzone";
    private String SINA_WEI_BO_PKN = "com.sina.weibo";

    private ShareUtil() {

    }

    private static class SharePlatformUtilHolder {
        private static final ShareUtil instance = new ShareUtil();
    }

    public static ShareUtil getInstance() {
        return SharePlatformUtilHolder.instance;
    }

    /**
     * @param c
     * @param entity
     * @param callback
     */
    public void share(final Activity c, final ShareEntity entity, final ShareCallback callback) {

        if (c == null) {
            CJLog.getInstance().log_e("invoke share context == null");
            return;
        }

        if (entity == null) {
            CJLog.getInstance().log_e("invoke share entity == null");
            return;
        }

        if (callback == null) {
            CJLog.getInstance().log_e("invoke share callback == null");
            return;
        }

        Platform platform = null;

        //分享URL地址
        String shareUrl =  entity.getUrl();


        //确定分享平台
        switch (entity.getPlatform()) {
            //微信好友
            case 1:
                if (PackageUtil.getInstance(c).isXInstalled(WE_CHAT_PKN)) {
                    platform = ShareSDK.getPlatform(Wechat.NAME);
                } else {
                    AlertManager.create(c).
                            setTitle("分享错误").
                            setMessage("请安装微信 app 之后再分享").
                            setAlertListener(new AlerterListener() {
                        @Override
                        public void onShow() {

                        }

                        @Override
                        public void onHide() {

                        }
                    })
                            .show();
                    return;
                }

                break;
            //微信朋友圈
            case 2:
                if (PackageUtil.getInstance(c).isXInstalled(WE_CHAT_PKN)) {
                    platform = ShareSDK.getPlatform(WechatMoments.NAME);
                } else {
                    AlertManager.create(c).
                            setTitle("分享错误").
                            setMessage("请安装微信 app 之后再分享").
                            setAlertListener(new AlerterListener() {
                        @Override
                        public void onShow() {

                        }

                        @Override
                        public void onHide() {

                        }
                    }).
                            show();
                    return;
                }
                break;
            //qq 好友
            case 3:
                if (PackageUtil.getInstance(c).isXInstalled(QQ_PKN)) {
                    platform = ShareSDK.getPlatform(QQ.NAME);
                } else {
                    AlertManager.create(c).
                            setTitle("分享错误").
                            setMessage("请安装QQ app 之后再分享").
                            setAlertListener(new AlerterListener() {
                        @Override
                        public void onShow() {

                        }

                        @Override
                        public void onHide() {

                        }
                    }).
                            show();
                }

                break;
            //qzone
            case 4:
                if (PackageUtil.getInstance(c).isXInstalled(QZONE_PKN) || PackageUtil.getInstance(c).isXInstalled(QQ_PKN)) {
                    platform = ShareSDK.getPlatform(QZone.NAME);
                } else {
                    AlertManager.create(c).
                            setTitle("分享错误").
                            setMessage("请安装QQ或QZone app 之后再分享").
                            setAlertListener(new AlerterListener() {
                        @Override
                        public void onShow() {

                        }

                        @Override
                        public void onHide() {

                        }
                    }).
                            show();
                    return;
                }
                break;
            //sina weibo
            case 5:
                if (PackageUtil.getInstance(c).isXInstalled(SINA_WEI_BO_PKN)) {
                    platform = ShareSDK.getPlatform(SinaWeibo.NAME);
                } else {
                    AlertManager.create(c).
                            setTitle("分享错误").
                            setMessage("请安装新浪微博 app 之后再分享").
                            setAlertListener(new AlerterListener() {
                        @Override
                        public void onShow() {

                        }

                        @Override
                        public void onHide() {

                        }
                    }).
                            show();
                    return;
                }
                break;
        }
        //设置分享参数
        final Platform.ShareParams params = new Platform.ShareParams();

        //区分分享类型
        switch (entity.getShareType()) {
            //分享网页
            case 1:
                params.setShareType(Platform.SHARE_WEBPAGE);
                params.setUrl(shareUrl);
                //附带的图片地址
                //todo 如果传入的图片已经被删除，无法分享
                //todo 如果传入的图片已经被删除，图片不存在加载APP的启动图
                if (!TextUtils.isEmpty(entity.getImgPath())) {
                    params.setImageUrl(entity.getImgPath());
                } else {
                    params.setImageData(BitmapFactory.decodeResource(c.getResources(), R.drawable.share_login_default_white_rectangle));
                }
                break;
            //分享图片
            case 2:
                params.setShareType(Platform.SHARE_IMAGE);
                if (!TextUtils.isEmpty(entity.getImgPath())) {
                    params.setImageUrl(entity.getImgPath());
                } else {
                    Bitmap logo = BitmapFactory.decodeResource(c.getResources(), R.drawable.share_login_default_white_rectangle);
                    params.setImageData(logo);
                }
                break;
            //分享文本
            case 3:
                params.setShareType(Platform.SHARE_TEXT);
                break;

        }
        //以下为通用分享内容
        //设置 title
        params.setTitle(entity.getTitle());

        //设置 text
        if (!TextUtils.isEmpty(entity.getText())) {
            params.setText(entity.getText());
        } else {
            params.setText("来自"+ AndroidSystemUtil.getInstance().getAppName(c.getApplicationContext())+"的分享");
        }

        if (platform != null) {
            //QQ 和 QZone 要分享的网页字段是 titleUrl
            if (platform.getName().equals(QZone.NAME)) {
                params.setTitleUrl(shareUrl);
            }
            if (platform.getName().equals(QQ.NAME)) {
                params.setTitleUrl(shareUrl);
                params.setUrl(null);
            }

            //添加结果回调
            platform.setPlatformActionListener(new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                    callback.onSuccess();
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                    callback.onError();
                }

                @Override
                public void onCancel(Platform platform, int i) {
                    callback.onCancel();
                }
            });

            //发起操作
            platform.share(params);
        }


    }


    /**
     * 统一的分享回调接口
     */
    public interface ShareCallback {

        void onSuccess();

        void onCancel();

        void onError();
    }


}
