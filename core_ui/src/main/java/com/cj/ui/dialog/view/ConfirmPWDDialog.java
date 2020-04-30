package com.cj.ui.dialog.view;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.cj.ui.R;
import com.cj.ui.dialog.callback.PWDDialogCallback;


/**
 * Author:chris - jason
 * Date:2019-06-27.
 * Package:com.cj.ui.dialog.view
 * 输入密码对话框，带文案+两个按钮 +输入框
 */
public class ConfirmPWDDialog extends BaseDialogView {

    private TextView mTVContent,mTVNegative,mTVPositive;
    private PWDDialogCallback callback;
    private EditText et;
    public ConfirmPWDDialog(@NonNull Context context, String content, String negative, String positive, PWDDialogCallback callback) {
        super(context);

        setCancelable(true);//点击外部区域可取消
        setCanceledOnTouchOutside(true);//点击返回键可取消

        this.callback = callback;
        mTVContent.setText(content);
        mTVNegative.setText(negative);
        mTVPositive.setText(positive);
    }

    @Override
    protected void bindView(View root) {

        mTVContent = root.findViewById(R.id.tv_content);
        mTVNegative = root.findViewById(R.id.rtv_negative_btn);
        mTVPositive = root.findViewById(R.id.rtv_positive_btn);

        mTVNegative.setOnClickListener(this);
        mTVPositive.setOnClickListener(this);
        et = root.findViewById(R.id.et);

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int vid = v.getId();
        if(callback==null){
            dismiss();
            return;
        }

        if(R.id.rtv_negative_btn == vid){
            callback.onNegativeClicked();
        }

        if(R.id.rtv_positive_btn == vid){
            callback.onPositiveClicked(et.getText().toString());
        }

        dismiss();

    }

    @Override
    protected int setDialogLayout() {
        return R.layout.core_ui_confirm_pwd_dialog_layout;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(callback!=null){
            callback.onDismiss();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if(callback!=null){
            callback.onCancel();
        }
    }
}