package com.cj.share_login.share;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.cj.share_login.R;
import com.cj.share_login.share.entity.SharePlatformType;

/**
 * Created by mayikang on 2017/11/22.
 */

/**
 * 聚合的社会化分享弹出窗口
 */
public class PopShareViewUtil extends Dialog implements View.OnClickListener {

    private ShareClick click;
    boolean cancelAble = true;
    private Context context;

    public PopShareViewUtil(@NonNull Context context,ShareClick click) {
        //全部宽度dialog样式
        super(context,R.style.share_login_full_width_dialog);
        this.context=context;
        this.click=click;
        init();
    }

    private void init(){

        View v = LayoutInflater.from(context).inflate(R.layout.share_login_combine_social_share_pop_view_layout, null);

        if (v == null) {
            return;
        }

        setContentView(v);
        //点击返回键可取消
        setCanceledOnTouchOutside(true);
        //点击外部可取消
        setCancelAble(cancelAble);
        //获取窗口
        Window window = getWindow();
        //显示在底部
        window.setGravity(Gravity.BOTTOM);
        //设置宽高
        WindowManager.LayoutParams params = window.getAttributes();
        Display display=getWindow().getWindowManager().getDefaultDisplay();

        params.width = display.getWidth();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);



        initView(v);

    }



    public void setCancelAble(boolean cancelAble) {
        this.cancelAble = cancelAble;
    }



    @Override
    public void onClick(View v) {

        if (click == null) {
            return;
        }

        int id=v.getId();

        if(id==R.id.rl_share_to_wechat){
            click.onClick(SharePlatformType.PLATFORM_WE_CHAT_CONVERSION);
        }

        if(id==R.id.rl_share_to_wechat_timeline){
            click.onClick(SharePlatformType.PLATFORM_WE_CHAT_TIMELINE);
        }

        if(id==R.id.rl_share_to_qq){
            click.onClick(SharePlatformType.PLATFORM_QQ_CONVERSION);
        }

        if(id==R.id.rl_share_to_qzone){
            click.onClick(SharePlatformType.PLATFORM_QQ_ZONE);
        }

        if(id==R.id.rl_share_to_sina_weibo){
            click.onClick(SharePlatformType.PLATFORM_SINA_WEI_BO);
        }

        if(id==R.id.tv_cancel_share){
            dismiss();
        }

        if (cancelAble) {
           dismiss();
        }

    }


    void initView(View v) {
        v.findViewById(R.id.tv_cancel_share).setOnClickListener(this);
        v.findViewById(R.id.rl_share_to_wechat).setOnClickListener(this);
        v.findViewById(R.id.rl_share_to_wechat_timeline).setOnClickListener(this);
        v.findViewById(R.id.rl_share_to_qq).setOnClickListener(this);
        v.findViewById(R.id.rl_share_to_qzone).setOnClickListener(this);
        v.findViewById(R.id.rl_share_to_sina_weibo).setOnClickListener(this);
    }


    public interface ShareClick {
        void onClick(int type);
    }

}
