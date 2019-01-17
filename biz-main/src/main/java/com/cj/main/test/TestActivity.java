package com.cj.main.test;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;

import com.cj.common.multitype.Items;
import com.cj.common.multitype.MultiTypeAdapter;
import com.cj.common.multitype.MultiTypeViewBinder;
import com.cj.common.multitype.ViewHolder;
import com.cj.common.mvp.BaseMVPActivity;

import com.cj.main.R;
import com.cj.main.R2;
import com.cj.ui.tip.UITipDialog;

import butterknife.BindView;
import butterknife.OnClick;

@Route(path = "/biz_main/test")
public class TestActivity extends BaseMVPActivity<ITestPresenter> implements ITestView {

    @BindView(R2.id.rv)
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getLoadService().showSuccess();
            }
        },1500);
        initAdapter();
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

    @OnClick({R2.id.http,R2.id.pop_tip})
    public void onClick(View v){
        int id=v.getId();

        if(R.id.http==id){
            mPresenter.doTest("123");
        }

        if(R.id.pop_tip==id){

            final UITipDialog tipDialog = new UITipDialog.Builder(this)
                    .setIconType(UITipDialog.Builder.ICON_TYPE_LOADING)
                    .setTipWord("发送失败")
                    .create();
            tipDialog.show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    tipDialog.dismiss();
                }
            },1500);
        }





    }



    private void initAdapter(){
        Items items = new Items();
        items.add("哈哈");
        items.add(R.drawable.core_log_icon_crash_dialog_close);

        items.add("嘻嘻");
        items.add(R.drawable.core_log_icon_crash_dialog_bug);

        MultiTypeAdapter adapter =new MultiTypeAdapter(items);
        MultiTypeViewBinder<String> binder1=new MultiTypeViewBinder<String>(this,R.layout.biz_main_lay_1) {
            @Override
            protected void convert(ViewHolder holder, String s, int position) {
                TextView tv =holder.getView(R.id.tv);
                tv.setText(s);
            }
        };

        MultiTypeViewBinder<Integer> binder2 =new MultiTypeViewBinder<Integer>(this,R.layout.biz_main_lay_2) {
            @Override
            protected void convert(ViewHolder holder, Integer integer, int position) {
                ImageView iv=holder.getView(R.id.iv);
                iv.setImageResource(integer);
            }
        };
        adapter.register(String.class,binder1);
        adapter.register(Integer.class,binder2);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }





}
