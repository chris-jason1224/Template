package com.cj.common.multitype;

import android.content.Context;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;




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
