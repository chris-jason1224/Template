package com.cj.ui.dialog;

import android.app.Activity;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;

/**
 * Author:chris-jason
 * <p>
 * Date:2019/3/23 0023
 * <p>
 * Package:com.cj.ui.dialog
 */


public class DialogUtil {

    private DialogUtil(){

    }

    public static DialogUtil getInstance(){
        return Holder.instance;
    }

    private static class Holder{
        private static final DialogUtil instance = new DialogUtil();
    }


    /**
     *
     * @param message message
     * @param ContentView 自定义的view
     * @param leftButton 左边按钮文字
     * @param color_left 左边按钮颜色
     * @param rightButton 右边按钮文字
     * @param color_right 右边按钮颜色
     */
    public void showMessageDialog(@NonNull Activity activity, @Nullable String message ,@Nullable View ContentView ,
                                  @Nullable String leftButton , int color_left , @Nullable String rightButton,
                                  int color_right,@Nullable final ClickCallback clickCallback){

        MessageDialog dialog = new MessageDialog(activity, new MessageDialog.MessageDialogCallback() {
            @Override
            public void onLeftButtonClick() {
                if(clickCallback!=null){
                    clickCallback.onClick(0);
                }
            }

            @Override
            public void onRightButtonClick() {
                if(clickCallback!=null){
                    clickCallback.onClick(1);
                }
            }
        });
        dialog.setMessage(message);
        dialog.setCustomerView(ContentView);

        dialog.setLeftButton(leftButton);
        dialog.setLeftColor(color_left);

        dialog.setRightButton(rightButton);
        dialog.setRightColor(color_right);

        dialog.show();
    }

    public void showMessageDialog(@NonNull Activity activity, @Nullable String message ,@Nullable View ContentView ,
                                  @Nullable String leftButton ,  @Nullable String rightButton, @Nullable final ClickCallback clickCallback){

        MessageDialog dialog = new MessageDialog(activity, new MessageDialog.MessageDialogCallback() {
            @Override
            public void onLeftButtonClick() {
                if(clickCallback!=null){
                    clickCallback.onClick(0);
                }
            }

            @Override
            public void onRightButtonClick() {
                if(clickCallback!=null){
                    clickCallback.onClick(1);
                }
            }
        });
        dialog.setMessage(message);
        dialog.setCustomerView(ContentView);

        dialog.setLeftButton(leftButton);

        dialog.setRightButton(rightButton);

        dialog.show();
    }

    public void showMessageDialog(@NonNull Activity activity, @Nullable String message,@Nullable String leftButton ,
                                  @Nullable String rightButton, @Nullable final ClickCallback clickCallback){

        MessageDialog dialog = new MessageDialog(activity, new MessageDialog.MessageDialogCallback() {
            @Override
            public void onLeftButtonClick() {
                if(clickCallback!=null){
                    clickCallback.onClick(0);
                }
            }

            @Override
            public void onRightButtonClick() {
                if(clickCallback!=null){
                    clickCallback.onClick(1);
                }
            }
        });
        dialog.setMessage(message);

        dialog.setLeftButton(leftButton);
        dialog.setRightButton(rightButton);

        dialog.show();
    }


    public void showMessageDialog(@NonNull Activity activity, @Nullable String message, @Nullable final ClickCallback clickCallback){

        MessageDialog dialog = new MessageDialog(activity, new MessageDialog.MessageDialogCallback() {
            @Override
            public void onLeftButtonClick() {
                if(clickCallback!=null){
                    clickCallback.onClick(0);
                }
            }

            @Override
            public void onRightButtonClick() {
                if(clickCallback!=null){
                    clickCallback.onClick(1);
                }
            }
        });
        dialog.setMessage(message);

        dialog.show();
    }


    public interface ClickCallback{
        void onClick(int position);
    }

}
