package com.cj.main.test;

import java.io.Serializable;

/**
 * SpringMVC 方式写的第一个model
 */
public class UserModel implements Serializable {
    private int id;
    private String userId;
    private String account;
    private String pwd;
    private int isDelete;

    public UserModel() {
    }

    public UserModel(String account, String pwd, int isDelete) {
        this.account = account;
        this.pwd = pwd;
        this.isDelete = isDelete;
    }

    public UserModel(int id, String userId, String account, String pwd) {
        this.id = id;
        this.userId = userId;
        this.account = account;
        this.pwd = pwd;
    }

    public int getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(int isDelete) {
        this.isDelete = isDelete;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
