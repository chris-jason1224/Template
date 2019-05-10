package com.cj.common.db;

import android.content.Context;

import com.cj.common.exception.NotInitException;
import com.cj.common.model.MyObjectBox;
import io.objectbox.Box;
import io.objectbox.BoxStore;

/**
 * Author:chris - jason
 * Date:2019-05-10.
 * Package:com.cj.fun_orm
 * OrmService通过调用该类对其他组件提供orm能力
 */
public class DBCenter {

    private static Context mContext;
    private BoxStore mBoxStore;

    private DBCenter() {
        //初始化BoxStore
        mBoxStore = MyObjectBox.builder().androidContext(mContext.getApplicationContext()).build();
    }

    private static class Holder {
        private static final DBCenter instance = new DBCenter();
    }

    public static DBCenter getInstance() {
        return Holder.instance;
    }

    public static void init(Context c) {
        mContext = c;
    }

    /**
     * 获取实体Box
     *
     * @param clz 实体class
     * @param <T> 实体的对象
     * @return
     */
    public <T> Box<T> getBox(Class<T> clz) {

        if (mContext == null) {
            throw new NotInitException(DBCenter.class);
        }
        Box<T> box = null;
        if (mBoxStore != null) {
            box = mBoxStore.boxFor(clz);
        }

        return box;
    }

}
