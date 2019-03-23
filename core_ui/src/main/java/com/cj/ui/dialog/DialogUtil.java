package com.cj.ui.dialog;

/**
 * Author:chris-jason
 * <p>
 * Date:2019/3/23 0023
 * <p>
 * Package:com.cj.ui.dialog
 */


public class DialogUtil implements IDialog {


    private DialogUtil(){

    }

    public static DialogUtil getInstance(){
        return Holder.instance;
    }



    private static class Holder{
        private static final DialogUtil instance = new DialogUtil();
    }


    @Override
    public void showAlert(String title, String contentMess) {

    }









}
