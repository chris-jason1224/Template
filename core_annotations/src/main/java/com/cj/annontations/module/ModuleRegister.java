package com.cj.annontations.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by mayikang on 2018/7/22.
 * 注解：编译时期自动为添加该注解的module生成配置文件
 */

@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE})
public @interface ModuleRegister {

    //代理名，绝对类名
    String delegateName();

    //组件名
    String moduleName();

}
