package com.cj.common.interceptor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.annotation.Interceptor;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.cj.common.util.kv.DiskCacheUtil;
import com.cj.common.var.KeyTag;
import com.cj.common.var.AsyncNotifyCode;
/**
 * Created by mayikang on 17/2/8.
 */

/**
 * 用户登录拦截器
 * priority 越小，优先级越高
 * 所有拦截器会在程序初始化的时候完成注入
 */
@Interceptor(priority = 1, name = "login-interceptor")
public class LoginInterceptor implements IInterceptor {
    private final String TAG =getClass().getSimpleName();
    private Context mContext;
    private InterceptorCallback mCallback;
    private Postcard mPostcard;

    /**
     * 所有路由操作都要进入这个拦截器
     * 1.Activity：组件代号/ACT/{USR}/activity_name
     * <p>
     * 2.Fragment：组件代号/FRG/fragment_name
     * <p>
     * 3.组件服务：组件代号/SEV/service_name
     *
     * @param postcard
     * @param callback
     */

    @Override
    public void process(Postcard postcard, final InterceptorCallback callback) {

        /**需要登录的页面**/
        if (postcard.getPath().contains(KeyTag.TAG_USER_NEED_LOGIN)) {
            mPostcard = postcard;
            mCallback = callback;
            //todo 未登录，被拦截，跳转登录页面
            if (TextUtils.isEmpty(DiskCacheUtil.getInstance().getToken())) {

                //ARouter.getInstance().build(TargetPage.PAGE_AFTER_LOGIN_INTERCEPTED).navigation();
            } else {
                callback.onContinue(postcard);
            }

        } else {
            /**无需登录**/
            callback.onContinue(postcard);
        }

    }

    @Override
    public void init(Context context) {
        mContext = context;

        //注册广播接收器
        LoginResultReceiver accountReceiver = new LoginResultReceiver();
        IntentFilter intentFilter = new IntentFilter(KeyTag.TAG_LOGIN_FILTER);
        LocalBroadcastManager.getInstance(context).registerReceiver(accountReceiver, intentFilter);

    }


    /**
     * 接收用户登录状态的广播
     */
    public class LoginResultReceiver extends BroadcastReceiver {

        /**
         * 登录成功后发送本地广播
         * Intent intent=new Intent(Tag.TAG_LOGIN_FILTER);
         * intent.putExtra(TAG_LOGIN_RESULT,CODE_LOGIN_SUCCESS);
         * LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
         */
        public LoginResultReceiver() {

        }

        @Override
        public void onReceive(Context context, Intent intent) {

            int result = intent.getIntExtra(KeyTag.TAG_LOGIN_RESULT, 0);

            switch (result) {
                case AsyncNotifyCode.CODE_LOGIN_SUCCESS:
                    Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show();
                    mCallback.onContinue(mPostcard);
                    break;
                case AsyncNotifyCode.CODE_LOGIN_FAILED:
                    Toast.makeText(context, "登录失败", Toast.LENGTH_SHORT).show();
                    mCallback.onInterrupt(new Throwable("登录失败"));
                    break;
                case AsyncNotifyCode.CODE_LOGIN_ERROR:
                    Toast.makeText(context, "登录错误", Toast.LENGTH_SHORT).show();
                    mCallback.onInterrupt(new Throwable("登录错误"));
                    break;

            }
        }
    }


}
