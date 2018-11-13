package com.cj.common.http.util;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * JSON转换工具类，对解析工具做隔离
 */
public class JSONUtils {

    /**
     * json 转换成 object 实体
     * @param jsonString json
     * @param className 实体类
     * @param <T> 实体泛型
     * @return
     */
    public static <T> T jsonString2JavaObject(String jsonString,Class<T> className){
        return new Gson().fromJson(jsonString,className);
    }

    /**
     * Object 实体转换成 JSON格式的 String
     * @param object
     * @return
     */
    public static String javaObject2JsonString(Object object){
        return new Gson().toJson(object);
    }


    /**
     * jsonArray转javaList --》 泛型在编译期类型被擦除导致报错
     * List<T> = new Gson().fromJson(gsonString, new TypeToken<List<T>>() {}.getType());
     *
     * @param jsonArray
     * @param cls
     * @return
     */
    public static  <T> List<T> jsonArray2JavaList(String jsonArray, Class<T> cls) {
        Gson gson = new Gson();
        List<T> list = new ArrayList<T>();
        JsonArray array = new JsonParser().parse(jsonArray).getAsJsonArray();
        for(final JsonElement elem : array){
            list.add(gson.fromJson(elem, cls));
        }
        return list;
    }


}
