package com.example.pg.activity;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.TextView;

import com.example.pg.R;
import com.example.pg.baseview.BaseActivity;
import com.gyf.barlibrary.ImmersionBar;


/**
 * 马肯码分析结果
 */
public class MarkenCode_Activity extends BaseActivity {
    private String makenStr="";

    /**
     * @param context
     * @param makenStr 马肯码分析结果
     */
    public static void startActivity(Context context,String makenStr) {
        Intent intent = new Intent(context, MarkenCode_Activity.class);
        intent.putExtra("makenStr",makenStr);
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
        return R.layout.marken_activity;
    }


    @Override
    protected void initLocalData() {
        super.initLocalData();
        makenStr = getIntent().getStringExtra("makenStr");
    }

    @Override
    protected void initView() {
       TextView maken_Tv = findMyViewById(R.id.maken_Tv);
        if (TextUtils.equals(makenStr,"0")){
            maken_Tv.setText("初步判断为假货");
        }else if (TextUtils.equals(makenStr,"1")){
            maken_Tv.setText("初步判断为正品");
        }else if (TextUtils.equals(makenStr,"2")){
            maken_Tv.setText("识别失败，请重新拍照");
        }
    }






}