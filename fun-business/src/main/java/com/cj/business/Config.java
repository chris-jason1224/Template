package com.cj.business;

/**
 * Author:chris - jason
 * Date:2019/2/1.
 * Package:com.cj.business
 */

public class Config {


    private Config(){

    }

    private static class Holder{
        private static final Config instance = new Config();
    }

    public static Config getInstance(){
        return Holder.instance;
    }

    public static final String WECHAT_APP_ID = "";//微信appID

    public static final String ALIPAY_APP_ID="";//支付宝appID


}