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
        //message一般是推送下来的主要消息体
        String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
        //extras一般是json格式的附带数据
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        PushCenter.getInstance().dispatchPush(message,extras);
    }


    /****弹出通知消息*********/
//    private void showNotification(Context c,String channelID,String channelName,String title,String message){
//
//        Intent intent = new Intent();
//        intent.setPackage(c.getPackageName());//android 8.0 适配静态广播无法接受隐式intent的问题
//
//        //todo 根据业务需求决定intent跳转到哪个页面
//        intent.setClass(c, BonusDetailsActivity.class);
//        intent.putExtra("id", entity.getBizID());
//
//        NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(c, "jpush");
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            builder.setSmallIcon(R.mipmap.app_logo);
//            //Android 8.0 必须添加渠道
//            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
//            notificationManager.createNotificationChannel(channel);
//            builder.setChannelId(channelID);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            //Android 7.0
//            builder.setSmallIcon(R.mipmap.app_logo);
//        } else {
//            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M) {
//                //android 6.0  必须设置大图和小图，小图的图片尺寸24x24，图案透明
//                builder.setLargeIcon(BitmapFactory.decodeResource(c.getResources(), R.mipmap.app_logo));
//                builder.setSmallIcon(R.mipmap.notification_samll_app_logo);
//                builder.setColor(c.getResources().getColor(R.color.colorPrimary));
//            } else {
//                //Android 6.0以下
//                builder.setSmallIcon(R.mipmap.app_logo);
//            }
//        }
//        builder.setContentTitle(title);
//        //魅族手机的坑
//        message = message.length() > 44 ? message.substring(0, 44) : message;
//        message = message.replace("!", "。").replace("！", "。");
//        builder.setContentText(message);
//        //设置点击通知跳转页面后，通知消失
//        builder.setDefaults(Notification.DEFAULT_ALL);
//        builder.setAutoCancel(true);
//
//
//        //设置优先级
//        builder.setPriority(NotificationCompat.PRIORITY_MAX);
//        builder.setWhen(System.currentTimeMillis());
//        PendingIntent pi = null;
//        if (intent.hasExtra("pushType")) {
//            pi = PendingIntent.getBroadcast(c, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        } else {
//            pi = PendingIntent.getActivity(c, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        }
//        builder.setContentIntent(pi);
//        Notification notification = builder.build();
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        int notifyId = (int) System.currentTimeMillis();
//
//        notificationManager.notify(notifyId, notification);
//    }


}
