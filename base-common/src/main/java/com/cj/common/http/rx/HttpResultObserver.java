package com.cj.common.http.rx;

import android.app.Activity;
import android.widget.Toast;

import com.cj.common.BuildConfig;
import com.cj.common.base.BaseApp;
import com.cj.common.http.base.BaseHttpResultEntity;
import com.cj.common.http.base.HttpCallback;
import com.cj.common.http.base.HttpErrorCode;
import com.cj.common.util.LooperUtil;
import com.cj.log.CJLog;
import com.cj.ui.notify.Alerter.AlertManager;
import com.cj.ui.notify.Alerter.Alerter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;
import retrofit2.http.HTTP;


/**
 * Created by mayikang on 2018/8/20.
 * Rx封装的观察者
 * T是业务数据data的类型，一般是 JSONObject、JSONArray、Object
 * M是实际数据实体的泛型
 */

public  class HttpResultObserver<T> extends DisposableObserver<BaseHttpResultEntity<T>> implements Observer<BaseHttpResultEntity<T>> {

    private HttpCallback callback;
    //在Presenter层注入CompositeDisposable实例进来，将presenter中的Observable订阅关系加入管理中
    public HttpResultObserver(CompositeDisposable disposable, HttpCallback<T> callback) {

        if(callback==null || disposable ==null){
            return;
        }

        this.callback=callback;
        disposable.add(this);
    }

    //封装业务逻辑层级的请求结果回调
    @Override
    public void onNext(BaseHttpResultEntity<T> resultEntity) {

        if (resultEntity == null) {
            return;
        }

        if(BuildConfig.DEBUG){
            //debug模式下这里统一打印日志，okhttp拦截器也配置了日志打印功能
            CJLog.getInstance().log_json(resultEntity.getData().toString());
        }

        //做业务逻辑层的处理
        switch (resultEntity.getErrorCode()) {
            //业务逻辑成功
            case HttpErrorCode.CODE_INT_REQUEST_OK:
                if(callback!=null){
                    if(resultEntity.getData()!=null){
                        callback.onSuccess(resultEntity.getData());
                    }
                }

                break;
            //业务逻辑失败
            case HttpErrorCode.CODE_INT_REQUEST_FAILED:
                if(callback!=null){
                    callback.onFailed(resultEntity.getMessage());
                }
                break;

            default:
                if(callback!=null){
                    callback.onFailed(HttpErrorCode.CODE_STR_REQUEST_UNDEFINED);
                }
                break;
        }

    }

    //onError和onComplete不能同时回调
    @Override
    public void onError(Throwable throwable) {

        final String error=throwable.toString();

        if(BuildConfig.DEBUG){
            throwable.printStackTrace();
        }

        /***处理常见Http异常信息***/

        //连接超时
        if(throwable instanceof SocketTimeoutException){
            showAlerter(BaseApp.getInstance().getCurrentActivity(),"网络连接超时",error);
        }

        //服务端常见问题 404 500等
        if(throwable instanceof HttpException){
            HttpException exception= (HttpException) throwable;
            int errorCode=exception.code();
            showAlerter(BaseApp.getInstance().getCurrentActivity(),errorCode+"",exception.response().toString());
        }


        if(callback!=null){
            callback.onFailed(throwable.getMessage());
        }

    }

    @Override
    public void onComplete() {

    }


    /**
     * 异常信息提示
     * @param activity
     * @param title
     * @param message
     */
    private void showAlerter(final Activity activity, final String title, final String message){

        if(activity==null){
            Toast.makeText(BaseApp.getApp(), title, Toast.LENGTH_SHORT).show();
            return;
        }

        LooperUtil.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertManager.create(activity).setTitle(title).setMessage(message).setAutoCollapse(false).show();
            }
        });
    }

}
