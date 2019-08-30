package com.cj.fun_aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author:chris - jason
 * Date:2019-05-23.
 * Package:com.cj.fun_aop.annotation
 * 需要wifi环境下执行，否则提示用户
 */
@Target(ElementType.METHOD)//该注解只能作用于方法
@Retention(RetentionPolicy.RUNTIME)//该注解保留至运行时
public @interface WifiNeed {

}
