package com.cj.annontations.module;

/**
 * Author:chris - jason
 * Date:2019/4/1.
 * Package:com.cj.annontations.module
 */
public class ModuleInfo {

    //组件描述文件名字前缀
    public static final String MODULE_PREFIX = "CJ_ModuleProxy_";

    //组件名
    private String moduleName;

    //组件包名
    private String packageName;

    //组件代理名{代理类的绝对路径}
    private String delegateName;

    public ModuleInfo(){

    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getDelegateName() {
        return delegateName;
    }

    public void setDelegateName(String delegateName) {
        this.delegateName = delegateName;
    }
}
