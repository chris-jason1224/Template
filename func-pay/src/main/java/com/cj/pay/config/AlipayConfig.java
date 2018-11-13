package com.cj.pay.config;

/**
 * Created by mayikang on 2018/9/30.
 */

/**
 * 支付宝参数配置类
 */
public class AlipayConfig {

    /** 商户私钥，pkcs8格式 */
    /** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
    /** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
    /** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
    /** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
    /** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1 */

    /**商户私钥 rsa2 **/
    public static final String RSA2_PRIVATE="   请自行配置   ";

    /**商户私钥 rsa **/
    public static final String RSA_PRIVATE="    请自行配置  ";

    public static final String APPID=" 请自行配置 ";



}
