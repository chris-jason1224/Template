package com.cj.common.multitype;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public abstract class MultiTypeViewBinder<T> extends ItemViewBinder<T, ViewHolder> {

    protected Context mContext;
    protected int mLyaoutId;


    public MultiTypeViewBinder(Context context, @LayoutRes int layoutId) {
        mContext = context;
        mLyaoutId = layoutId;
    }

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        ViewHolder holder = ViewHolder.createViewHolder(mContext, parent, mLyaoutId);
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull T item) {
        convert(holder, item, holder.getAdapterPosition());
    }

    protected abstract void convert(ViewHolder holder, T t, int position);

}
