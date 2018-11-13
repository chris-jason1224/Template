package com.cj.common.http.base;

/**
 * Created by mayikang on 2018/8/20.
 */

public class HttpErrorCode {

    /*********int字典值**********/

    //http网络连通，接口返回正确的业务数据
    public static final int CODE_INT_REQUEST_OK = 9001;

    //http网络连通，接口返回异常数据或返回空值
    public static final int CODE_INT_REQUEST_FAILED = 9002;


    /*********字符串***********/

    public static final String CODE_STR_REQUEST_OK = "请求成功";

    public static final String CODE_STR_REQUEST_FAILED = "请求失败";

    public static final String CODE_STR_REQUEST_UNDEFINED = "请求结果异常";

    public static final String CODE_STR_REQUEST_RESULT_EMPTY = "请求结果为空";

}
