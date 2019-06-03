// IProcessCenter.aidl
package com.cj.common.ipc;
import com.cj.common.ipc.IDataCallback;
import com.cj.common.ipc.PostDataEntity;

interface IProcessCenter {

    void registerProcessInfo(in int pid,in String pName,in IDataCallback callback);

    void unRegisterProcessInfo(in int pid,in String pName,in IDataCallback callback);

    void postData(in PostDataEntity entity);

}
