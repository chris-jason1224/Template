package com.cj.ui.dialog;

import android.content.Context;

import com.cj.ui.dialog.callback.BaseDialogCallback;
import com.cj.ui.dialog.callback.DialogCallback;
import com.cj.ui.dialog.callback.ItemClickCallback;
import com.cj.ui.dialog.callback.PWDDialogCallback;
import com.cj.ui.dialog.view.BottomOptionsView;
import com.cj.ui.dialog.view.ConfirmDialog;
import com.cj.ui.dialog.view.ConfirmPWDDialog;
import com.cj.ui.dialog.view.MessageDialog;

import java.util.List;

/**
 * Author:chris - jason
 * Date:2019-06-27.
 * Package:com.cj.ui.dialog
 */
public class DialogUtil {

    private DialogUtil() { }

    private static class Holder {
        private static final DialogUtil instance = new DialogUtil();
    }

    public static DialogUtil getInstance() {
        return Holder.instance;
    }



    /**
     * 弹出消息确认对话框 ，带有单行文案+确认按钮
     * @param context
     * @param content
     * @param btn
     * @param callback
     */
    public void showMessageDialog(Context context, String content, String btn, BaseDialogCallback callback) {
        MessageDialog dialog = new MessageDialog(context, content, btn, callback);
        dialog.show();
    }

    /**
     * 弹出确认对话框 带有文案+两个按钮
     * @param context
     * @param content
     * @param negative
     * @param positive
     * @param callback
     */
    public void showConfirmDialog(Context context, String content, String negative, String positive, DialogCallback callback) {
        ConfirmDialog dialog = new ConfirmDialog(context, content, negative, positive, callback);
        dialog.show();
    }

    /**
     * 弹出输入密码对话框
     * @param context
     * @param content
     * @param negative
     * @param positive
     * @param callback
     */
    public void showConfirmPWDDialog(Context context, String content, String negative, String positive, PWDDialogCallback callback){
        ConfirmPWDDialog dialog  = new ConfirmPWDDialog(context,content,negative,positive,callback);
        dialog.show();
    }

    /**
     * 底部选择框
     * @param context
     * @param options
     * @param callback
     */
    public void showBottomOptionsDialog(Context context, List<String> options, ItemClickCallback callback){
        BottomOptionsView dialog =  new BottomOptionsView(context,options,callback);
        dialog.show();
    }

    /**
     * 底部选择框 带标题
     * @param context
     * @param title 标题
     * @param options
     * @param callback
     */
    public void showBottomOptionsDialog(Context context,String title,List<String> options, ItemClickCallback callback){
        BottomOptionsView dialog =  new BottomOptionsView(context,title,options,callback);
        dialog.show();
    }

}
