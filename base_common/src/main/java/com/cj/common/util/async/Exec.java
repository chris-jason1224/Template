package com.cj.common.util.async;

/**
 * Author:chris - jason
 * Date:2019/4/10.
 * Package:com.cj.common.util.async
 * 异步执行的方法体接口
 */
public interface Exec<T> {
    T execute();
}
