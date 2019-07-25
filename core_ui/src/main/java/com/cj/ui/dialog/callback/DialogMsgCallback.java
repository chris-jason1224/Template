package com.cj.ui.dialog.callback;

/**
 * Author:chris - jason
 * Date:2019-07-17.
 * Package:com.cj.ui.dialog.callback
 */
public interface DialogMsgCallback extends BaseDialogCallback {
    void   onPositiveClicked(String msg);
    void onNegativeClicked();
}
