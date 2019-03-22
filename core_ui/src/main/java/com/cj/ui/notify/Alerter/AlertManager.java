package com.cj.ui.notify.Alerter;

import android.app.Activity;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.v4.view.ViewCompat;
import android.view.ViewGroup;

import com.cj.ui.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayikang on 2018/7/26.
 */

public class AlertManager {

    private static AlertManager manager = null;

    private AlertManager() {
    }

    private static ViewGroup mDecorView;

    private static WeakReference<Activity> weakReference = null;

    private static Alerter alerter = null;

    public static AlertManager create(Activity act) {

        if (act == null) {
            throw new IllegalArgumentException("Activity cannot be null!");
        }


        manager = new AlertManager();

        mDecorView = (ViewGroup) act.getWindow().getDecorView();

        clearCurrent(act);

        setActivity(act);

        alerter = new Alerter(act);

        return manager;

    }

    public static void setActivity(Activity act) {
        weakReference = new WeakReference(act);
    }

    public AlertManager setAlertListener(AlerterListener listener) {
        alerter.setListener(listener);
        return this;
    }

    public AlertManager setIcon(@DrawableRes int drawableRes) {
        alerter.setIcon(drawableRes);
        return this;
    }

    public AlertManager setBackgroundColor(@ColorInt int backgroundColor){
        alerter.setBackground(backgroundColor);
        return this;
    }

    //设置title
    public AlertManager setTitle(String title) {
        alerter.setTitle(title);
        return this;
    }

    //设置message
    public AlertManager setMessage(String message) {
        alerter.setMessage(message);
        return this;
    }

    private static void clearCurrent(Activity activity) {

        if (activity == null) {
            return;
        }

        if (mDecorView == null)
            return;
        //可能已经往decorview中添加了子view，这里重新获取一遍
        ViewGroup p = (ViewGroup) activity.getWindow().getDecorView();

        if (p != null) {
            for (int i = 0; i < p.getChildCount(); i++) {
                Alerter alerter = null;
                if (p.getChildAt(i) instanceof Alerter) {
                    alerter = (Alerter) p.getChildAt(i);
                    if (alerter != null && alerter.getWindowToken() != null) {
                        ViewCompat.animate(alerter).alpha(0f).withEndAction(getRemoveViewRunnable(alerter));
                    }
                }
            }
        }

    }

    private static Runnable getRemoveViewRunnable(final Alerter view) {

        return new Runnable() {
            @Override
            public void run() {
                if (view.getParent() instanceof ViewGroup) {
                    ((ViewGroup) view.getParent()).removeView(view);
                }
            }
        };
    }

    public Alerter show() {
        if (weakReference != null && weakReference.get() != null && this.alerter != null) {
            weakReference.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mDecorView != null) {
                        mDecorView.addView(alerter);
                    }
                }
            });
        }
        return alerter;
    }

    public AlertManager setAutoCollapse(boolean autoCollapse) {
        alerter.setAutoCollapse(autoCollapse);
        return this;
    }

}
