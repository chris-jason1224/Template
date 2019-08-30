package com.cj.common.ipc;
import com.cj.annontations.bus.ModuleEventCenter;
import com.cj.annontations.bus.EventRegister;

/**
 * Author:chris - jason
 * Date:2019-08-30.
 * Package:com.cj.common.ipc
 */
@ModuleEventCenter
public class IPCEventCenter {

    //主进程接收消息key
    @EventRegister(type = String.class)
    public String ProcessMainReceiveDataEvent;

    //主进程消息通信key
    @EventRegister(type = String.class)
    public String ProcessMainDataEvent;

    //子进程消息通信key
    @EventRegister(type = String.class)
    public String ProcessSubDataEvent;

}
