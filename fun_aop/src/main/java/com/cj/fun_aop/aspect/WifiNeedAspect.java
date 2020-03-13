package com.cj.fun_aop.aspect;

import com.cj.common.base.BaseApp;
import com.cj.common.util.NetUtil;
import com.cj.fun_aop.annotation.WifiNeed;
import com.cj.manager.basement.BaseApplication;
import com.cj.ui.dialog.DialogUtil;
import com.cj.ui.dialog.callback.DialogCallback;
import com.cj.ui.dialog.view.MessageDialog;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Author:chris - jason
 * Date:2019-05-23.
 * Package:com.cj.fun_aop.aspect
 * wifi监测切面
 */
@Aspect
public class WifiNeedAspect {

    //切入点：添加了@com.cj.fun_aop.annotation.SingleSubmit注解的方法体
    //格式： 连接点( @注解 类名 方法名 （参数名） )
    @Pointcut("execution(@com.cj.fun_aop.annotation.WifiNeed  * *(..))")
    public void wifiNeed(){

    }

    //Advice：
    @Around("wifiNeed()")
    public void wifiNeed(final ProceedingJoinPoint point) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) point.getSignature();

        if(methodSignature!=null){
            Method method = methodSignature.getMethod();
            if(method!=null){
                //获取注解
                WifiNeed annotation = method.getAnnotation(WifiNeed.class);
                if(annotation!=null){
                    //非wifi情况下
                    if(!NetUtil.isWifi(BaseApp.getInstance())){
                        DialogUtil.getInstance().showConfirmDialog(BaseApplication.getInstance().getCurrentActivity(), "非wifi环境，请注意流量消耗", "取消", "任性继续", new DialogCallback() {
                            @Override
                            public void onNegativeClicked() {

                            }

                            @Override
                            public void onPositiveClicked() {
                                try {
                                    point.proceed();
                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();
                                }
                            }

                            @Override
                            public void onDismiss() {

                            }

                            @Override
                            public void onCancel() {

                            }
                        });

                        return;
                    }
                }
            }
        }


        point.proceed();
    }

}
