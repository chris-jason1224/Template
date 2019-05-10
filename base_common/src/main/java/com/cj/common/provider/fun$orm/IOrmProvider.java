package com.cj.common.provider.fun$orm;

import com.alibaba.android.arouter.facade.template.IProvider;

import java.util.Collection;
import java.util.List;

import io.objectbox.query.QueryBuilder;

/**
 * Author:chris - jason
 * Date:2019/4/4.
 * Package:com.cj.common.provider.fun$orm
 * fun_orm对外提供服务的约束接口
 */
public interface IOrmProvider extends IProvider {


    <T> void put(Class<T> clz, T... entity);

    <T> void put(Class<T> clz, Collection<T> entityCollection);

    <T> T get(Class<T> clz, long id);

    <T> List<T> get(Class<T> clz, long[] ids);

    <T> List<T> get(Class<T> clz, Iterable<Long> ids);

    <T> List<T> getAll(Class<T> clz);

    <T> void remove(Class<T> clz, long... id);

    <T> void remove(Class<T> clz, T... entity);

    <T> void remove(Class<T> clz, Collection<T> entities);

    <T> void removeAll(Class<T> clz);

    <T> long count(Class<T> clz);

    <T> boolean isOverCountX(Class<T> clz, long max);

    <T> QueryBuilder<T> QueryBuilder(Class<T> clz);
}
