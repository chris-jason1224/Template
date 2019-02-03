package com.cj.business.pay.wechat;

/**
 * Author:chris - jason
 * Date:2019/2/1.
 * Package:com.cj.business.pay.wechat
 */

public class PrePayInfo {

    private String appid; //微信appID
    private String noncestr;//随机字符串
    private String packageValue;//包名
    private String partnerid;//商户号
    private String prepayid;//预付单号
    private String sign;//签名串
    private String timestamp;//时间戳

    public PrePayInfo() {
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getPackageValue() {
        return packageValue;
    }

    public void setPackageValue(String packageValue) {
        this.packageValue = packageValue;
    }

    public String getPartnerid() {
        return partnerid;
    }

    public void setPartnerid(String partnerid) {
        this.partnerid = partnerid;
    }

    public String getPrepayid() {
        return prepayid;
    }

    public void setPrepayid(String prepayid) {
        this.prepayid = prepayid;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}