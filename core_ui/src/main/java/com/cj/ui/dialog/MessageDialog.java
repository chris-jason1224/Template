package com.cj.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import androidx.annotation.NonNull;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cj.ui.R;

/**
 * Author:chris - jason
 * Date:2019/4/1.
 * Package:com.cj.ui.dialog
 */
public class MessageDialog extends BaseDialogView implements View.OnClickListener {

    private MessageDialogCallback callback;
    private TextView mTVMessage;
    private RelativeLayout mRLContentView;
    private RelativeLayout mRLButtonLeft, mRLButtonRight;
    private TextView mTVButtonLeft, mTVButtonRight;

    private int color_red;
    private int color_black;
    private int color_blue;

    public MessageDialog(@NonNull Context context,MessageDialogCallback callback) {
        super(context);
        this.mContext = context;
        this.callback = callback;
    }

    @Override
    protected void bindView(View root) {
        color_black = Color.parseColor("#333333");
        color_red = Color.parseColor("#F3363B");
        color_blue = Color.parseColor("#0070FF");

        mTVMessage = findViewById(R.id.tv_message);
        mRLContentView = findViewById(R.id.rl_content_view);

        mRLButtonLeft = findViewById(R.id.rl_button_left);
        mTVButtonLeft = findViewById(R.id.tv_button_left);

        mRLButtonRight = findViewById(R.id.rl_button_right);
        mTVButtonRight = findViewById(R.id.tv_button_right);

        mRLButtonLeft.setOnClickListener(this);
        mRLButtonRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int i = v.getId();

        if (callback != null) {

            if (i == R.id.rl_button_left) {
                callback.onLeftButtonClick();
            } else if (i == R.id.rl_button_right) {
                callback.onRightButtonClick();
            }

        }

    }

    @Override
    protected int setDialogLayout() {
        return R.layout.core_ui_base_dialog_layout;
    }

    public interface MessageDialogCallback {
        void onLeftButtonClick();

        void onRightButtonClick();
    }

    public void setMessage(String msg){

        if(TextUtils.isEmpty(msg)){
            return;
        }

        if(mTVMessage!=null){
            mTVMessage.setText(msg);
        }

    }

    public void setCustomerView(View contentView){

        if(contentView==null){
            return;
        }

        if(mRLContentView!=null){
            mRLContentView.removeAllViews();
            mRLContentView.addView(contentView);
        }

    }

    public void setLeftButton(String left){

        if(TextUtils.isEmpty(left)){
            return;
        }

        if(mTVButtonLeft!=null){
            mTVButtonLeft.setText(left);
        }
    }

    public void setLeftColor(int color){
        if(mTVButtonLeft!=null){
            mTVButtonLeft.setTextColor(color);
        }
    }

    public void setRightButton(String right){

        if(TextUtils.isEmpty(right)){
            return;
        }
        if(mTVButtonRight!=null){
            mTVButtonRight.setText(right);
        }
    }

    public void setRightColor(int color){
        if(mTVButtonRight!=null){
            mTVButtonRight.setTextColor(color);
        }
    }

}
