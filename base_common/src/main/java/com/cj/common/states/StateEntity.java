package com.cj.common.states;

import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.cj.common.states.StateEntity.StateCode.EMPTY_LAYOUT;
import static com.cj.common.states.StateEntity.StateCode.PLACEHOLDER_LAYOUT;
import static com.cj.common.states.StateEntity.StateCode.SUCCESS_LAYOUT;
import static com.cj.common.states.StateEntity.StateCode.TIMEOUT_LAYOUT;

/**
 * Author:chris - jason
 * Date:2019/3/29.
 * Package:com.cj.common.states
 * 多布局页面状态实体
 */
public class StateEntity {


    //要显示的布局字典值
    @IntDef({SUCCESS_LAYOUT, EMPTY_LAYOUT,PLACEHOLDER_LAYOUT,TIMEOUT_LAYOUT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface StateCode {
        int SUCCESS_LAYOUT = 1;
        int EMPTY_LAYOUT = 2;
        int PLACEHOLDER_LAYOUT = 3;
        int TIMEOUT_LAYOUT = 4;
    }

    private int state;
    private int drawableRes;
    private String message;


    public StateEntity(int state) {
        this.state = state;
    }

    public StateEntity(int state, String message) {
        this.state = state;
        this.message = message;
    }

    public StateEntity(int state, @DrawableRes int drawableRes, String message) {
        this.state = state;
        this.drawableRes = drawableRes;
        this.message = message;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getDrawableRes() {
        return drawableRes;
    }

    public void setDrawableRes(int drawableRes) {
        this.drawableRes = drawableRes;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
