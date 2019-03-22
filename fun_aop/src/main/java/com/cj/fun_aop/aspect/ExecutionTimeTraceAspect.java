package com.cj.fun_aop.aspect;

import android.util.Log;

import com.cj.fun_aop.annotation.ExecutionTimeTrace;

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
//方法执行时间统计切面
@Aspect
public class ExecutionTimeTraceAspect {

    private static String tag = "ExecutionTimeTraceAspect";

    //切入点：所有添加了@ExecutionTimeTrace注解的方法执行时
    @Pointcut("execution(@com.cj.fun_aop.annotation.ExecutionTimeTrace  * *(..))")
    public void executionTimeTracePointcut() {

    }

    //Advice：整体替换
    @Around("executionTimeTracePointcut()")
    public void timeTraceExecute(ProceedingJoinPoint point) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) point.getSignature();

        if (methodSignature != null) {
            Method method = methodSignature.getMethod();
            if(method!=null){
                ExecutionTimeTrace annotation = method.getAnnotation(ExecutionTimeTrace.class);
                if(annotation!=null){
                    boolean enabled = annotation.enabled();
                    if(enabled){
                        long timeStart = System.currentTimeMillis();
                        //执行原方法体
                        point.proceed();
                        long timeEnd = System.currentTimeMillis();
                        Log.e(tag, methodSignature.getMethod().getName() + "方法执行耗时--" + (timeEnd - timeStart) + "ms");
                        return;
                    }
                }
            }
        }

        point.proceed();

    }


}
