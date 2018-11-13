package com.cj.main.test;

import android.view.View;
import android.widget.Button;

import com.cj.common.vlayout.VLayoutBaseViewHolder;
import com.cj.main.R;

/**
 * Created by mayikang on 2018/8/30.
 */

public class Holder1 extends VLayoutBaseViewHolder<Object> {

    public Holder1(View itemView) {
        super(itemView);
    }

    @Override
    public void setData(final int ps, final Object mData) {
        super.setData(ps, mData);
        final Button btn=itemView.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(btn,ps,mData);
            }
        });

    }

}
