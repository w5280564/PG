package com.example.pg.adapter;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.pg.R;
import com.example.pg.bean.EarlyWarning_Detail_Bean;
import com.example.pg.bean.Statistical_Bean;


/**
 * ================================================
 *
 * @Description: <p>
 * ================================================
 */
public class Statistical_item_Adapter extends BaseQuickAdapter<Statistical_Bean, BaseViewHolder> {
    public Statistical_item_Adapter() {
        super(R.layout.statistical_item);
    }
    @SuppressLint("CheckResult")
    @Override
    protected void convert(@NonNull BaseViewHolder helper, Statistical_Bean item) {
        helper.setText(R.id.name_Tv,item.getDcName());
        helper.setText(R.id.code_Tv,item.getTaskNo()+"");
        helper.setText(R.id.ying_Tv,item.getShouldScanNumber()+"");
        helper.setText(R.id.shi_Tv,item.getRealScanNumber()+"");
        helper.setText(R.id.lv_Tv,item.getScanRate()+"");
    }



}



