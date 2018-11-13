package com.cj.common.util;

/**
 * Created by mayikang on 2018/7/24.
 */
import android.text.TextUtils;

import com.cj.common.var.KeyTag;
/**
 * 操作用户token的工具类
 */
public class TokenUtil {

    private TokenUtil(){}

    private static class Holder{
        private static final TokenUtil instance=new TokenUtil();
    }

    public static TokenUtil getInstance(){
        return Holder.instance;
    }

    public boolean hasToken(){
        String k= (String) SPFUtil.getInstance().get(KeyTag.TAG_USER_TOKEN,"");
        return !TextUtils.isEmpty(k);
    }

    public void clearToken(){
        SPFUtil.getInstance().removeOne(KeyTag.TAG_USER_TOKEN);
    }

    public String getTokenString(){
        return (String) SPFUtil.getInstance().get(KeyTag.TAG_USER_TOKEN,"");
    }

    public void saveToken(String token){
        SPFUtil.getInstance().put(KeyTag.TAG_USER_TOKEN,token);
    }
}
