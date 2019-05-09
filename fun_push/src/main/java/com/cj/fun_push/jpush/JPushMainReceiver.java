package com.cj.fun_push.jpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.cj.fun_push.PushCenter;
import com.cj.log.CJLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义极光推送消息接收器
 */
public class JPushMainReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            //推送携带的数据
            Bundle bundle = intent.getExtras();

            //打印完整的intent信息
            CJLog.getInstance().log_d("[MyJPushReceiver] onReceive - " + intent.getAction() + ", extras: " + decodeBundleInfo(bundle));

            /**
             * 接收到 Registration Id
             */
            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                return;
            }

            /**
             * 接收到推送下来的自定义消息
             */
            if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
                String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
                processCustomMessage(context, bundle);
                return;
            }

            /**
             * 接收到推送下来的通知
             * @param notificationId  接收到推送下来的通知的ID
             */
            if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
                return;
            }

            /**
             * [JPushMainReceiver] 用户点击打开了通知
             */
            if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
                //todo 根据业务打开页面
                String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
                return;
            }

            /**
             * 用户收到到RICH PUSH CALLBACK
             */
            if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
                String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);
                return;
            }

            /**
             * 连接状态改变
             */
            if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //解析 intent extra 数据
    private static String decodeBundleInfo(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
                    CJLog.getInstance().log_e("This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    CJLog.getInstance().log_e("Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.get(key));
            }
        }
        return sb.toString();
    }

    //处理接收到的消息
    private void processCustomMessage(Context context, Bundle bundle) {

        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);

        PushCenter.getInstance().dispatchPush(message);

    }

}
