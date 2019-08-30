package com.cj.common.bus;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.cj.annontations.Key;
import com.cj.annontations.bus.model.ModuleEventCenterEntity;
import com.cj.log.CJLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author:chris - jason
 * Date:2019-08-28.
 * Package:com.cj.common.bus
 * 组件间消息总线
 */
public class ModuleBus {

    private String TAG = "ModuleBus";
    //第一级 key：moduleName ，第二级 key：消息名
    private HashMap<String, HashMap<String, LiveEvent>> bus;
    private List<ModuleEventCenterEntity> eventCenterEntityList;

    private static final ModuleBus ourInstance = new ModuleBus();

    public static ModuleBus getInstance() {
        return ourInstance;
    }

    private ModuleBus() {
        bus = new HashMap<>();
        eventCenterEntityList = new ArrayList<>();
    }

    //Application中注册，读取所有使用了@ModuleEventCenter注解的module信息
    public void init(Context context) {
        //读取json文件
        AssetManager assetManager = context.getApplicationContext().getAssets();
        try {
            String[] fileList = assetManager.list("");
            for (String file : fileList) {
                String fileName = file;
                if (fileName.startsWith(Key.MODULE_EVENT_FILE_PRE) && fileName.endsWith(".json")) {
                    //解析json配置文件
                    ModuleEventCenterEntity moduleEventCenterEntity = parse(assetManager.open(fileName));
                    if (moduleEventCenterEntity == null) {
                        continue;
                    }
                    eventCenterEntityList.add(moduleEventCenterEntity);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ModuleEventCenterEntity parse(InputStream inputStream) {
        ModuleEventCenterEntity fromJson = null;
        try {
            InputStreamReader isr = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                builder.append(line);
            }
            br.close();
            isr.close();
            fromJson = parseModuleInfoByJson(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "解析Module.json出错");
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "解析Module.json出错");
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "解析Module.json出错");
        }
        return fromJson;
    }

    private ModuleEventCenterEntity parseModuleInfoByJson(String jsonString) throws JSONException, ParseException {
        JSONObject jsonObject = new JSONObject(jsonString);
        if (jsonObject == null) {
            return null;
        }
        ModuleEventCenterEntity moduleEventCenterEntity = new ModuleEventCenterEntity();
        moduleEventCenterEntity.setModuleName(jsonObject.optString("moduleName"));
        moduleEventCenterEntity.setPkgName(jsonObject.optString("pkgName"));
        moduleEventCenterEntity.setDelegateName(jsonObject.optString("delegateName"));
        return moduleEventCenterEntity;
    }


    /**
     * @param clz Gen$fun_business$Interface.class
     * @param <T>
     * @return
     */
    public synchronized <T> T of(Class<T> clz) {
        T t = null;
        String moduleName = "";
        for (ModuleEventCenterEntity entity : eventCenterEntityList) {
            String intfName = clz.getName();
            if (!TextUtils.isEmpty(intfName)) {
                String[] splits = intfName.split("\\$");
                if (TextUtils.equals(splits[1], entity.getModuleName())) {
                    moduleName = entity.getModuleName();
                    break;
                }
            }
        }

        if (!TextUtils.isEmpty(moduleName)) {
            //动态生成的接口名
            //接口名：gen.com.cj.bus.Gen$ +moduleName+ $Interface
            String interfaceName = "gen.com.cj.bus.Gen$" + moduleName + "$Interface";
            if (!TextUtils.equals(interfaceName, clz.getCanonicalName())) {
                return null;
            }
            try {
                Class intf = Class.forName(interfaceName);
                if (intf != null && intf.isInterface()) {
                    ModuleBusInvocationHandler handler = new ModuleBusInvocationHandler(moduleName);
                    ClassLoader classLoader = clz.getClassLoader();
                    Class<?>[] interfaces = new Class[]{clz};
                    t = (T) Proxy.newProxyInstance(classLoader, interfaces, handler);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

        return t;
    }

    private class ModuleBusInvocationHandler implements InvocationHandler {

        private String moduleName;

        public ModuleBusInvocationHandler(String moduleName) {
            this.moduleName = moduleName;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            String methodName = method.getName();
            if (TextUtils.isEmpty(methodName)) {
                return null;
            }

            String[] array = methodName.split("\\$");
            //消息名
            String eventName = array[1];

            LiveEvent liveEvent;

            if (!bus.containsKey(moduleName)) {
                HashMap<String, LiveEvent> map_1 = new HashMap<>();
                map_1.put(eventName,liveEvent = new LiveEvent());
                bus.put(moduleName, map_1);
            } else {
                HashMap<String, LiveEvent> map_1 = bus.get(moduleName);
                if(map_1.containsKey(eventName)){
                    liveEvent = map_1.get(eventName);
                }else {
                    map_1.put(eventName,liveEvent = new LiveEvent());
                }
            }

            return liveEvent;
        }
    }


    @Deprecated
    private class ObservableProxy implements InvocationHandler {

        private LiveEvent liveEvent;

        public ObservableProxy(LiveEvent liveEvent) {
            this.liveEvent = liveEvent;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            if (name.contains("observe")) {
                LifecycleOwner owner = (LifecycleOwner) args[0];
                Observer observer = (Observer) args[1];
                //todo 根据key来进行注册观察者
                liveEvent.observe(owner,observer);
                CJLog.getInstance().log_e("根据key来进行注册观察者");
            }

            return method.invoke(args);
        }
    }


}
