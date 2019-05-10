package com.cj.fun_orm;

import android.content.Context;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.cj.common.db.DBCenter;
import com.cj.common.provider.fun$orm.IOrmProvider;
import java.util.Collection;
import java.util.List;
import io.objectbox.Box;
import io.objectbox.query.QueryBuilder;

/**
 * Author:chris - jason
 * Date:2019/4/4.
 * Package:com.cj.fun_orm
 * fun_orm对外提供服务的类
 * 通过调用DBCenter实现功能
 */
@Route(path = "/fun_orm/SEV/com.cj.fun_orm.OrmService")
public class OrmService implements IOrmProvider {

    /**
     * 多进程时，只能一写多度，否则数据会不同步
     *
     * @param context
     */
    @Override
    public void init(Context context) {
    }


    /**
     * 通过主键id 插入或更新一个已经存在的object对象
     * Inserts a new or updates an existing object with the same ID.
     * When inserting and put returns,an ID will be assigned to the just inserted object (this will be explained below).
     * put also supports putting multiple objects, which is more efficient.
     *
     * @param clz
     * @param entity
     * @param <T>
     */
    @Override
    public <T> void put(Class<T> clz, T... entity) {
        Box<T> box = DBCenter.getInstance().getBox(clz);
        checkNull(box);
        box.put(entity);
    }

    @Override
    public <T> void put(Class<T> clz, Collection<T> entityCollection) {
        Box<T> box = DBCenter.getInstance().getBox(clz);
        checkNull(box);
        box.put(entityCollection);
    }


    /**
     * 通过主键id查询实体
     * Given an object’s ID reads it back from its box. To get all objects in the box use getAll
     *
     * @param clz
     * @param id
     * @param <T>
     * @return
     */
    @Override
    public <T> T get(Class<T> clz, long id) {
        Box<T> box = DBCenter.getInstance().getBox(clz);
        checkNull(box);
        return box.get(id);
    }

    @Override
    public <T> List<T> get(Class<T> clz, long[] ids) {
        Box<T> box = DBCenter.getInstance().getBox(clz);
        checkNull(box);
        return box.get(ids);
    }

    @Override
    public <T> List<T> get(Class<T> clz, Iterable<Long> ids) {
        Box<T> box = DBCenter.getInstance().getBox(clz);
        checkNull(box);
        return box.get(ids);
    }

    @Override
    public <T> List<T> getAll(Class<T> clz) {
        Box<T> box = DBCenter.getInstance().getBox(clz);
        checkNull(box);
        return box.getAll();
    }


    /**
     * 移除数据
     * Remove a previously put object from its box (deletes it).
     * remove also supports removing multiple objects, which is more efficient.
     * removeAll  removes (deletes) all objects in a box.
     *
     * @param clz
     * @param id
     * @param <T>
     */
    @Override
    public <T> void remove(Class<T> clz, long... id) {
        Box<T> box = DBCenter.getInstance().getBox(clz);
        checkNull(box);
        box.remove(id);
    }

    @Override
    public <T> void remove(Class<T> clz, T... entity) {
        Box<T> box = DBCenter.getInstance().getBox(clz);
        checkNull(box);
        box.remove(entity);
    }

    @Override
    public <T> void remove(Class<T> clz, Collection<T> entities) {
        Box<T> box = DBCenter.getInstance().getBox(clz);
        checkNull(box);
        box.remove(entities);
    }

    @Override
    public <T> void removeAll(Class<T> clz) {
        Box<T> box = DBCenter.getInstance().getBox(clz);
        checkNull(box);
        box.removeAll();
    }

    /**
     * 获取存储实体总数
     * Returns the number of objects stored in this box.
     *
     * @param clz
     */
    @Override
    public <T> long count(Class<T> clz) {
        Box<T> box = DBCenter.getInstance().getBox(clz);
        checkNull(box);
        return box.count();
    }

    /**
     * 存储的实体数量 是否超过 max
     *
     * @param clz
     * @param max
     * @param <T>
     * @return
     */
    @Override
    public <T> boolean isOverCountX(Class<T> clz, long max) {
        Box<T> box = DBCenter.getInstance().getBox(clz);
        checkNull(box);
        long total = box.count(max);

        if (total < max) {
            return false;
        }

        return true;
    }


    /**
     * 获取QueryBuilder对象
     *
     * @param clz
     * @param <T>
     * @return
     */
    @Override
    public <T> QueryBuilder<T> QueryBuilder(Class<T> clz) {
        Box<T> box = DBCenter.getInstance().getBox(clz);
        checkNull(box);
        return box.query();
    }


    private void checkNull(Object obj) {
        if (obj == null) {
            throw new NullPointerException(obj.getClass().toString());
        }
    }

}
