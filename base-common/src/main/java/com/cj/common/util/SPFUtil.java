package com.cj.common.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.cj.common.exception.NotInitException;
import com.tencent.mmkv.MMKV;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * Created by mayikang on 17/2/6.
 * SharedPreferences 管理工具
 */

public class SPFUtil {

    private static Context context;
    private static MMKV mmkv;


    private SPFUtil() {

    }

    private static class Holder {
        private static final SPFUtil instance = new SPFUtil();
    }

    public static SPFUtil getInstance() {
        if (mmkv == null) {
            throw new IllegalStateException("- 请先初始化MMKV -");
        }
        return Holder.instance;
    }

    /**
     * 初始化该工具类
     *
     * @param con
     */
    public static void init(@NonNull Context con) {
        context = con;
        MMKV.initialize(con);
        //设置MMKV跨进程使用
        mmkv = MMKV.mmkvWithID("InterProcessKV", MMKV.MULTI_PROCESS_MODE);
    }


    /**
     * 支持以下 Java 语言基础类型：boolean、int、long、float、double、byte[]
     * 支持以下 Java 类和容器：String、Set<String>
     */

    public void saveBoolean(String key, boolean value) {
        mmkv.encode(key, value);
    }

    public void saveInt(String key, int value) {
        mmkv.encode(key, value);
    }

    public void saveLong(String key, long value) {
        mmkv.encode(key, value);
    }

    public void saveFloat(String key, float value) {
        mmkv.encode(key, value);
    }

    public void saveDouble(String key, double value) {
        mmkv.encode(key, value);
    }

    public void saveBytes(String key, byte[] value) {
        mmkv.encode(key, value);
    }

    public void saveString(String key, String value) {
        mmkv.encode(key, value);
    }

    public void saveStringSet(String key, Set<String> value) {
        mmkv.encode(key, value);
    }


    public boolean getBoolean(String key,boolean defaultVal){
        return mmkv.decodeBool(key,defaultVal);
    }

    public int getInt(String key,int defaultVal){
        return mmkv.decodeInt(key,defaultVal);
    }

    public long getLong(String key,long defaultVal){
        return mmkv.decodeLong(key,defaultVal);
    }

    public float getFloat(String key,float defaultVal){
        return mmkv.decodeFloat(key,defaultVal);
    }

    public double getDouble(String key,double defaultVal){
        return mmkv.decodeDouble(key,defaultVal);
    }

    public byte[] getBytes(String key){
        return mmkv.decodeBytes(key);
    }

    public String getString(String key,String defaultVal){
        return mmkv.decodeString(key,defaultVal);
    }

    public Set<String> getStringSet(String key){
        return mmkv.decodeStringSet(key);
    }


    public void removeOne(String key){
        mmkv.remove(key);
    }

}