package com.cj.common.vlayout;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.android.vlayout.DelegateAdapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.VirtualLayoutManager;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;

/**
 * VLayout的适配器基类
 */

public class VLayoutBaseAdapter<T> extends DelegateAdapter.Adapter<VLayoutBaseViewHolder<T>> {
    //上下文
    private Context mContext;
    //布局文件资源ID
    private int mResLayout;
    private VirtualLayoutManager.LayoutParams mLayoutParams;
    //数据源
    private List<T> mDatas;
    //布局管理器
    private LayoutHelper mLayoutHelper;
    //继承VBaseHolder的Holder
    private Class<? extends VLayoutBaseViewHolder> mClazz;
    //回调监听
    private VLayoutItemListener mListener;

    public VLayoutBaseAdapter(Context context) {
        mContext = context;
    }

    /**
     * <br/> 方法名称:VLayoutBaseAdapter
     * <br/> 方法详述:构造函数
     * <br/> 参数:<同上申明>
     */
    public VLayoutBaseAdapter(Context context, List<T> mDatas, int mResLayout, Class<? extends VLayoutBaseViewHolder> mClazz,
                              LayoutHelper layoutHelper, VLayoutItemListener listener) {
        if (mClazz == null) {
            throw new RuntimeException("clazz is null,please check your params !");
        }
        if (mResLayout == 0) {
            throw new RuntimeException("res is null,please check your params !");
        }
        this.mContext = context;
        this.mResLayout = mResLayout;
        this.mLayoutHelper = layoutHelper;
        this.mClazz = mClazz;
        this.mListener = listener;
        this.mDatas = mDatas;

    }

    /**
     * <br/> 方法名称: VLayoutBaseAdapter
     * <br/> 方法详述: 设置数据源
     * <br/> 参数: mDatas，数据源
     * <br/> 返回值:  VLayoutBaseAdapter
     */
    public VLayoutBaseAdapter setData(List<T> mDatas) {
        this.mDatas = mDatas;
        return this;
    }

    /**
     * <br/> 方法名称: setItem
     * <br/> 方法详述: 设置单个数据源
     * <br/> 参数: mItem，单个数据源
     * <br/> 返回值:  VLayoutBaseAdapter
     */
    public VLayoutBaseAdapter setItem(T mItem) {
        this.mDatas.add(mItem);
        return this;
    }

    /**
     * <br/> 方法名称: setLayout
     * <br/> 方法详述: 设置布局资源ID
     * <br/> 参数: mResLayout, 布局资源ID
     * <br/> 返回值:  VLayoutBaseAdapter
     */
    public VLayoutBaseAdapter setLayout(@LayoutRes int mResLayout) {
        if (mResLayout == 0) {
            throw new RuntimeException("res is null,please check your params !");
        }
        this.mResLayout = mResLayout;
        return this;
    }

    /**
     * <br/> 方法名称: setLayoutHelper
     * <br/> 方法详述: 设置布局管理器
     * <br/> 参数: layoutHelper，管理器
     * <br/> 返回值:  VLayoutBaseAdapter
     */
    public VLayoutBaseAdapter setLayoutHelper(LayoutHelper layoutHelper) {
        this.mLayoutHelper = layoutHelper;
        return this;
    }

    /**
     * <br/> 方法名称: setHolder
     * <br/> 方法详述: 设置holder
     * <br/> 参数: mClazz,集成VBaseHolder的holder
     * <br/> 返回值:  VLayoutBaseAdapter
     */
    public VLayoutBaseAdapter setHolder(Class<? extends VLayoutBaseViewHolder> mClazz) {
        if (mClazz == null) {
            throw new RuntimeException("clazz is null,please check your params !");
        }
        this.mClazz = mClazz;
        return this;
    }

    /**
     * <br/> 方法名称: setListener
     * <br/> 方法详述: 传入监听，方便回调
     * <br/> 参数: listener
     * <br/> 返回值:  VLayoutBaseAdapter
     */
    public VLayoutBaseAdapter setListener(VLayoutItemListener listener) {
        this.mListener = listener;
        return this;
    }

    /**
     * <br/> 方法名称: onCreateLayoutHelper
     * <br/> 方法详述: 继承elegateAdapter.Adapter后重写方法，告知elegateAdapter.Adapter使用何种布局管理器
     * <br/> 参数:
     * <br/> 返回值:  VLayoutBaseAdapter
     */
    @Override
    public LayoutHelper onCreateLayoutHelper() {
        return mLayoutHelper;
    }


    public HashMap<Integer, Object> tags = new HashMap<>();

    /**
     * <br/> 方法名称: setTag
     * <br/> 方法详述: 设置mObject
     * <br/> 参数: mObject
     * <br/> 返回值:  VLayoutBaseAdapter
     */
    public VLayoutBaseAdapter setTag(int tag, Object mObject) {
        if (mObject != null) {
            tags.put(tag, mObject);
        }
        return this;
    }

    /**
     * <br/> 方法名称: onCreateViewHolder
     * <br/> 方法详述: 解析布局文件，返回传入holder的构造器
     */
    @Override
    public VLayoutBaseViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mResLayout,parent,false);
        if (tags != null && tags.size() > 0) {
            for (int tag : tags.keySet()) {
                view.setTag(tag, tags.get(tag));
            }
        }
        try {
            Constructor<? extends VLayoutBaseViewHolder> mClazzConstructor = mClazz.getConstructor(View.class);
            if (mClazzConstructor != null) {
                return mClazzConstructor.newInstance(view);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <br/> 方法名称: onBindViewHolder
     * <br/> 方法详述: 绑定数据
     * <br/> 参数:
     * <br/> 返回值:  VLayoutBaseAdapter
     */
    
    @Override
    public void onBindViewHolder(VLayoutBaseViewHolder holder, int position) {
        holder.setListener(mListener);
        holder.setContext(mContext);
        holder.setData(position, mDatas.get(position));
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }
}
