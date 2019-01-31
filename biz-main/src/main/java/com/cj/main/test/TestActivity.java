package com.cj.main.test;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cj.common.mvp.BaseMVPActivity;
import com.cj.common.provider.func$web.IWebService;
import com.cj.main.R;
import com.cj.main.R2;

import butterknife.OnClick;

@Route(path = "/biz_main/ACT/com.cj.main.test.TestActivity")
public class TestActivity extends BaseMVPActivity<ITestPresenter> implements ITestView {


    IWebService webService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment fragment = (Fragment) ARouter.getInstance().build("/func_web/FRG/com.cj.web.fragment.BaseWebFragment").navigation();

        webService = (IWebService) ARouter.getInstance().build("/func_web/SEV/com.cj.web.base").navigation();
        if(fragment!=null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.box,fragment)
                    .commit();
        }
        if(webService!=null){
            WebView web= webService.getWebView();
            if(web!=null){
                Toast.makeText(this, "fff", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected int resourceLayout() {
        return R.layout.biz_main_activity_test;
    }

    @Override
    public void onReloadClick() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected ITestPresenter createPresenter() {
        return new TestPresenter();
    }

    @OnClick({})
    public void onClick(View v){
        int id=v.getId();



    }





}
