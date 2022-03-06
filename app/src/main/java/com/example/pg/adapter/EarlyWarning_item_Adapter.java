package com.example.pg.adapter;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.pg.R;
import com.example.pg.bean.EarlyWarning_Detail_Bean;


/**
 * ================================================
 *
 * @Description: <p>
 * ================================================
 */
public class EarlyWarning_item_Adapter extends BaseQuickAdapter<EarlyWarning_Detail_Bean.DataDTO.DetialsDTO, BaseViewHolder> {
    public EarlyWarning_item_Adapter() {
        super(R.layout.earlywarning_item);
    }
    @SuppressLint("CheckResult")
    @Override
    protected void convert(@NonNull BaseViewHolder helper, EarlyWarning_Detail_Bean.DataDTO.DetialsDTO item) {
        helper.setText(R.id.style_Tv,item.getTicketType());
        helper.setText(R.id.count_Tv,item.getQuantity()+"");
    }



}



