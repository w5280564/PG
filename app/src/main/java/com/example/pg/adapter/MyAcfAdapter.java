package com.example.pg.adapter;

import android.annotation.SuppressLint;
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
public class MyAcfAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public MyAcfAdapter() {
        super(R.layout.myacf_item);
    }
    @SuppressLint("CheckResult")
    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {
        ImageView acf_Img = helper.getView(R.id.acf_Img);
        GlideUtils.loadBase64Image(mContext,acf_Img,item);
    }



}



