package com.cj.common.http.repository;

import com.cj.common.http.base.BaseHttpResultEntity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by mayikang on 2018/8/20.
 * 存放网络请求的接口
 *
 *  data中数据类型：
 *                 BaseHttpEntity<Object>
 *                 BaseHttpEntity<List<Object>>
 */

public interface APIStore {

    //测试我自己写的接口
    @FormUrlEncoded
    @POST("/dev/api/json")
    Observable<BaseHttpResultEntity<List<Object>>> testJSON(@Field("uid") String uid);

}
