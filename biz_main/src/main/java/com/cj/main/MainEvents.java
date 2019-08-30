package com.cj.main;

import com.cj.annontations.bus.EventRegister;
import com.cj.annontations.bus.ModuleEventCenter;

/**
 * Author:chris - jason
 * Date:2019-08-22.
 * Package:com.cj.main
 */
@ModuleEventCenter
public class MainEvents {

    @EventRegister(type = String.class)
    public String Refresh_Event = "need to refresh data!";

    @EventRegister(type = Integer.class)
    public Integer Close_Event = 233232;




}
