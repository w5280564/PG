package com.example.pg.adapter;

import android.annotation.SuppressLint;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.pg.R;
import com.example.pg.bean.Statistical_Bean;


/**
 * ================================================
 *
 * @Description: <p>
 * ================================================
 */
public class Customer_item_Adapter extends BaseQuickAdapter<Statistical_Bean, BaseViewHolder> {
    public Customer_item_Adapter() {
        super(R.layout.statistical_item);
    }
    @SuppressLint("CheckResult")
    @Override
    protected void convert(@NonNull BaseViewHolder helper, Statistical_Bean item) {
       TextView name_Tv =  helper.getView(R.id.name_Tv);
       name_Tv.setTextColor(ContextCompat.getColor(mContext,R.color.text_color_a1a1a1));
       name_Tv.setText(item.getDivision());
//        helper.setText(R.id.name_Tv,item.getDivision());
        helper.setText(R.id.code_Tv,item.getTaskNo()+"");
        helper.setText(R.id.ying_Tv,item.getShouldScanNumber()+"");
        helper.setText(R.id.shi_Tv,item.getRealScanNumber()+"");
        helper.setText(R.id.lv_Tv,item.getScanRate()+"");
    }



}



