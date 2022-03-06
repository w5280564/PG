package com.example.pg.adapter;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.pg.R;
import com.example.pg.baseview.GlideUtils;


/**
 * ================================================
 *
 * @Description: <p>
 * ================================================
 */
public class MyAcfTimeAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    private View round_Tv;

    public MyAcfTimeAdapter() {
        super(R.layout.myacftime_item);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        if (position == 1 || position == 3){
            round_Tv.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("CheckResult")
    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        ImageView acf_Img = helper.getView(R.id.acf_Img);
         round_Tv = helper.getView(R.id.round_Tv);
        GlideUtils.loadBase64Image(mContext,acf_Img,item);
    }



}



