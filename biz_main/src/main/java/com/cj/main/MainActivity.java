package com.cj.main;

import android.Manifest;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cj.common.base.BaseActivity;
import com.cj.common.bus.ModuleBus;
import com.cj.common.db.IOrm;
import com.cj.common.db.OrmUtil;
import com.cj.common.model.StudentEntity;
import com.cj.common.provider.fun$bluetooth.BTState;
import com.cj.common.provider.fun$bluetooth.BTStateObserver;
import com.cj.common.provider.fun$bluetooth.IBTProvider;
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
import com.cj.common.provider.fun$compressor.compress.ICompressCallback;
import com.cj.common.provider.fun$compressor.compress.ICompressProvider;
import com.cj.common.provider.fun$lbs.ILBSProvider;
import com.cj.common.provider.fun$lbs.ILocateResultCallback;
import com.cj.common.provider.fun$lbs.locate.LocationInfoEntity;
import com.cj.common.util.AndroidSystemUtil;
import com.cj.common.util.JSONUtils;
import com.cj.common.util.ProgressUtil;
import com.cj.common.util.async.AsyncCenter;
import com.cj.common.util.async.Exec;
import com.cj.common.util.async.IAsyncCallback;
import com.cj.common.util.image.IImageLoadCallback;
import com.cj.common.util.image.ImageLoader;
import com.cj.fun_aop.annotation.ExecutionTimeTrace;
import com.cj.fun_aop.annotation.SingleSubmit;
import com.cj.fun_aop.annotation.WifiNeed;
import com.cj.log.CJLog;
import com.cj.ui.banner.Banner;
import com.cj.ui.banner.loader.ImageLoaderInterface;
import com.cj.ui.dialog.DialogUtil;
import com.cj.ui.notify.Alerter.AlertManager;
import com.cj.ui.notify.Alerter.AlerterListener;
import com.cj.ui.util.ScreenUtil;
import com.cj.utils.io.IOUtil;
import com.cj.utils.list.ListUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import gen.com.cj.bus.Gen$biz_login$Interface;
import gen.com.cj.bus.Gen$biz_main$Interface;
import io.objectbox.query.Query;
import pub.devrel.easypermissions.EasyPermissions;

@Route(path = "/biz_main/ACT/com.cj.main.MainActivity")
public class MainActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private SimpleDraweeView draweeView;
    @BindView(R2.id.base_common_toolbar)
    Toolbar toolbar;
    private LinearLayout mLLParent;
    private TextView mTVState;
    private ImageView mIVTest;
    private Banner banner;


    @Autowired(name = "/fun_business/SEV/com.cj.business.pay.PayService")
    IPayProvider pay;
    @Autowired(name = "/fun_business/SEV/com.cj.business.share.ShareService")
    IShareProvider share;
    @Autowired(name = "/fun_business/SEV/com.cj.business.auth.AuthService")
    IAuthProvider auth;
    @Autowired(name = "/fun_compressor/SEV/com.cj.easycompressor.CompressService")
    ICompressProvider compress;
    @Autowired(name = "/fun_bluetooth/SEV/com.cj.bluetooth.BTService")
    IBTProvider bt;
    @Autowired(name = "/fun_lbs/SEV/com.cj.fun_lbs.LBSService")
    ILBSProvider lbs;

    private IOrm orm = new OrmUtil();


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

        bt.registerBTStateObserver(new BTStateObserver() {
            @Override
            public void onStateChanged(BTState btState) {
                CJLog.getInstance().log_e("main - state  -" + btState.getState());
                mTVState.setText("状态=" + btState.getState());
            }
        });


        ModuleBus.getInstance().of(Gen$biz_main$Interface.class).Gen$Refresh_Event$Method().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(MainActivity.this, "String = "+s, Toast.LENGTH_SHORT).show();
            }
        });

        ModuleBus.getInstance().of(Gen$biz_main$Interface.class).Gen$Close_Event$Method().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                Toast.makeText(MainActivity.this, "integer = "+integer.intValue(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void initView() {
        draweeView = fb(R.id.drawee);
        mLLParent = fb(R.id.ll_parent);
        mTVState = fb(R.id.tv_state);
        mIVTest = fb(R.id.iv_test);

        fb(R.id.msg).setOnClickListener(this);
        fb(R.id.msg2).setOnClickListener(this);
        fb(R.id.goto_biz_login).setOnClickListener(this);
        fb(R.id.alert).setOnClickListener(this);
        fb(R.id.goto_test).setOnClickListener(this);
        fb(R.id.make_crash).setOnClickListener(this);
        fb(R.id.foreground).setOnClickListener(this);
        fb(R.id.encrypt).setOnClickListener(this);
        fb(R.id.decrypt).setOnClickListener(this);
        fb(R.id.pay).setOnClickListener(this);
        fb(R.id.share).setOnClickListener(this);
        fb(R.id.auth).setOnClickListener(this);
        fb(R.id.compress).setOnClickListener(this);
        fb(R.id.bt).setOnClickListener(this);
        fb(R.id.print).setOnClickListener(this);
        fb(R.id.dialog).setOnClickListener(this);
        fb(R.id.async).setOnClickListener(this);
        fb(R.id.put).setOnClickListener(this);
        fb(R.id.get).setOnClickListener(this);
        fb(R.id.to_locate).setOnClickListener(this);
        fb(R.id.calculate).setOnClickListener(this);

        //////////////////////////
        List<String> imgs = new ArrayList<>();
        imgs.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572609968395&di=f1ffda3814f533f319b3e2f62277e176&imgtype=0&src=http%3A%2F%2Fimg.52jbj.com%2Fuploads%2Fallimg%2F160117%2Fco16011G64453-0.jpg");
        imgs.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572609968393&di=0ee1d72b7493abeb623b84a654008aef&imgtype=0&src=http%3A%2F%2Fi4.bbs.fd.zol-img.com.cn%2Ft_s1200x5000%2Fg5%2FM00%2F0F%2F0B%2FChMkJ1hjc3aIAGHyAAIQm0TYxUsAAY7aAB4_wsAAhCz914.jpg");
        imgs.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572609968392&di=c313712ce7768b428f6bd41f042f74f9&imgtype=0&src=http%3A%2F%2Fphotocdn.sohu.com%2F20130302%2FImg367606372.jpg");
        imgs.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1572609968389&di=1a59ceed2657ae3fe9c7ea5ba16313b0&imgtype=0&src=http%3A%2F%2Fn.sinaimg.cn%2Fsinacn%2Fw803h449%2F20180110%2F70fa-fyqnick1909052.png");

        banner = fb(R.id.banner);
        banner.setImages(imgs);
        banner.setImageLoader(new ImageLoaderInterface() {
            @Override
            public void displayImage(Context context, Object path, View imageView) {

                SimpleDraweeView drev = (SimpleDraweeView) imageView;
                ImageLoader.getInstance().load(MainActivity.this,drev,(String)path);
            }

            @Override
            public View createImageView(Context context) {
                SimpleDraweeView drev = new SimpleDraweeView(MainActivity.this);
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.dip2px(MainActivity.this,200));
                drev.setLayoutParams(lp);

                return drev;

            }
        });
        banner.start();

    }

    @WifiNeed
    @ExecutionTimeTrace
    @SingleSubmit
    @Override
    public void onClick(View v) {

        int vid = v.getId();

        if(R.id.msg  == vid){
            SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
            sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
            String time = sdf.format(new Date());
            ModuleBus.getInstance().of(Gen$biz_main$Interface.class).Gen$Refresh_Event$Method().post(time);
            return;
        }

        if(R.id.msg2  == vid){
            ModuleBus.getInstance().of(Gen$biz_main$Interface.class).Gen$Close_Event$Method().post(1);
            return;
        }

        if (R.id.to_locate == vid) {
            //6.0以上系统动态申请定位权限
            if (Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23) {
                if (!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    EasyPermissions.requestPermissions(this, "请授予我定位权限", 1, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    return;
                }
            }


            lbs.startLocate(new ILocateResultCallback() {
                @Override
                public void onLocation(LocationInfoEntity info) {
                    if (info != null) {
                        CJLog.getInstance().log_e("定位信息：" + info.toString());
                    }
                }
            });
        }

        if (R.id.calculate == vid) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String str_start = "2018-01-27";
            try {
                Date date_start = sdf.parse(str_start);
                long diff = System.currentTimeMillis() - date_start.getTime();
                long total = diff / (1000 * 60 * 60 * 24);
                int day = Integer.parseInt(String.valueOf(total));
                AlertManager.create(this).setTitle(sdf.format(date_start) + " - " + sdf.format(new Date())).setMessage("马牛牛和狗狗已经在一起" + day + "天啦啦啦啦啦啦").show();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (R.id.put == vid) {
            StudentEntity stu = new StudentEntity();
            stu.setAge(26);
            stu.setName("chris.Jason");
            orm.put(StudentEntity.class, stu);
        }

        if (R.id.get == vid) {
            //List<StudentEntity> list =orm.getAll(StudentEntity.class);
            Query<StudentEntity> query = orm.QueryBuilder(StudentEntity.class).build();
            List<StudentEntity> list = query.find();

            if (!ListUtil.isEmpty(list)) {
                String json = JSONUtils.javaObject2JsonString(list);
                CJLog.getInstance().log_json(json);
            }

        }

        if (R.id.async == vid) {

            AsyncCenter.getInstance().submit(new Exec<Bitmap>() {
                @Override
                public Bitmap execute() {
                    Bitmap bmp = null;
                    URL url;
                    try {
                        url = new URL("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1547813416419&di=cd93b735d229213f2e0dee2759ad81d3&imgtype=0&src=http%3A%2F%2Fattimg.dospy.com%2Fimg%2Fday_111004%2F20111004_f4e8d9f067a3542375c920PXx4HtkkZZ.jpg");
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        if (connection.getResponseCode() == 200) {
                            InputStream inputStream = connection.getInputStream();
                            if (inputStream != null) {
                                byte[] bb = IOUtil.getInstance().inputStreamToByte(inputStream);
                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = false;
                                bmp = BitmapFactory.decodeByteArray(bb, 0, bb.length, options);
                            }
                        }
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        return bmp;
                    }
                }
            }, new IAsyncCallback<Bitmap>() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    if (bitmap != null) {
                        mIVTest.setImageBitmap(bitmap);
                    }
                }

                @Override
                public void onFailed(Throwable throwable) {

                }

                @Override
                public void onComplete() {
                    CJLog.getInstance().log_e("结束");
                }

            });

        }

        if (R.id.dialog == vid) {
            DialogUtil.getInstance().showMessageDialog(this, "hhhhh", "f", null);
        }

        if (R.id.print == vid) {

            CJLog.getInstance().log_file("ffffffff{}fff");

            String start = "*** 懒购外卖 ***\n\n";
            bt.printCenter();
            bt.printSize(1);
            bt.printMessage(start);

            //订单小号
            String StoreOrderNo = "NO:1234567890abc%$#@" + "\n\n";
            bt.printCenter();
            bt.printSize(1);
            bt.printMessage(StoreOrderNo);

            //店铺名字
            String storeName = "韩式烤肉" + "\n\n";
            bt.printCenter();
            bt.printSize(1);
            bt.printMessage(storeName);

            //下单时间
            String payTime = "支付时间:" + "2019-03-24 12:26" + "\n\n";
            bt.printLeft();
            bt.printSize(0);
            bt.printMessage(payTime);

        }

        if (R.id.bt == vid) {
            bt.scan();
        }

        if (R.id.compress == vid) {
            compress.invokeCompress("", new ICompressCallback() {
                @Override
                public void onSuccess(File compressedFile) {

                }

                @Override
                public void onFailed(Throwable throwable) {

                }
            });
        }

        if (R.id.auth == vid) {
            AuthParams<String> params = new AuthParams<>();
            params.setPlatform(1);
            params.setData("哈哈哈哈哈");
            if (auth != null) {
                auth.invokeAuth(params, new IAuthResultCallback() {
                    @Override
                    public void onSuccess(String code) {
                        CJLog.getInstance().log_e("授权成功" + code);
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

        if (R.id.share == vid) {
            if (share != null) {
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
            } else {
                AlertManager.create(this).setMessage("Share == null").show();
            }
        }

        if (R.id.pay == vid) {

            if (pay != null) {

                String extra = " {\"appid\":\"wx39d4de0570bd3f5e\",\"noncestr\":\"b605b10a8054478289b248df2a862f12\",\"package\":\"Sign=WXPay\",\"packageValue\":\"Sign=WXPay\",\"partnerid\":\"1498803022\",\"prepayid\":\"wx02102704848674779bb5938b2166506728\",\"sign\":\"8B72AF273C00EB6DD3F0C3A14B93B79D\",\"timestamp\":\"1549074425\"}";
                PayParams<String> payParams = new PayParams<>("123456", 2, extra);

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
            ModuleBus.getInstance().of(Gen$biz_login$Interface.class).Gen$LoginEvent$Method().post("xxx");
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

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        lbs.startLocate(new ILocateResultCallback() {
            @Override
            public void onLocation(LocationInfoEntity info) {
                if (info != null) {

                }
            }
        });
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }



}