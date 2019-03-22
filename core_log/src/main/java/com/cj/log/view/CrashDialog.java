package com.cj.log.view;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cj.log.R;


/**
 * Created by mayikang on 2018/10/16.
 */

public class CrashDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private ClickCallback callback;
    private ProgressBar progressBar;
    private TextView tvMsg;
    private LinearLayout llRestart;

    public CrashDialog(@NonNull Context context, ClickCallback callback) {
        super(context, R.style.core_log_crash_dialog_style);
        this.callback = callback;
        init(context);
    }


    private void init(Context context) {
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);

        View windowView = inflater.inflate(R.layout.core_log_crash_dialog_layout, null);
        //set view
        setContentView(windowView);

        //点击外部区域不可取消
        setCancelable(false);

        //点击返回键不可取消
        setCanceledOnTouchOutside(false);

        Window window = getWindow();
        window.getDecorView().setPadding(dip2px(context, 20f), 0, dip2px(context, 20f), 0);
        //显示在中间
        window.setGravity(Gravity.CENTER);

        //设置宽高
        WindowManager.LayoutParams params = window.getAttributes();

        Display display = getWindow().getWindowManager().getDefaultDisplay();

        //设置宽度全屏
        params.width = display.getWidth();

        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);

        //bind view
        setView(windowView);

    }


    public void setView(View view) {
        view.findViewById(R.id.rl_close).setOnClickListener(this);
        view.findViewById(R.id.rl_restart).setOnClickListener(this);
        progressBar = view.findViewById(R.id.progress);
        tvMsg = view.findViewById(R.id.tv_msg);
        llRestart = view.findViewById(R.id.ll_restart);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (R.id.rl_close == id) {
            dismiss();
            callback.onClickClose();
            return;
        }

        if (R.id.rl_restart == id) {
            callback.onClickRestart();
            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) llRestart.getLayoutParams();
            lp.leftMargin = 0;
            lp.rightMargin = dip2px(context, 22);
            llRestart.setLayoutParams(lp);
            progressBar.setVisibility(View.VISIBLE);
            tvMsg.setText("重启中...");
            return;
        }

    }

    public interface ClickCallback {
        void onClickClose();

        void onClickRestart();
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
