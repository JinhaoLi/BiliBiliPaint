package com.jil.paintf.custom;

import androidx.recyclerview.widget.RecyclerView;

/**
 * 2020/9/6 22:07
 *
 * @author JIL
 **/
public interface ItemTouchHelperAdapter {
    //数据交换
    void onItemMove(RecyclerView.ViewHolder source, RecyclerView.ViewHolder target);

    //数据删除
    void onItemSwiped(RecyclerView.ViewHolder source);

    //drag或者swipe选中
    void onItemSelectedChanged(RecyclerView.ViewHolder source, int actionState);

    //状态清除
    void onItemClear(RecyclerView.ViewHolder source);
}
