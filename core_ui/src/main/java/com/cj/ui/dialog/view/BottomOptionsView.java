package com.cj.ui.dialog.view;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.cj.ui.R;
import com.cj.ui.RoundShape.RoundTextView;
import com.cj.ui.dialog.callback.ItemClickCallback;
import com.cj.ui.util.ListViewUtil;
import com.cj.ui.util.ScreenUtil;

import java.util.List;

/**
 * Author:chris - jason
 * Date:2019-07-16.
 * Package:com.cj.ui.dialog.view
 */
public class BottomOptionsView extends BaseDialogView {

    private ListView lv;
    private ItemClickCallback callback;
    private Adapter adapter;
    private boolean hasTitle = false;

    public BottomOptionsView(@NonNull Context context, List<String> options, ItemClickCallback callback) {
        super(context);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);

        setCancelable(true);
        setCanceledOnTouchOutside(true);

        this.callback = callback;
        adapter = new Adapter(options);
        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        ListViewUtil.setHeightBasedOnAllItems(lv);

    }

    //带title的
    public BottomOptionsView(@NonNull Context context, String title, List<String> options, ItemClickCallback callback) {
        super(context);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM);

        setCancelable(true);
        setCanceledOnTouchOutside(true);

        this.callback = callback;

        hasTitle = true;
        options.add(0, title);

        adapter = new Adapter(options);

        lv.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        ListViewUtil.setHeightBasedOnAllItems(lv);

    }

    @Override
    protected void bindView(View root) {
        lv = root.findViewById(R.id.lv);
        findViewById(R.id.rtv_cancel).setOnClickListener(this);
    }

    @Override
    protected int setDialogLayout() {
        return R.layout.core_ui_bottom_options_view_layout;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if(callback!=null){
            callback.onCancel();
        }
    }

    class Adapter extends BaseAdapter {

        private List<String> options;

        public Adapter(List<String> options) {
            this.options = options;
        }

        @Override
        public int getCount() {
            return options.size();
        }

        @Override
        public Object getItem(int position) {
            return options.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.core_ui_item_bottom_options_view_layout, null);
                holder = new Holder();
                holder.tvOption = convertView.findViewById(R.id.rtv_option);
                holder.v_divider = convertView.findViewById(R.id.v_divider);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }

            holder.tvOption.setText(options.get(position));
            if (options.size() == 1) {
                holder.v_divider.setVisibility(View.GONE);
            } else {
                if (position == options.size() - 1) {
                    holder.v_divider.setVisibility(View.GONE);
                } else {
                    holder.v_divider.setVisibility(View.VISIBLE);
                }
            }

            holder.tvOption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (callback != null) {
                        callback.onClick(options.get(position), position);
                        dismiss();
                    }
                }
            });

            if (0 == position) {
                holder.tvOption.getDelegate().setCornerRadius_TL(ScreenUtil.dip2px(mContext,10));
                holder.tvOption.getDelegate().setCornerRadius_TR(ScreenUtil.dip2px(mContext,10));
                holder.tvOption.getDelegate().setCornerRadius_BL(0);
                holder.tvOption.getDelegate().setCornerRadius_BR(0);

                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.tvOption.getLayoutParams();
                if(hasTitle){
                    lp.height = ScreenUtil.dip2px(mContext,32);
                    holder.tvOption.setTextColor(Color.parseColor("#999999"));
                    holder.tvOption.setTextSize(12);
                }else {
                    lp.height = ScreenUtil.dip2px(mContext,48);
                    holder.tvOption.setTextColor(Color.parseColor("#333333"));
                    holder.tvOption.setTextSize(16);
                }
                holder.tvOption.setLayoutParams(lp);

            } else if ((options.size() - 1) == position) {
                holder.tvOption.getDelegate().setCornerRadius_TL(0);
                holder.tvOption.getDelegate().setCornerRadius_TR(0);
                holder.tvOption.getDelegate().setCornerRadius_BL(ScreenUtil.dip2px(mContext,10));
                holder.tvOption.getDelegate().setCornerRadius_BR(ScreenUtil.dip2px(mContext,10));
            } else {
                holder.tvOption.getDelegate().setCornerRadius_TL(0);
                holder.tvOption.getDelegate().setCornerRadius_TR(0);
                holder.tvOption.getDelegate().setCornerRadius_BL(0);
                holder.tvOption.getDelegate().setCornerRadius_BR(0);
            }

            return convertView;
        }
    }

    class Holder {
        RoundTextView tvOption;
        TextView v_divider;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        int vid = v.getId();
        if (R.id.rtv_cancel == vid) {
            dismiss();
        }
    }

}
