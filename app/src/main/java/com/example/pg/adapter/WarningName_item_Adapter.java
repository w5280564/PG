package com.example.pg.adapter;

import android.annotation.SuppressLint;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.example.pg.R;
import com.example.pg.bean.CustomerName_Bean;
import com.example.pg.bean.Statistical_Bean;


/**
 * ================================================
 *
 * @Description: <p>
 * ================================================
 */
public class WarningName_item_Adapter extends BaseQuickAdapter<CustomerName_Bean.nameBean, BaseViewHolder> {
    public WarningName_item_Adapter() {
        super(R.layout.warning_name_item);
    }
    @SuppressLint("CheckResult")
    @Override
    protected void convert(@NonNull BaseViewHolder helper, CustomerName_Bean.nameBean item) {
        helper.setText(R.id.name_Tv,item.getShiptoName());
    }


}



