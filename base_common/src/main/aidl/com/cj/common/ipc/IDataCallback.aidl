// IDataCallback.aidl
package com.cj.common.ipc;
import com.cj.common.ipc.PostDataEntity;
interface IDataCallback {
    void onReceiveFromMainProcess(in PostDataEntity entity);
}
