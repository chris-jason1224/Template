package com.cj.annontations.bus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author:chris - jason
 * Date:2019-08-14.
 * Package:com.cj.compiler.event
 * 将一个属性注册为一个消息事件
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface EventRegister {
    Class<?> type() default Object.class;
}
