package com.cj.common.vlayout;

import android.view.View;
public interface VLayoutItemListener<T> {

    void onItemClick(View view, int position, T mData);

}