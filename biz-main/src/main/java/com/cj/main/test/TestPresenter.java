package com.cj.main.test;

import com.cj.common.http.base.BaseHttpResultEntity;
import com.cj.common.http.base.HttpCallback;
import com.cj.common.http.repository.APIStore;
import com.cj.common.http.repository.RetrofitFactory;
import com.cj.common.http.rx.CJSchedulers;
import com.cj.common.http.rx.HttpResultObserver;
import com.cj.common.http.util.JSONUtils;
import com.cj.common.mvp.BaseMVPPresenter;
import com.cj.log.CJLog;

import java.util.List;


/**
 * Created by mayikang on 2018/8/3.
 */

public class TestPresenter extends BaseMVPPresenter implements ITestPresenter {


    @Override
    public void doTest(String obj) {
        mAPIStore.testJSON("u_4")
                .compose(CJSchedulers.<BaseHttpResultEntity<List<Object>>>compose()).
                subscribe(new HttpResultObserver<>(mDisposable, new HttpCallback<List<Object>>() {
                    @Override
                    public void onSuccess(List<Object> jsonArray) {
                        if(jsonArray!=null){
                            List<UserModel> models= JSONUtils.jsonArray2JavaList(jsonArray.toString(),UserModel.class);
                            if(models!=null){
                                for (UserModel m:models){
                                    CJLog.getInstance().log_json(JSONUtils.javaObject2JsonString(m));
                                }
                            }

                        }
                    }

                    @Override
                    public void onFailed(String msg) {

                    }
                }));


    }


}
