package com.example.workdemo.main;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.example.workdemo.R;

import org.jetbrains.annotations.NotNull;

public class MainPageAdapter extends BaseQuickAdapter<MainListEntity, BaseViewHolder> {

    public MainPageAdapter() {
        super(R.layout.item_main_page);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder baseViewHolder, MainListEntity mainListEntity) {
        baseViewHolder.setText(R.id.tv_title,mainListEntity.title);
        baseViewHolder.setText(R.id.tv_content,mainListEntity.content);
    }
}
