package com.cj.common.util;

import java.util.List;

/**
 * Author:chris - jason
 * Date:2019/1/16.
 * Package:com.cj.common.util
 */

public class ListUtil {

    /**
     * 判断一个集合是否为空
     * @param list
     * @return
     */
    public static boolean isEmpty(List<? extends Object> list){

        if(list==null){
            return true;
        }

        if(list.size()==0){
            return true;
        }

        return false;
    }


}
