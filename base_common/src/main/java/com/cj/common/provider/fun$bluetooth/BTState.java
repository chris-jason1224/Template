package com.cj.common.provider.fun$bluetooth;

/**
 * Author:chris - jason
 * Date:2019/3/22.
 * Package:com.cj.common.provider.fun$bluetooth
 * 蓝牙连接状态
 */
public class BTState {
    //具体值 见@link{com.cj.fun_bluetooth.BTCenter.BTState}
    private int state;

    public BTState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }
}
