package com.cj.ui.dialog.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;

import com.cj.ui.R;

/**
 * Author:chris - jason
 * Date:2019-06-27.
 * Package:com.cj.ui.dialog.view
 * 通用Dialog弹窗基类
 */
public abstract class BaseDialogView extends Dialog implements View.OnClickListener,Dialog.OnDismissListener, DialogInterface.OnCancelListener {

    //Dialog弹出需要依赖于activity
    protected Context mContext;
    protected View mContentView;

    public BaseDialogView(@NonNull Context context) {
        this(context, R.style.core_ui_common_dialog);
    }

    public BaseDialogView(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;

        //初始化属性
        initAttr();
        setOnDismissListener(this);
        setOnCancelListener(this);
    }

    private void initAttr() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View windowView = inflater.inflate(setDialogLayout(), null);

        //set view
        super.setContentView(windowView);
        this.mContentView = windowView;

        Window window = getWindow();
        window.setGravity(Gravity.CENTER);//显示在中间

        //设置宽高
        WindowManager.LayoutParams params = window.getAttributes();
        Display display = getWindow().getWindowManager().getDefaultDisplay();

        params.width = display.getWidth();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        //bind view
        bindView(windowView);
    }

    //绑定控件
    protected abstract void bindView(View root);


    @Override
    public void onDismiss(DialogInterface dialog) {

    }

    @Override
    public void onClick(View v) {

    }

    //dialog设置布局
    protected abstract @LayoutRes int  setDialogLayout();

    @Override
    public void show() {
//        // Dialog 在初始化时会生成新的 Window，先禁止 Dialog Window 获取焦点，
//        // 等 Dialog 显示后对 Dialog Window 的 DecorView 设置 setSystemUiVisibility ，
//        // 接着再获取焦点。 这样表面上看起来就没有退出沉浸模式。
//        // Set the dialog to not focusable (makes navigation ignore us adding the window)
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
//
//        //Show the dialog!
//        super.show();
//
//        //Set the dialog to immersive
//        fullScreenImmersive(getWindow().getDecorView());
//
//        //Clear the not focusable flag from the window
//        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

    }
    /**
     * 全屏显示，隐藏虚拟按钮
     * @param view
     */
    private void fullScreenImmersive(View view) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            view.setSystemUiVisibility(uiOptions);
        }
    }

}
