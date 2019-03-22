package com.cj.common.util;

import android.app.Activity;

import com.cj.common.base.BaseApp;
import com.cj.ui.tip.UITipDialog;

/**
 * Author:chris - jason
 * Date:2019/2/26.
 * Package:com.cj.common.util
 */
//页面加载进度显示、隐藏控制类
public class ProgressUtil {

    private UITipDialog dialog;

    private ProgressUtil(){

    }
    public static ProgressUtil getInstance(){
        return Holder.instance;
    }

    private static class Holder{
        private static final ProgressUtil instance =  new ProgressUtil();
    }

    //显示加载进度
    public void showLoading(){
        try{
            if(dialog==null){
                Activity act = BaseApp.getInstance().getCurrentActivity();
                if(act!=null){
                    dialog = new UITipDialog.Builder(BaseApp.getInstance().getCurrentActivity()).setIconType(UITipDialog.Builder.ICON_TYPE_LOADING).setTipWord("Loading...").create();
                }
            }
            if(dialog!=null){
                dialog.show();
            }

        }catch (Exception e){

        }

    }

    //关闭加载进度
    public void dismissLoading(){
        if(dialog!=null){
            dialog.dismiss();
            dialog =null;
        }
    }


}
