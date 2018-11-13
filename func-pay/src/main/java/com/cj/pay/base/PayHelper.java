package com.cj.pay.base;

/**
 * Created by mayikang on 2018/9/30.
 */

public class PayHelper implements IPay {

    private PayHelper(){

    }

    private static class Holder{
        private static final PayHelper instance=new PayHelper();
    }

    public static PayHelper getInstance(){
        return Holder.instance;
    }





}
