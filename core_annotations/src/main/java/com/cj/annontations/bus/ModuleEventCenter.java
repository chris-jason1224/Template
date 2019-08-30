package com.cj.annontations.bus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author:chris - jason
 * Date:2019-08-13.
 * Package:com.cj.compiler.event
 * 组件的消息列表。组件所支持的所有消息必须都在该注解修饰的类中
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ModuleEventCenter {

}
