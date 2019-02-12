package com.cj.business.share.wechat;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.cj.business.Config;
import com.cj.business.share.IShare;
import com.cj.business.share.lifecycle.ShareEventLifecycleObserver;
import com.cj.common.bus.DataBus;
import com.cj.common.bus.DataBusKey;
import com.cj.common.provider.fun$business.share.IShareResultCallback;
import com.cj.common.provider.fun$business.share.ShareParams;
import com.cj.common.provider.fun$business.share.WeChatShareParams;
import com.cj.common.util.LooperUtil;
import com.cj.common.util.pkg.PackageUtil;
import com.cj.log.CJLog;
import com.cj.manager.basement.BaseApplication;
import com.cj.mst.wxapi.WeChatUtil;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXTextObject;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.io.InputStream;
import java.util.concurrent.CountDownLatch;


/**
 * Author:chris - jason
 * Date:2019/2/3.
 * Package:com.cj.business.share.wechat
 */

public class WeChatShare implements IShare, LifecycleOwner {

    private String tag = "微信分享";

    //微信允许的图片尺寸最大为10M
    private long maxImageSize = 10 * 1024 * 1024;

    //图片过大提示信息
    private String str_imageOverSize = " 图片过大，已超过10M!!!";

    private LifecycleRegistry lifecycleRegistry;

    //分享结果回调接口
    private IShareResultCallback callback;

    private WeChatShare() {

        //手动实现LifeCycle生命周期管理
        lifecycleRegistry = new LifecycleRegistry(this);
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE);
        lifecycleRegistry.addObserver(new ShareEventLifecycleObserver());

        //注册DataBus接收器
        DataBus.get().with(DataBusKey.WeChatShareResult.getKey(), DataBusKey.WeChatShareResult.getT()).observe(this, observer);

    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycleRegistry;
    }


    private static class Holder {
        private static final WeChatShare instance = new WeChatShare();
    }

    public static WeChatShare getInstance() {
        return Holder.instance;
    }


    //微信分享实际功能

    /**
     * IMediaObject
     * WXMediaMessage （WXWebPageObject）
     * SendMessageToWX.Req (WXMediaMessage)
     *
     * @param params
     * @param callback
     */
    @Override
    public void realShare(WeChatShareParams params, IShareResultCallback callback) {

        this.callback = callback;

        if (TextUtils.isEmpty(Config.WECHAT_APP_ID)) {
            CJLog.getInstance().log_d("微信AppID为空!!!");
            return;
        }

        IWXAPI wxapi = WXAPIFactory.createWXAPI(BaseApplication.getInstance(), Config.WECHAT_APP_ID, true);
        if (wxapi == null) {
            CJLog.getInstance().log_d("WXAPI == null");
            return;
        }

        if (!PackageUtil.getInstance(BaseApplication.getInstance()).isXInstalled("com.tencent.mm")) {
            LooperUtil.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BaseApplication.getInstance().getCurrentActivity(), "请先安装微信再继续分享", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }

        SendMessageToWX.Req req = new SendMessageToWX.Req();
        WXMediaMessage msg = new WXMediaMessage();
        //title 和 description是通用参数
        msg.title = params.getTitle();
        msg.description = params.getDescription();

        //区分分享类型
        switch (params.getShareType()) {
            //文本分享
            case ShareParams.Type.TEXT:
                WXTextObject textObject = new WXTextObject();
                textObject.text = params.getText();
                msg.mediaObject = textObject;
                break;

            //图片分享
            case ShareParams.Type.IMAGE:
                WXImageObject imageObject = new WXImageObject();
                //校验图片参数
                imageObject.imageData = getImageByteData(params);
                msg.mediaObject = imageObject;
                break;

            //网页分享
            case ShareParams.Type.WEBPAGE:
                WXWebpageObject webPageObject = new WXWebpageObject();
                //要分享的网页链接地址
                webPageObject.webpageUrl = params.getWebpageUrl();
                //校验图片参数
                msg.thumbData = getImageByteData(params);
                msg.mediaObject = webPageObject;
                break;
        }


        //区分分享类型
        switch (params.getShareType()) {

            /**
             *  SendMessageToWX.Req.transaction 对应请求的事务ID
             *
             *  SendMessageToWX.Req.scene 发送的目标场景
             *
             *  分享到对话:SendMessageToWX.Req.WXSceneSession
             *  分享到朋友圈:SendMessageToWX.Req.WXSceneTimeline ;
             *  分享到收藏:SendMessageToWX.Req.WXSceneFavorite
             */
            case ShareParams.Type.TEXT:
                req.transaction = buildTransaction("text");
                break;
            case ShareParams.Type.IMAGE:
                req.transaction = buildTransaction("image");
                break;
            case ShareParams.Type.WEBPAGE:
                req.transaction = buildTransaction("webpage");
                break;
        }

        //区分分享平台（业务场景）
        switch (params.getSharePlatform()) {

            //微信聊天
            case ShareParams.Platform.WECHAT_SESSION:
                req.scene = SendMessageToWX.Req.WXSceneSession;
                break;
            //微信朋友圈
            case ShareParams.Platform.WECHAT_TIMELINE:
                req.scene = SendMessageToWX.Req.WXSceneTimeline;
                break;

        }

        req.message = msg;

        //调用api接口，发送数据到微信
        boolean sendRes = wxapi.sendReq(req);
        //激活微信分享结果接收器
        //当观察者的生命周期处于STARTED或RESUMED状态时，LiveData会通知观察者数据变化；在观察者处于其他状态时，即使LiveData的数据变化了，也不会通知。
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START);
    }

    /**
     * 通过时间戳生成唯一的请求
     *
     * @param type
     * @return
     */
    private String buildTransaction(String type) {
        return type == null ? String.valueOf(System.currentTimeMillis()) : type + String.valueOf(System.currentTimeMillis());
    }

    /**
     * 校验图片参数
     *
     * @param params
     * @return
     */
    private byte[] getImageByteData(WeChatShareParams params) {

        //imageData优先级高于imagePath
        if (params.getImageData() != null) {

            //校验本地图片是否大于10M
            if (params.getImageData().length > maxImageSize) {
                CJLog.getInstance().log_e(tag + str_imageOverSize);
                return null;
            }

            return params.getImageData();

        } else if (!TextUtils.isEmpty(params.getImagePath())) {
            //网络图片
            if (params.getImagePath().startsWith("http")) {
                byte[] bytes = WeChatUtil.getInstance().getByteArrayFromUrl(params.getImagePath());
                if (bytes == null || bytes.length > maxImageSize) {
                    CJLog.getInstance().log_e(tag + str_imageOverSize);
                    return null;
                }
                return bytes;
            } else {
                //本地文件图片
                byte[] bytes = WeChatUtil.getInstance().readFromFile(params.getImagePath(), 0, -1);
                if (bytes == null || bytes.length > maxImageSize) {
                    CJLog.getInstance().log_e(tag + str_imageOverSize);
                    return null;
                }
                return bytes;
            }

        }

        return null;

    }

    /**
     * 微信支付结果接收器
     */
    private Observer<String> observer = new Observer<String>() {
        @Override
        public void onChanged(@Nullable String msg) {
            if (!TextUtils.isEmpty(msg) && callback != null) {
                switch (msg) {
                    case WeChatShareResult.SUCCESS:
                        callback.onSuccess();
                        break;
                    case WeChatShareResult.FAILED:
                        //todo 这里暂时回调一个空错误信息
                        callback.onFailed(null);
                        break;
                    case WeChatShareResult.CANCEL:
                        callback.onCancel();
                        break;
                }
            }
            //暂时关闭数据接收
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP);
        }
    };

}
