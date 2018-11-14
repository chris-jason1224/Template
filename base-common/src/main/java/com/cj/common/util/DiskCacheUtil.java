package com.cj.common.util;

/**
 * Created by mayikang on 2018/7/24.
 */
import android.text.TextUtils;

import com.cj.common.var.KeyTag;
/**
 * 磁盘缓存管理类
 * 存取本地 K-V 类型的用户数据
 */
public class DiskCacheUtil {

    private DiskCacheUtil(){}

    private static class Holder{
        private static final DiskCacheUtil instance=new DiskCacheUtil();
    }

    public static DiskCacheUtil getInstance(){
        return Holder.instance;
    }
    
    /**********用户登录Token相关********************/

    public boolean hasToken(){
        String k= (String) SPFUtil.getInstance().getString(KeyTag.TAG_USER_TOKEN,"");
        return !TextUtils.isEmpty(k);
    }

    public void clearToken(){
        SPFUtil.getInstance().removeOne(KeyTag.TAG_USER_TOKEN);
    }

    public String getTokenString(){
        return (String) SPFUtil.getInstance().getString(KeyTag.TAG_USER_TOKEN,"");
    }

    public void saveToken(String token){
        SPFUtil.getInstance().saveString(KeyTag.TAG_USER_TOKEN,token);
    }





}
