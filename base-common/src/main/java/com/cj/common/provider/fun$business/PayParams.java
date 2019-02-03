package com.cj.common.provider.fun$business;

/**
 * Author:chris - jason
 * Date:2019/2/2.
 * Package:com.cj.common.provider.fun$business
 * 统一的支付参数封装
 */

public class PayParams<T> {

    private String orderCode;//业务订单编号

    private int payWay;//支付方式

    private T extra;//携带的各大平台实际支付参数，支付宝传签名信息，微信传整个预付单信息

    public PayParams() {

    }

    public PayParams(String orderCode, int payWay, T extra) {
        this.orderCode = orderCode;
        this.payWay = payWay;
        this.extra = extra;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public int getPayWay() {
        return payWay;
    }

    public void setPayWay(int payWay) {
        this.payWay = payWay;
    }

    public T getExtra() {
        return extra;
    }

    public void setExtra(T extra) {
        this.extra = extra;
    }
}