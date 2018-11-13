package com.cj.common.http.base;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by mayikang on 2018/8/20.
 * 统一封装的网络请求返回实体，M为实际的数据泛型
 */

public class BaseHttpResultEntity<M> implements Serializable{
    //code对应接口的code字段
    @SerializedName("code")
    private int errorCode;

    //data对应接口的data字段
    @SerializedName("data")
    private M data;

    //msg对应接口的msg字段
    @SerializedName("msg")
    private String message;

    public BaseHttpResultEntity(){

    }

    public BaseHttpResultEntity(int errorCode, M data, String message) {
        this.errorCode = errorCode;
        this.data = data;
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public M getData() {
        return data;
    }

    public void setData(M data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
