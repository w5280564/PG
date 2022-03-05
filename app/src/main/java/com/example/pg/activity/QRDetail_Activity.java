package com.example.pg.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import com.example.pg.R;
import com.example.pg.baseview.BaseTabLayoutActivity;
import com.example.pg.bean.AppMenuBean;
import com.example.pg.fragment.QRDetail_Production_Fragment;
import com.gyf.barlibrary.ImmersionBar;


/**
 * 侧边栏 内部人列表
 */
public class QRDetail_Activity extends BaseTabLayoutActivity {
    private String sn;

    /**
     * @param context
     * @param sn 扫码信息
     */
    public static void startActivity(Context context, String sn) {
        Intent intent = new Intent(context, QRDetail_Activity.class);
        intent.putExtra("sn", sn);
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
        return R.layout.qrdetail_activity;
    }

    @Override
    protected void initLocalData() {
        sn = getIntent().getStringExtra("sn");
    }

    @Override
    protected void initView() {
        mViewPager = findViewById(R.id.card_ViewPager);
        mTabLayout = findViewById(R.id.card_Tab);
        initMenuData();
    }

    @SuppressLint("WrongConstant")
    private void initMenuData() {
        for (int i = 0; i < 2; i++) {
            AppMenuBean bean = new AppMenuBean();
            if (i == 0) {
                fragments.add(QRDetail_Production_Fragment.newInstance(sn));
                bean.setName("生产");
            } else {
                fragments.add(QRDetail_Production_Fragment.newInstance(sn));
                bean.setName("运输");
            }
            menuList.add(bean);
        }

        initViewPager(0);
    }


}