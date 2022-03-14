package com.example.pg.activity;


import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.example.pg.R;
import com.example.pg.baseview.BaseActivity;
import com.gyf.immersionbar.ImmersionBar;


/**
 * 统计分析
 */
public class DcReport_Activity extends BaseActivity implements View.OnClickListener {
    private TextView dcCode_Tv,knCode_Tv;

    /**
     * @param context

     */
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, DcReport_Activity.class);
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
        return R.layout.dcreport_activity;
    }


    @Override
    protected void initView() {
        dcCode_Tv =   findMyViewById(R.id.dcCode_Tv);
        knCode_Tv =   findMyViewById(R.id.knCode_Tv);

    }

    @Override
    protected void initEvent() {
        dcCode_Tv.setOnClickListener(this);
        knCode_Tv.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.dcCode_Tv:
                skipAnotherActivity(StatisticalDetail_Activity.class);
                break;
            case R.id.knCode_Tv:
                skipAnotherActivity(Customer_Detail_Activity.class);
                break;

        }
    }


}