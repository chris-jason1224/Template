package com.cj.common.exception;

import android.content.Context;

/**
 * Created by mayikang on 17/5/4.
 */

public class NotInitException extends RuntimeException{

    public NotInitException(Class<?> c){
        super("Java类未初始化异常",new Throwable(c.getSimpleName()));
    }
}
