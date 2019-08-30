package com.cj.processor.util;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

/**
 * Author:chris - jason
 * Date:2019-08-13.
 * Package:com.cj.compiler.util
 */
public class LogUtil {

    private static Messager messager;

    public static LogUtil getInstance(Messager m){
        messager = m;
        return Holder.instance;
    }

    private static class Holder{
        private static final LogUtil instance = new LogUtil();
    }

    //输出错误日志
    public void e_Message(String msg) {

        if (this.messager == null || msg == null) {
            return;
        }
        this.messager.printMessage(Diagnostic.Kind.ERROR, msg);
    }
}
