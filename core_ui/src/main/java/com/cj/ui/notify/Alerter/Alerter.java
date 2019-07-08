package com.cj.ui.notify.Alerter;

import android.content.Context;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.view.ViewCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.cj.ui.R;

/**
 * Created by mayikang on 2018/7/26.
 */

public class Alerter extends FrameLayout implements View.OnClickListener, Animation.AnimationListener {

    private AlerterListener listener;
    private Context context;
    private RelativeLayout mRLContainer;
    private TextView mTVTitle, mTVMessage;
    private AppCompatImageView mIVIcon;
    private int contentGravity;//预留着，以后用来调整Alert的位置
    private Animation slideInAnimation, slideOutAnimation;

    //Alert展示时间
    private long duration = 2500;

    //是否开启通知图标收缩跳动
    private boolean enableIconPulse = true;

    //是否自动收起
    private boolean autoCollapse = true;

    //是否振动
    private boolean vibrationEnabled = true;

    private boolean marginSet = false;

    private boolean isShowing = false;

    public Alerter(@NonNull Context context) {
        super(context, null, 0);
        this.context = context;
        inflate(context, R.layout.core_ui_alerter_alert_view, this);
        initView();
    }

    public Alerter(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Alerter(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onClick(View v) {
        if (isShowing)
            hide();
    }

    @Override
    public void onAnimationStart(Animation animation) {

        if (!isInEditMode()) {
            setVisibility(View.VISIBLE);
            if (vibrationEnabled) {
                performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            }
        }
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        //Alert滑到屏幕展示
        isShowing = true;

        if (enableIconPulse && mIVIcon.getVisibility() == View.VISIBLE) {
            try {
                mIVIcon.startAnimation(AnimationUtils.loadAnimation(context, R.anim.core_ui_alerter_pulse));
            } catch (Exception ex) {
                Log.e("myk", Log.getStackTraceString(ex));
            }
        }

        if (listener != null) {
            listener.onShow();
        }

        startAutoHideAnimation();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        Log.e("myk", "in repeat");
    }

    private void startAutoHideAnimation() {

        //支持自动收起
        if (autoCollapse) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    hide();
                }
            }, duration);

        }

    }

    private void initView() {
        //bind view
        mRLContainer = findViewById(R.id.core_ui_rlContainer);
        mTVTitle = findViewById(R.id.core_ui_tvTitle);
        mTVMessage = findViewById(R.id.core_ui_tvText);
        mIVIcon = findViewById(R.id.core_ui_ivIcon);

        //set layout params
        FrameLayout.LayoutParams lp = (LayoutParams) mRLContainer.getLayoutParams();
        contentGravity = lp.gravity;

        LinearLayout.LayoutParams paramsTitle = (LinearLayout.LayoutParams) mTVTitle.getLayoutParams();
        paramsTitle.gravity = contentGravity;
        mTVTitle.setLayoutParams(paramsTitle);

        LinearLayout.LayoutParams paramsText = (LinearLayout.LayoutParams) mTVMessage.getLayoutParams();
        paramsText.gravity = contentGravity;
        mTVMessage.setLayoutParams(paramsText);

        //允许振动反馈
        setHapticFeedbackEnabled(true);

        ViewCompat.setTranslationZ(this, Integer.MAX_VALUE);
        mRLContainer.setOnClickListener(this);


        slideInAnimation = AnimationUtils.loadAnimation(this.context, R.anim.core_ui_alerter_slide_in_from_top);
        slideOutAnimation = AnimationUtils.loadAnimation(context, R.anim.core_ui_alerter_slide_out_to_top);

        slideInAnimation.setAnimationListener(this);

        //Set Animation to be Run when View is added to Window
        setAnimation(slideInAnimation);
    }

    public void setListener(AlerterListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!marginSet) {
            marginSet = true;
            ViewGroup.MarginLayoutParams lp = (MarginLayoutParams) getLayoutParams();
            lp.topMargin = context.getResources().getDimensionPixelSize(R.dimen.core_ui_alerter_alert_negative_margin_top);
            requestLayout();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isShowing = false;
        slideInAnimation.setAnimationListener(null);
        slideOutAnimation.setAnimationListener(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        performClick();
        return super.onTouchEvent(event);
    }

    @Override
    public void setVisibility(int visibility) {
        for (int i = 0; i < this.getChildCount(); i++) {
            this.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }

    private void hide() {
        try {
            setAnimation(slideOutAnimation);
            slideOutAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mRLContainer.setOnClickListener(null);
                    mRLContainer.setClickable(false);
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (listener != null) {
                        listener.onHide();
                    }
                    removeFromParent();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    Log.e("myk", "out repeat");
                }

            });
        } catch (Exception ex) {
            Log.e("myk", Log.getStackTraceString(ex));
        }

        startAnimation(slideOutAnimation);
    }

    private void removeFromParent() {
        //设置了fillafeter 要先调用clearAnimation 才能设置visibility
        //clearAnimation();
        setVisibility(View.GONE);
        //先隐藏再清楚动画，否则会闪烁
        post(new Runnable() {
            @Override
            public void run() {
                ViewGroup parent = (ViewGroup) getParent();
                if (parent != null) {
                    parent.removeView(Alerter.this);
                }
            }
        });
    }

    public void setAutoCollapse(boolean autoCollapse) {
        this.autoCollapse = autoCollapse;
    }

    public void setIcon(@DrawableRes int drawableRes) {
        if (drawableRes > 0) {
            mIVIcon.setVisibility(View.VISIBLE);
            mIVIcon.setImageResource(drawableRes);
        }

    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            mTVTitle.setVisibility(View.VISIBLE);
            mTVTitle.setText(title);
        }
    }

    public void setMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
            mTVMessage.setVisibility(View.VISIBLE);
            mTVMessage.setText(message);
        }
    }

    public void setBackground(int color){
        mRLContainer.setBackgroundColor(color);
    }

}
