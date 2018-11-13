package com.cj.common.vlayout;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * 封装VLayout ViewHolder
 * @param <T>
 */
public class VLayoutBaseViewHolder<T> extends RecyclerView.ViewHolder {
    public VLayoutItemListener mListener;
    public Context mContext;
    public View mView;
    public T mData;
    public int position;

    public VLayoutBaseViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        ButterKnife.bind(this, itemView);
        init();
    }

    public void init() {

    }

    public void setContext(Context context) {
        mContext = context;
    }

    public void setListener(VLayoutItemListener listener) {
        mListener = listener;
    }

    public void setData(int position, T mData) {
        this.mData = mData;
        this.position = position;
    }

}
