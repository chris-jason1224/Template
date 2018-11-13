package com.cj.common.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.cj.common.exception.NotInitException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by mayikang on 17/2/6.
 * SharedPreferences 管理工具
 */

public class SPFUtil {

    private static Context context;
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;

    private SPFUtil() {

    }

    private static class Holder{
        private static final SPFUtil instance=new SPFUtil();
    }

    public static SPFUtil getInstance(){
        return Holder.instance;
    }
    /**
     * 初始化该工具类
     *
     * @param con
     */
    public static void init(Context con) {
        context = con;
        sp = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        editor = sp.edit();
    }

    /**
     * 保存在手机里面的文件名
     */
    public static final String FILE_NAME = "SYSTEM_SPF";

    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     *
     * @param key
     * @param object
     */
    public void put(String key, Object object) {

        if (sp == null || editor == null) {
            throw new NotInitException(SPFUtil.class);
        }


        if (object instanceof String) {
            editor.putString(key, (String) object);
        }

        if (object instanceof Integer) {
            editor.putInt(key, (Integer) object);
        }

        if (object instanceof Boolean) {
            editor.putBoolean(key, (Boolean) object);
        }

        if (object instanceof Float) {
            editor.putFloat(key, (Float) object);
        }

        if (object instanceof Long) {
            editor.putLong(key, (Long) object);
        } else {
            editor.putString(key, object.toString());
        }

        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param key
     * @param defaultObject
     * @return
     */
    public Object get(String key, Object defaultObject) {

        if (sp == null || editor == null) {
            throw new NotInitException(SPFUtil.class);
        }


        if (defaultObject instanceof String) {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long) {
            return sp.getLong(key, (Long) defaultObject);
        }

        return null;
    }

    /**
     * 移除某个key值已经对应的值
     *
     * @param key
     */
    public void removeOne(String key) {
        if (sp == null || editor == null) {
            throw new NotInitException(SPFUtil.class);
        }
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 清除所有数据
     */
    public void clearAll() {
        if (sp == null || editor == null) {
            throw new NotInitException(SPFUtil.class);
        }
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }

    /**
     * 查询某个key是否已经存在
     *
     * @param key
     * @return
     */
    public boolean contains(String key) {
        if (sp == null || editor == null) {
            throw new NotInitException(SPFUtil.class);
        }
        return sp.contains(key);
    }

    /**
     * 返回所有的键值对
     *
     * @return
     */
    public Map<String, ?> getAll() {
        if (sp == null || editor == null) {
            throw new NotInitException(SPFUtil.class);
        }
        return sp.getAll();
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     * 兼容api 9 之前
     */
    private static class SharedPreferencesCompat {
        private static final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        @SuppressWarnings({"unchecked", "rawtypes"})
        private static Method findApplyMethod() {
            try {
                Class clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (NoSuchMethodException e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
            editor.commit();
        }
    }

}