package com.cj.ui.util;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by Chris-Jason on 2019/7/25.
 */
public class ListViewUtil {
    /**
     * 计算listview完整高度
     *
     * @param listView
     */

    public static void setHeightBasedOnAllItems(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 显示指定个数的 item
     *
     * @param listView
     * @param showCount
     */
    public static void setHeightBasedOnItemCount(ListView listView, int showCount) {

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }

        //获取单个item高度，不包含divider
        int totalHeight = 0;
        int realCount = (showCount <= listAdapter.getCount()) ? showCount : listAdapter.getCount();

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        for (int i = 0; i < realCount; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        if (realCount > 1) {
            params.height = totalHeight + listView.getDividerHeight() * (realCount - 1);
        } else {
            params.height = totalHeight;
        }


        listView.setLayoutParams(params);

    }
}
