package com.cj.annontations.bus.model;

/**
 * Author:chris - jason
 * Date:2019-08-13.
 * Package:com.cj.compiler.entity
 */
public class ModuleEventCenterEntity {
    private String moduleName;
    private String delegateName;//每个module中注解添加的类名
    private String pkgName;


    public ModuleEventCenterEntity() {

    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getDelegateName() {
        return delegateName;
    }

    public void setDelegateName(String delegateName) {
        this.delegateName = delegateName;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

}
