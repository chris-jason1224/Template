package com.cj.common.bus;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ExternalLiveData;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import com.cj.annontations.Key;
import com.cj.annontations.bus.model.ModuleEventCenterEntity;
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
import java.util.Map;

/**
 * Author:chris - jason
 * Date:2019-08-28.
 * Package:com.cj.common.bus
 * 组件间消息总线
 */
public class ModuleBus {

    private String TAG = "ModuleBus";
    private Context context;

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
        this.context = context;
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
                map_1.put(eventName,liveEvent = new LiveEvent(eventName));
                bus.put(moduleName, map_1);
            } else {
                HashMap<String, LiveEvent> map_1 = bus.get(moduleName);
                if(map_1.containsKey(eventName)){
                    liveEvent = map_1.get(eventName);
                }else {
                    map_1.put(eventName,liveEvent = new LiveEvent(eventName));
                }
            }

            return liveEvent;
        }
    }


    public interface Observable<T> {

        /**
         * 发送一个消息，支持前台线程、后台线程发送
         *
         * @param value
         */
        void post(T value);

        /**
         * 发送一个消息，支持前台线程、后台线程发送
         * 需要跨进程、跨APP发送消息的时候调用该方法
         *
         * @param value
         */
        void broadcast(T value);

        /**
         * 延迟发送一个消息，支持前台线程、后台线程发送
         *
         * @param value
         * @param delay 延迟毫秒数
         */
        void postDelay(T value, long delay);

        /**
         * 发送一个消息，支持前台线程、后台线程发送
         * 需要跨进程、跨APP发送消息的时候调用该方法
         *
         * @param value
         */
        void broadcast(T value, boolean foreground);

        /**
         * 注册一个Observer，生命周期感知，自动取消订阅
         *
         * @param owner
         * @param observer
         */
        void observe(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer);

        /**
         * 注册一个Observer，生命周期感知，自动取消订阅
         * 如果之前有消息发送，可以在注册时收到消息（消息同步）
         *
         * @param owner
         * @param observer
         */
        void observeSticky(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer);

        /**
         * 注册一个Observer
         *
         * @param observer
         */
        void observeForever(@NonNull Observer<T> observer);

        /**
         * 注册一个Observer
         * 如果之前有消息发送，可以在注册时收到消息（消息同步）
         *
         * @param observer
         */
        void observeStickyForever(@NonNull Observer<T> observer);

        /**
         * 通过observeForever或observeStickyForever注册的，需要调用该方法取消订阅
         *
         * @param observer
         */
        void removeObserver(@NonNull Observer<T> observer);
    }

    private class LiveEvent<T> implements Observable<T> {

        @NonNull
        private final String key;
        private final LifecycleLiveData<T> liveData;
        private final Map<Observer, ObserverWrapper<T>> observerMap = new HashMap<>();
        private final Handler mainHandler = new Handler(Looper.getMainLooper());

        LiveEvent(@NonNull String key) {
            this.key = key;
            this.liveData = new LifecycleLiveData<>();
        }

        @Override
        public void post(T value) {
            if (isMainThread()) {
                postInternal(value);
            } else {
                mainHandler.post(new PostValueTask(value));
            }
        }

        @Override
        public void broadcast(T value) {
            broadcast(value, false);
        }

        @Override
        public void postDelay(T value, long delay) {
            mainHandler.postDelayed(new PostValueTask(value), delay);
        }

        @Override
        public void broadcast(final T value, final boolean foreground) {
            if (context!= null) {
                if (isMainThread()) {
                    broadcastInternal(value, foreground);
                } else {
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            broadcastInternal(value, foreground);
                        }
                    });
                }
            } else {
                post(value);
            }
        }

        @Override
        public void observe(@NonNull final LifecycleOwner owner, @NonNull final Observer<T> observer) {
            if (isMainThread()) {
                observeInternal(owner, observer);
            } else {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        observeInternal(owner, observer);
                    }
                });
            }
        }

        @Override
        public void observeSticky(@NonNull final LifecycleOwner owner, @NonNull final Observer<T> observer) {
            if (isMainThread()) {
                observeStickyInternal(owner, observer);
            } else {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        observeStickyInternal(owner, observer);
                    }
                });
            }
        }

        @Override
        public void observeForever(@NonNull final Observer<T> observer) {
            if (isMainThread()) {
                observeForeverInternal(observer);
            } else {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        observeForeverInternal(observer);
                    }
                });
            }
        }

        @Override
        public void observeStickyForever(@NonNull final Observer<T> observer) {
            if (isMainThread()) {
                observeStickyForeverInternal(observer);
            } else {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        observeStickyForeverInternal(observer);
                    }
                });
            }
        }

        @Override
        public void removeObserver(@NonNull final Observer<T> observer) {
            if (isMainThread()) {
                removeObserverInternal(observer);
            } else {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        removeObserverInternal(observer);
                    }
                });
            }
        }

        @MainThread
        private void postInternal(T value) {
            liveData.setValue(value);
        }

        @MainThread
        private void broadcastInternal(T value, boolean foreground) {
//            Intent intent = new Intent(ModuleBusConstant.intent_name);
//            if (foreground && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);
//            }
//            intent.putExtra(ModuleBusConstant.event_key, key);
//            try {
//                encoder.encode(intent, value);
//                context.getApplicationContext().sendBroadcast(intent);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            //todo 跨进程发送消息，暂时不支持

        }

        @MainThread
        private void observeInternal(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
            ObserverWrapper<T> observerWrapper = new ObserverWrapper<>(observer);
            observerWrapper.preventNextEvent = liveData.getVersion() > ExternalLiveData.START_VERSION;
            liveData.observe(owner, observerWrapper);
        }

        @MainThread
        private void observeStickyInternal(@NonNull LifecycleOwner owner, @NonNull Observer<T> observer) {
            ObserverWrapper<T> observerWrapper = new ObserverWrapper<>(observer);
            liveData.observe(owner, observerWrapper);
        }

        @MainThread
        private void observeForeverInternal(@NonNull Observer<T> observer) {
            ObserverWrapper<T> observerWrapper = new ObserverWrapper<>(observer);
            observerWrapper.preventNextEvent = liveData.getVersion() > ExternalLiveData.START_VERSION;
            observerMap.put(observer, observerWrapper);
            liveData.observeForever(observerWrapper);
        }

        @MainThread
        private void observeStickyForeverInternal(@NonNull Observer<T> observer) {
            ObserverWrapper<T> observerWrapper = new ObserverWrapper<>(observer);
            observerMap.put(observer, observerWrapper);
            liveData.observeForever(observerWrapper);
        }

        @MainThread
        private void removeObserverInternal(@NonNull Observer<T> observer) {
            Observer<T> realObserver;
            if (observerMap.containsKey(observer)) {
                realObserver = observerMap.remove(observer);
            } else {
                realObserver = observer;
            }
            liveData.removeObserver(realObserver);
        }

        private class LifecycleLiveData<T> extends ExternalLiveData<T> {
            @Override
            protected Lifecycle.State observerActiveLevel() {
                //return lifecycleObserverAlwaysActive ? Lifecycle.State.CREATED : Lifecycle.State.STARTED;
                //只有onStart之后才能接收到消息
                return Lifecycle.State.CREATED;
            }

            @Override
            public void removeObserver(@NonNull Observer<? super T> observer) {
                super.removeObserver(observer);
//                if (autoClear && !liveData.hasObservers()) {
//                     bus.remove(key);
//                }

                if(!liveData.hasObservers()){
                    bus.remove(key);
                }

            }
        }

        private class PostValueTask implements Runnable {
            private Object newValue;

            public PostValueTask(@NonNull Object newValue) {
                this.newValue = newValue;
            }

            @Override
            public void run() {
                postInternal((T) newValue);
            }
        }
    }

    private static class ObserverWrapper<T> implements Observer<T> {

        @NonNull
        private final Observer<T> observer;
        private boolean preventNextEvent = false;

        ObserverWrapper(@NonNull Observer<T> observer) {
            this.observer = observer;
        }

        @Override
        public void onChanged(@Nullable T t) {
            if (preventNextEvent) {
                preventNextEvent = false;
                return;
            }
            try {
                observer.onChanged(t);
            } catch (ClassCastException e) {
                e.printStackTrace();
            }
        }
    }


    //判断当前线程是否是主线程
    private boolean isMainThread(){
       return Looper.myLooper() == Looper.getMainLooper();
    }


}
