package com.cj.main.test;

import android.os.Bundle;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.cj.common.mvp.BaseMVPActivity;
import com.cj.main.R;

import java.sql.Driver;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;


@Route(path = "/biz_main/ACT/com.cj.main.test.TestActivity")
public class TestActivity extends BaseMVPActivity<ITestPresenter> implements ITestView {

    Fragment map;
    private List<Fragment> fragmentList = new ArrayList<>();
    private ViewPager mVP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected int resourceLayout() {
        return R.layout.biz_main_activity_test;
    }

    @Override
    public View initStatusLayout() {
        return null;
    }

    @Override
    protected void initData() {
        TestFragment fragment = new TestFragment();
        map = (Fragment) ARouter.getInstance().build("/fun_lbs/FRG/com.cj.fun_lbs.map.base.BaseMapFragment").navigation();
//        getSupportFragmentManager().beginTransaction().add(R.id.box, map).show(fragment).commit();

        fragmentList.add(fragment);
        fragmentList.add(map);

        mVP.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return fragmentList.get(i);
            }

            @Override
            public int getCount() {
                return fragmentList.size();
            }
        });

    }

    @Override
    protected void initView() {
        mVP = fb(R.id.vp);

    }

    @Override
    protected ITestPresenter createPresenter() {
        return new TestPresenter();
    }

    @Override
    public void onClick(View v) {

    }

}
