package com.cj.fun_aop.aspect;

import com.cj.common.base.BaseApp;
import com.cj.common.util.NetUtil;
import com.cj.fun_aop.annotation.SingleSubmit;
import com.cj.fun_aop.annotation.WifiNeed;
import com.cj.fun_aop.util.ClickUtil;
import com.cj.manager.basement.BaseApplication;
import com.cj.ui.dialog.MessageDialog;
import com.cj.ui.notify.Alerter.AlertManager;

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

    private MessageDialog dialog;
    //切入点：添加了@com.cj.fun_aop.annotation.SingleSubmit注解的方法体
    //格式： 连接点( @注解 类名 方法名 （参数名） )
    @Pointcut("execution(@com.cj.fun_aop.annotation.WifiNeed  * *(..))")
    public void wifiNeed(){

    }

    //Advice：
    @Around("wifiNeed()")
    public void SingleSubmitExecute(final ProceedingJoinPoint point) throws Throwable {

        MethodSignature methodSignature = (MethodSignature) point.getSignature();

        if(methodSignature!=null){
            Method method = methodSignature.getMethod();
            if(method!=null){
                //获取注解
                WifiNeed annotation = method.getAnnotation(WifiNeed.class);
                if(annotation!=null){
                    //非wifi情况下
                    if(!NetUtil.isWifi(BaseApp.getInstance())){
                        if(dialog==null){
                            dialog = new MessageDialog(BaseApplication.getInstance().getCurrentActivity(), new MessageDialog.MessageDialogCallback() {
                                @Override
                                public void onLeftButtonClick() {
                                    dialog.dismiss();
                                    dialog = null;
                                }

                                @Override
                                public void onRightButtonClick() {
                                    dialog.dismiss();
                                    dialog = null;
                                    try {
                                        point.proceed();
                                    } catch (Throwable throwable) {
                                        throwable.printStackTrace();
                                    }
                                }
                            });
                            dialog.setCancelable(false);
                            dialog.setMessage("非wifi环境，请注意流量消耗");
                            dialog.setLeftButton("取消");
                            dialog.setRightButton("继续");
                        }
                        dialog.show();
                        return;
                    }
                }
            }
        }


        point.proceed();
    }

}
