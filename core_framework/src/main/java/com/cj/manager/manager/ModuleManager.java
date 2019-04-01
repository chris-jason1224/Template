package com.cj.manager.manager;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;


import com.cj.annontations.module.ModuleInfo;
import com.cj.manager.basement.BaseApplication;
import com.cj.manager.module.interfaces.IModuleApplicationDelegate;
import com.cj.manager.module.utils.ModuleClassUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.cj.annontations.module.ModuleInfo.MODULE_PREFIX;


public class ModuleManager {

    private static final String TAG = "ModuleManager";
    private List<ModuleInfo> moduleInfoList;
    private List<String> delegateNameList;
    private List<IModuleApplicationDelegate> appDelegateList;

    private static class ModuleManagerHolder {  //内部静态类单例模式
        private static final ModuleManager sInstance  = new ModuleManager();
    }

    public static ModuleManager getInstance() {
        return ModuleManagerHolder.sInstance;
    }

    private ModuleManager() {
        this.moduleInfoList = new ArrayList();
    }

    public Context getApplicationContext() {
        return BaseApplication.getInstance().getApplicationContext();
    }

    public List<ModuleInfo> getModuleInfoList() {
        return moduleInfoList;
    }

    public List<String> getDelegateNameList() {
        return delegateNameList;
    }

    public List<IModuleApplicationDelegate> getAppDelegateList() {
        return appDelegateList;
    }

    /**
     * 加载各个module组件信息
     */
    public void loadModules() {
        Context context = getApplicationContext();
        appDelegateList = new ArrayList();
        delegateNameList = new ArrayList();
        try {
            //获取assets路径下的所有文件
            AssetManager assetManager = context.getResources().getAssets();
            String[] fileList = assetManager.list("");
            for(String file:fileList) {
                String fileName = file;
                if(fileName.startsWith(MODULE_PREFIX) && fileName.endsWith(".json")) {
                    //解析json配置文件
                    ModuleInfo moduleInfo = parse(assetManager.open(fileName));
                    if(moduleInfo == null){
                        continue;
                    }
                    moduleInfoList.add(moduleInfo);
                    //反射获取代理实例
                    delegateNameList.add(moduleInfo.getDelegateName());
                }
            }
            //ModuleInfo对应写到json描述文件的结构
            appDelegateList.addAll(ModuleClassUtils.getObjectsWithClassName(context, IModuleApplicationDelegate.class, delegateNameList));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析ModuleInfo对象
     *
     * @param inputStream
     * @return
     */
    private ModuleInfo parse(InputStream inputStream) {
        ModuleInfo fromJson = null;
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
            Log.e(TAG,"解析Module.json出错");
        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG,"解析Module.json出错");
        }
        catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG,"解析Module.json出错");
        }
        return fromJson;
    }

    /**
     * 解析Json数据
     *
     * @param jsonString Json数据字符串
     * @throws JSONException
     * @throws ParseException
     */
    private ModuleInfo parseModuleInfoByJson(String jsonString) throws JSONException, ParseException {
        JSONObject jsonObject = new JSONObject(jsonString);
        if(jsonObject == null){
            return null;
        }
        ModuleInfo moduleInfo =new ModuleInfo();
        moduleInfo.setModuleName(jsonObject.optString("moduleName"));
        moduleInfo.setPackageName(jsonObject.optString("packageName"));
        moduleInfo.setDelegateName(jsonObject.optString("delegateName"));
        return moduleInfo;
    }
}
