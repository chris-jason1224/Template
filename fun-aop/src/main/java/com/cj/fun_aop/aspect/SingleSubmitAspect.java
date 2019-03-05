package com.cj.fun_aop.aspect;

import com.cj.fun_aop.annotation.SingleSubmit;
import com.cj.fun_aop.util.ClickUtil;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Author:chris - jason
 * Date:2019/3/5.
 * Package:com.cj.fun_aop.aspect
 */
//禁止用户连续快速点击按钮提交数据切面
@Aspect
public class SingleSubmitAspect {


    //切入点：添加了@com.cj.fun_aop.annotation.SingleSubmit注解的方法体
    //格式： 连接点( @注解 类名 方法名 （参数名） )
    @Pointcut("execution(@com.cj.fun_aop.annotation.SingleSubmit  * *(..))")
    public void singleSubmitClick(){

    }

    //Advice：
    @Around("singleSubmitClick()")
    public void SingleSubmitExecute(ProceedingJoinPoint point) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) point.getSignature();

        if(methodSignature!=null){
            Method method = methodSignature.getMethod();
            if(method!=null){
                //获取注解
                SingleSubmit annotation = method.getAnnotation(SingleSubmit.class);
                if(annotation!=null){
                    int interval= annotation.interval();
                    boolean enabled = annotation.enabled();
                    if(enabled){
                        if(ClickUtil.getInstance().isFastClick(interval)){
                            return;
                        }
                    }
                }
            }
        }

        //不启用SingleSubmit注解，直接跳过
        point.proceed();
    }

}
