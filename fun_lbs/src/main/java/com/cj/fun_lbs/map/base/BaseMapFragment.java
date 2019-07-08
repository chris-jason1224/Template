package com.cj.fun_lbs.map.base;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.amap.api.maps.AMap;
import com.amap.api.maps.TextureMapView;
import com.cj.common.mvp.BaseMVPFragment;
import com.cj.fun_lbs.R;

import java.lang.reflect.Field;

/**
 * Author:chris - jason
 * Date:2019-05-17.
 * Package:com.cj.fun_lbs.map.base
 *
 *
 * Fragment切换时采用replace()方法，也可以采用hide()/show()方法。
 * 如果采用hide()/show()方法切换，地图是叠在一起的，GlSurfaceView叠放会出现穿透现象，建议使用TextureMapView避免这个问题。
 * 同时，采用TextureMapView，也可以避免Fragment切换动画时出现的黑边问题，或者是replace时有黑屏闪一下的问题。
 * MapView和TextureMapView的区别是：TextureMapView由TextureView实现，而MapView是通过GLSurfaceView实现的。
 * Activity中使用MapView，Fragment中使用TextureMapView
 */

@Route(path = "/fun_lbs/FRG/com.cj.fun_lbs.map.base.BaseMapFragment")
public class BaseMapFragment extends BaseMVPFragment<IBaseMapPresenter> implements IBaseMapView {

    private TextureMapView textureMapView;
    private AMap aMap;

    @Override
    protected int setLayoutResource() {
        return R.layout.fun_lbs_base_map_layout;
    }

    @Override
    protected IBaseMapPresenter createPresenter() {
        return new BaseMapPresenter();
    }

    @Override
    protected void initView() {
        textureMapView = fb(R.id.texture_map_view);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (textureMapView != null) {
            textureMapView.onCreate(savedInstanceState);
            aMap = textureMapView.getMap();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        textureMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        textureMapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        textureMapView.onSaveInstanceState(outState);
    }


    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        textureMapView.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    protected boolean setLazyLod() {
        return false;
    }



    @Override
    public void onClick(View v) {

    }
}
