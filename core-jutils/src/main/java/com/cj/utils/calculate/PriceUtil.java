package com.cj.utils.calculate;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Author:chris - jason
 * Date:2019/1/31.
 * Package:com.cj.utils.calculate
 * 用于电商精确的价格计算
 */

public class PriceUtil {
    
    private PriceUtil(){

    }

    static class Holder{
        private static final PriceUtil instance = new PriceUtil();
    }

    public static PriceUtil getInstance(){
        return Holder.instance;
    }

    /*******加法运算*****/

    public double add(String a,String b){
        BigDecimal decimal1 = new BigDecimal(a);
        BigDecimal decimal2 = new BigDecimal(b);
        return decimal1.add(decimal2).doubleValue();
    }

    public double add(Double a,Double b){
        BigDecimal decimal1 = new BigDecimal(String.valueOf(a));
        BigDecimal decimal2 = new BigDecimal(String.valueOf(b));
        return decimal1.add(decimal2).doubleValue();
    }

    /*******减法运算*******/

    public double sub(String a,String b){
        BigDecimal decimal1 = new BigDecimal(a);
        BigDecimal decimal2 = new BigDecimal(b);
        return decimal1.subtract(decimal2).doubleValue();
    }

    public double sub(double a,double b){
        BigDecimal decimal1 = new BigDecimal(String.valueOf(a));
        BigDecimal decimal2 = new BigDecimal(String.valueOf(b));
        return decimal1.subtract(decimal2).doubleValue();
    }

    /******乘法运算******/

    public double mul(String a,String b){
        BigDecimal decimal1 = new BigDecimal(a);
        BigDecimal decimal2 = new BigDecimal(b);
        return decimal1.multiply(decimal2).doubleValue();
    }

    public double mul(double a,double b){
        BigDecimal decimal1 = new BigDecimal(String.valueOf(a));
        BigDecimal decimal2 = new BigDecimal(String.valueOf(b));
        return decimal1.multiply(decimal2).doubleValue();
    }

    /******除法运算*******/

    public double div(String a,String b){
        BigDecimal decimal1 = new BigDecimal(a);
        BigDecimal decimal2 = new BigDecimal(b);
        return decimal1.divide(decimal2, RoundingMode.HALF_DOWN).doubleValue();
    }

    public double div(double a,double b){
        BigDecimal decimal1 = new BigDecimal(String.valueOf(a));
        BigDecimal decimal2 = new BigDecimal(String.valueOf(b));
        return decimal1.divide(decimal2, RoundingMode.HALF_DOWN).doubleValue();
    }



}
