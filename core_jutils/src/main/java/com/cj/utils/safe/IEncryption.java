package com.cj.utils.safe;

/**
 * Author:chris - jason
 * Date:2019/1/18.
 * Package:com.cj.utils.safe
 */

public interface IEncryption {
    //加密
    String encrypt(String content);

    //解密
    String decrypt(String content);
}
