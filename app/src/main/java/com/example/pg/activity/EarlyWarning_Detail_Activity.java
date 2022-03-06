package com.example.pg.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.pg.R;
import com.example.pg.adapter.EarlyWarning_item_Adapter;
import com.example.pg.adapter.MyAcfAdapter;
import com.example.pg.baseview.BaseActivity;
import com.example.pg.bean.CustomerAlarm_Bean;
import com.example.pg.bean.CustomerName_Bean;
import com.example.pg.bean.EarlyWarning_Detail_Bean;
import com.example.pg.common.utils.GsonUtil;
import com.example.pg.common.utils.ListUtils;
import com.example.pg.common.utils.T;
import com.example.pg.common.utils.xUtils3Http;
import com.gyf.barlibrary.ImmersionBar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 预警分析详情
 */
public class EarlyWarning_Detail_Activity extends BaseActivity {

    private EarlyWarning_Detail_Bean earlyWarning_detail_bean;
    private RecyclerView mRecyclerView;
    private EarlyWarning_item_Adapter mAdapter;
    private TextView shiptoCode_Tv,shiptoName_Tv,sendCode_Tv,sendName_Tv,taskNo_Tv;

    /**
     * @param context

     */
    public static void startActivity(Context context, EarlyWarning_Detail_Bean earlyWarning_detail_bean) {
        Intent intent = new Intent(context, EarlyWarning_Detail_Activity.class);
        intent.putExtra("earlyWarning_detail_bean",  earlyWarning_detail_bean);
        context.startActivity(intent);
    }


    /**
     * 设置状态栏
     */
    private void setBar() {
        ImmersionBar.with(this)
                .fitsSystemWindows(true)
                .statusBarColor(R.color.app_main_color)     //状态栏颜色，不写默认透明色
                .statusBarDarkFont(true)
                .init();
    }

    @Override
    protected void setStatusBar() {
        setBar();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.earlywarning_detail;
    }

    @Override
    protected void initLocalData() {
        earlyWarning_detail_bean = (EarlyWarning_Detail_Bean) getIntent().getSerializableExtra("earlyWarning_detail_bean");
    }

    @Override
    protected void initView() {
        mRecyclerView = findViewById(R.id.mRecyclerView);
        initRecyclerView();
        shiptoCode_Tv = findViewById(R.id.shiptoCode_Tv);
        shiptoName_Tv = findViewById(R.id.shiptoName_Tv);
        sendCode_Tv = findViewById(R.id.sendCode_Tv);
        sendName_Tv = findViewById(R.id.sendName_Tv);
        taskNo_Tv = findViewById(R.id.taskNo_Tv);
    }


    @Override
    protected void getServerData() {
        addData();
    }

    private void addData() {
        if (earlyWarning_detail_bean != null){
            mAdapter.addData(earlyWarning_detail_bean.getData().getDetials());
            shiptoCode_Tv.setText(earlyWarning_detail_bean.getData().getShiptoCode());
            shiptoName_Tv.setText(earlyWarning_detail_bean.getData().getShiptoName());
            sendCode_Tv.setText(earlyWarning_detail_bean.getData().getSendCode());
            sendName_Tv.setText(earlyWarning_detail_bean.getData().getSendName());
            taskNo_Tv.setText(earlyWarning_detail_bean.getData().getTaskNo());
        }
    }


    public void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new EarlyWarning_item_Adapter();
        mRecyclerView.setAdapter(mAdapter);
    }





}