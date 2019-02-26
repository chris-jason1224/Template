package com.cj.main;

import android.graphics.drawable.Animatable;
import android.os.Handler;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cj.common.base.BaseActivity;
import com.cj.common.provider.fun$business.auth.AuthParams;
import com.cj.common.provider.fun$business.auth.IAuthProvider;
import com.cj.common.provider.fun$business.auth.IAuthResultCallback;
import com.cj.common.provider.fun$business.pay.IPayProvider;
import com.cj.common.provider.fun$business.pay.IPayResultCallback;
import com.cj.common.provider.fun$business.pay.PayParams;
import com.cj.common.provider.fun$business.share.IShareProvider;
import com.cj.common.provider.fun$business.share.IShareResultCallback;
import com.cj.common.provider.fun$business.share.ShareParams;
import com.cj.common.provider.fun$business.share.WeChatShareParams;
import com.cj.common.util.AndroidSystemUtil;
import com.cj.common.util.ProgressUtil;
import com.cj.common.util.image.IImageLoadCallback;
import com.cj.common.util.image.ImageLoader;
import com.cj.log.CJLog;
import com.cj.ui.notify.Alerter.AlertManager;
import com.cj.ui.notify.Alerter.AlerterListener;
import com.cj.ui.tip.UITipDialog;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;


@Route(path = "/biz_main/ACT/com.cj.main.MainActivity")
public class MainActivity extends BaseActivity {


    @BindView(R2.id.drawee)
    SimpleDraweeView draweeView;

    @BindView(R2.id.base_common_toolbar)
    Toolbar toolbar;

    @BindView(R2.id.ll_parent)
    LinearLayout mLLParent;

    @Autowired(name = "/fun_business/SEV/com.cj.business.pay.PayService")
    IPayProvider pay;

    @Autowired(name = "/fun_business/SEV/com.cj.business.share.ShareService")
    IShareProvider share;

    @Autowired(name = "/fun_business/SEV/com.cj.business.auth.AuthService")
    IAuthProvider auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int resourceLayout() {
        return R.layout.biz_main_activity_main;
    }

    @Override
    public View initStatusLayout() {
        return mLLParent;
    }


    @Override
    protected void initData() {
        String url = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1547813416419&di=cd93b735d229213f2e0dee2759ad81d3&imgtype=0&src=http%3A%2F%2Fattimg.dospy.com%2Fimg%2Fday_111004%2F20111004_f4e8d9f067a3542375c920PXx4HtkkZZ.jpg";
        ImageLoader.getInstance().load(this, draweeView, url, new IImageLoadCallback() {
            @Override
            public void onSuccess(String id, ImageInfo imageInfo, Animatable animatable) {

            }

            @Override
            public void onFailed(String id, Throwable throwable) {

            }
        });
    }

    @OnClick({R2.id.goto_biz_login, R2.id.alert, R2.id.goto_test, R2.id.make_crash, R2.id.foreground,
            R2.id.encrypt, R2.id.decrypt, R2.id.pay,R2.id.share,R2.id.auth})
    public void onClick(View v) {

        int vid = v.getId();

        if(R.id.auth == vid){
            AuthParams<String> params = new AuthParams<>();
            params.setPlatform(1);
            params.setData("哈哈哈哈哈");
            if(auth!=null){
                auth.invokeAuth(params, new IAuthResultCallback() {
                    @Override
                    public void onSuccess(String code) {
                        CJLog.getInstance().log_e("授权成功"+code);
                    }

                    @Override
                    public void onFailed() {
                        CJLog.getInstance().log_e("授权失败");
                    }

                    @Override
                    public void onCancel() {
                        CJLog.getInstance().log_e("授权取消");
                    }
                });
            }

            return;
        }

        if(R.id.share == vid){
            if(share != null){
                WeChatShareParams shareParams = new WeChatShareParams();
                shareParams.setSharePlatform(ShareParams.Platform.WECHAT_SESSION);
                shareParams.setShareType(ShareParams.Type.WEBPAGE);
                shareParams.setTitle("title");
                shareParams.setDescription("description");
                shareParams.setWebpageUrl("http://www.baidu.com");
                shareParams.setImagePath("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1549877957450&di=b19dc4633b2d86390abe18c3c2bd249c&imgtype=0&src=http%3A%2F%2Fs9.rr.itc.cn%2Fr%2FwapChange%2F20165_4_0%2Fa5edgp26302277303596.jpg");
                share.invokeShare(shareParams, new IShareResultCallback() {
                    @Override
                    public void onSuccess() {
                        CJLog.getInstance().log_e("分享成功");
                    }

                    @Override
                    public void onFailed(@Nullable Throwable throwable) {
                        CJLog.getInstance().log_e("分享失败");
                    }

                    @Override
                    public void onCancel() {
                        CJLog.getInstance().log_e("分享取消");
                    }
                });
            }else {
                AlertManager.create(this).setMessage("Share == null").show();
            }
        }


        if (R.id.pay == vid) {

            if (pay != null) {

                String extra = " {\"appid\":\"wx529bf812fe26c929\",\"noncestr\":\"b605b10a8054478289b248df2a862f12\",\"package\":\"Sign=WXPay\",\"packageValue\":\"Sign=WXPay\",\"partnerid\":\"1498803022\",\"prepayid\":\"wx02102704848674779bb5938b2166506728\",\"sign\":\"8B72AF273C00EB6DD3F0C3A14B93B79D\",\"timestamp\":\"1549074425\"}";
                PayParams<String> payParams = new PayParams<>("123456", 1, extra);

                pay.invokePay(payParams, new IPayResultCallback() {
                    @Override
                    public void onSuccess() {
                        CJLog.getInstance().log_e("支付成功回调");
                    }

                    @Override
                    public void onFailed() {
                        CJLog.getInstance().log_e("支付失败回调");
                    }

                    @Override
                    public void onCancel() {
                        CJLog.getInstance().log_e("支付取消回调");
                    }
                });

            } else {
                AlertManager.create(this).setMessage("Pay == null").show();
            }

            return;
        }

        //加密测试
        if (R.id.encrypt == vid) {
            //AESUtil.getInstance().encrypt("");
            ProgressUtil.getInstance().showLoading();

            return;
        }
        //解密测试
        if (R.id.decrypt == vid) {
            //AESUtil.getInstance().decrypt("");
            ProgressUtil.getInstance().dismissLoading();
            return;
        }


        if (R.id.foreground == vid) {
            AndroidSystemUtil.getInstance().isAppForeground(this);
            return;
        }


        //人造一个crash
        if (R.id.make_crash == vid) {
            TextView tv = null;
            tv.setText("fffff");
            return;
        }

        //打开biz-login module
        if (R.id.goto_biz_login == vid) {
            ArrayList<Integer> list = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                list.add(i);
            }
            ARouter.getInstance().build("/biz_login/ACT/com.cj.login.LoginActivity").navigation();
            return;
        }

        if (R.id.alert == vid) {
            AlertManager.create(MainActivity.this).
                    setTitle("杨二狗").
                    setMessage("嘻嘻嘻嘻").
                    setAutoCollapse(true).
                    setAlertListener(new AlerterListener() {
                        @Override
                        public void onShow() {
                            Log.e("myk", "onShow");
                        }

                        @Override
                        public void onHide() {
                            Log.e("myk", "onHide");
                        }

                    }).show();

            return;
        }

        //跳转TestActivity
        if (R.id.goto_test == vid) {
            ARouter.getInstance().build("/biz_main/ACT/com.cj.main.test.TestActivity").navigation();
            return;
        }


    }

    @Override
    protected boolean useImmersionBar() {
        return true;
    }

    @Override
    protected void initImmersionBar() {
        super.initImmersionBar();
        immersionBar.
                statusBarDarkFont(true).
                titleBar(toolbar);//解决实际布局顶到statusBar
    }
}