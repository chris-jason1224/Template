package com.cj.main;

import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cj.common.base.BaseActivity;
import com.cj.common.bus.DataBusKey;
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
import com.cj.log.CJLog;
import com.cj.ui.dialog.DialogUtil;
import com.cj.ui.notify.Alerter.AlertManager;
import com.cj.ui.notify.Alerter.AlerterListener;
import com.cj.utils.io.IOUtil;
import com.cj.utils.list.ListUtil;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;
import com.jeremyliao.liveeventbus.LiveEventBus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.objectbox.query.Query;
import pub.devrel.easypermissions.EasyPermissions;


@Route(path = "/biz_main/ACT/com.cj.main.MainActivity")
public class MainActivity extends BaseActivity implements View.OnClickListener, EasyPermissions.PermissionCallbacks {

    private SimpleDraweeView draweeView;
    private Toolbar toolbar;
    private LinearLayout mLLParent;
    private TextView mTVState;
    private ImageView mIVTest;

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


        LiveEventBus.get().with(DataBusKey.ProcessMainDataEvent.getKey(),DataBusKey.ProcessMainDataEvent.getT()).
                observe(this, new Observer<String>() {
                    @Override
                    public void onChanged(@Nullable String entity) {
                        if(!TextUtils.isEmpty(entity)){
                            CJLog.getInstance().log_e("MainActivity 收到消息："+entity);
                        }
                    }
                });

    }

    @Override
    protected void initView() {
        draweeView = fb(R.id.drawee);
        toolbar = fb(R.id.base_common_toolbar);
        mLLParent = fb(R.id.ll_parent);
        mTVState = fb(R.id.tv_state);
        mIVTest = fb(R.id.iv_test);


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
    }

    @ExecutionTimeTrace
    @SingleSubmit
    @Override
    public void onClick(View v) {

        int vid = v.getId();

        if(R.id.to_locate == vid){
            //6.0以上系统动态申请定位权限
            if(Build.VERSION.SDK_INT >= 23 && getApplicationInfo().targetSdkVersion >= 23){
                if(!EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
                    EasyPermissions.requestPermissions(this,"请授予我定位权限",1,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    return;
                }
            }


            lbs.startLocate(new ILocateResultCallback() {
                @Override
                public void onLocation(LocationInfoEntity info) {
                    if(info!=null){
                        CJLog.getInstance().log_e("定位信息："+info.toString());
                    }
                }
            });
        }

        if(R.id.calculate == vid){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String  str_start = "2018-01-27";
            try {
                Date date_start  = sdf.parse(str_start);
                long diff =System.currentTimeMillis()-date_start.getTime();
                long total = diff/(1000*60*60*24);
                int day =Integer.parseInt(String.valueOf(total));
                AlertManager.create(this).setTitle( sdf.format(date_start) + " - " + sdf.format(new Date())).setMessage("马牛牛和狗狗已经在一起"+day+"天啦啦啦啦啦啦").show();
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
            DialogUtil.getInstance().showMessageDialog(this, "即将删除该消息", new DialogUtil.ClickCallback() {
                @Override
                public void onClick(int position) {

                }
            });
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
                if(info!=null){

                }
            }
        });
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }



}