package com.cj.login;
import com.cj.annontations.bus.ModuleEventCenter;
import com.cj.annontations.bus.EventRegister;
/**
 * Author:chris - jason
 * Date:2019-08-30.
 * Package:com.cj.login
 */
@ModuleEventCenter
public class BizLoginEventCenter {

    @EventRegister(type = String.class)
    public String LoginEvent;

}
