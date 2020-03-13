package com.cj.ui.dialog.view;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.cj.ui.R;
import com.cj.ui.dialog.callback.BaseDialogCallback;


/**
 * Author:chris - jason
 * Date:2019-06-27.
 * Package:com.cj.ui.dialog.view
 * dialog 带有单行文案 + 一个按钮
 */
public class MessageDialog extends BaseDialogView {

    private TextView mTVContent, mTVConfirm;
    private BaseDialogCallback callback;

    public MessageDialog(@NonNull Context context, String content, @Nullable String btn, BaseDialogCallback callback) {
        super(context);
        this.callback = callback;

        setCancelable(true);//点击外部区域可取消
        setCanceledOnTouchOutside(true);//点击返回键可取消

        mTVContent.setText(content);
        mTVConfirm.setText(btn);
    }

    @Override
    protected void bindView(View root) {
        mTVContent = root.findViewById(R.id.tv_content);
        mTVConfirm = root.findViewById(R.id.rtv_confirm_btn);

        mTVConfirm.setOnClickListener(this);
    }

    @Override
    protected int setDialogLayout() {
        return R.layout.core_ui_message_dialog_layout;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int vid = v.getId();

        if (callback == null) {
            dismiss();
            return;
        }

        if (R.id.rtv_confirm_btn == vid) {
            callback.onPositiveClicked();
            dismiss();
        }

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
