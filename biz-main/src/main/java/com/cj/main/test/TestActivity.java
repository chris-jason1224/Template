package com.cj.main.test;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.VirtualLayoutManager;
import com.alibaba.android.vlayout.layout.LinearLayoutHelper;
import com.cj.common.mvp.BaseMVPActivity;
import com.cj.common.vlayout.VLayoutBaseAdapter;
import com.cj.common.vlayout.VLayoutItemListener;
import com.cj.log.view.CrashDialog;
import com.cj.main.R;
import com.cj.main.R2;
import com.cj.ui.tip.UITipDialog;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;

@Route(path = "/biz_main/test")
public class TestActivity extends BaseMVPActivity<ITestPresenter> implements ITestView {

    @BindView(R2.id.rv)RecyclerView mRV;
    private DelegateAdapter delegateAdapter;
    private VLayoutBaseAdapter<Object> adapter1;
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



    @OnClick({R2.id.http,R2.id.pop_tip,R2.id.crash,R2.id.share,R2.id.db})
    public void onClick(View v){
        int id=v.getId();

        if(R.id.http==id){
            mPresenter.doTest("123");
        }

        if(R.id.pop_tip==id){

            final UITipDialog tipDialog = new UITipDialog.Builder(this)
                    .setIconType(UITipDialog.Builder.ICON_TYPE_FAIL)
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

        if(R.id.crash==id){
            TextView tv=null;
            tv.setText("hhah");
        }

        if(R.id.share==id){

        }

        if(R.id.db==id){
            CrashDialog crashDialog=new CrashDialog(this, new CrashDialog.ClickCallback() {
                @Override
                public void onClickClose() {

                }

                @Override
                public void onClickRestart() {

                }
            });
            crashDialog.show();
        }



    }

    private void initAdapter(){
        VirtualLayoutManager layoutManager=new VirtualLayoutManager(this);
        delegateAdapter=new DelegateAdapter(layoutManager);
        mRV.setLayoutManager(layoutManager);
        List<Object> data=new ArrayList<>();
        data.add(new Object());
        data.add(new Object());
        data.add(new Object());
        adapter1=new VLayoutBaseAdapter<Object>(this).
                setData(data).setHolder(Holder1.class).
                setLayout(R.layout.biz_main_item_test).
                setLayoutHelper(new LinearLayoutHelper()).
                setListener(new VLayoutItemListener() {
            @Override
            public void onItemClick(View view, int position, Object mData) {
                Toast.makeText(TestActivity.this, "click "+position, Toast.LENGTH_SHORT).show();
            }
        });

        delegateAdapter.addAdapter(adapter1);

        mRV.setAdapter(delegateAdapter);
    }
}
