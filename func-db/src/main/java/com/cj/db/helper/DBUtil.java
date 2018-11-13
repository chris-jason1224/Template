package com.cj.db.helper;

/**
 * Created by mayikang on 2018/10/17.
 */

/**
 * 二次封装对数据库的使用
 * @param <T>
 */

public class DBUtil<T> {

    private DBUtil(){

    }

    private static class Holder{
        private static final DBUtil helper =new DBUtil();
    }

    public static <T> DBUtil newInstance(){
        return Holder.helper;
    }

    public void insert(T object){

    }





}
