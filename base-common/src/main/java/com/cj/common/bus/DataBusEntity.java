package com.cj.common.bus;

/**
 * Created by mayikang on 2018/8/23.
 */

public class DataBusEntity<T> {

    private String key;
    private Class<T> t;


    public DataBusEntity(String key, Class<T> t) {
        this.key = key;
        this.t = t;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Class<T> getT() {
        return t;
    }

    public void setT(Class<T> t) {
        this.t = t;
    }

}
