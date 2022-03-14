package com.example.pg.adapter;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.pg.R;
import com.example.pg.bean.CustomerAlarm_Bean;
import com.example.pg.bean.CustomerName_Bean;

import java.util.List;


/**
 * ================================================
 *
 * @Description: <p>
 * ================================================
 */
public class WarningCode_item_Adapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public WarningCode_item_Adapter() {
        super(R.layout.warning_name_item);
    }
    @SuppressLint("CheckResult")
    @Override
    protected void convert(@NonNull BaseViewHolder helper, String item) {


        helper.setText(R.id.name_Tv,item);
    }


}



