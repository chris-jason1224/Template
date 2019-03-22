package com.cj.fun_aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author:chris - jason
 * Date:2019/3/5.
 * Package:com.cj.fun_aop.annotation
 */
//方法执行时间统计注解

@Target(ElementType.METHOD)//该注解只能作用于方法
@Retention(RetentionPolicy.RUNTIME)//该注解保留至运行时
public @interface ExecutionTimeTrace {

    //是否开启注解
    boolean enabled() default true;

}
