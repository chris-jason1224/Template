package com.cj.fun_aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author:chris - jason
 * Date:2019/3/5.
 * Package:com.cj.fun_aop.click
 */
//禁止用户连续快速点击按钮提交数据注解

@Target(ElementType.METHOD)//该注解只能作用于方法上
@Retention(RetentionPolicy.RUNTIME)//该注解保留至运行时
public @interface SingleSubmit {

    //是否启用该注解，默认启用
    boolean enabled() default true;

    //允许点击的时间间隔
    int interval() default 300;

}
