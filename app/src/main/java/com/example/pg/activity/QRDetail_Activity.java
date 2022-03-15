package com.example.pg.activity;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;

import com.example.pg.R;
import com.example.pg.baseview.BaseTabLayoutActivity;
import com.example.pg.bean.Acf_bean;
import com.example.pg.bean.AppMenuBean;
import com.example.pg.common.utils.GsonUtil;
import com.example.pg.common.utils.xUtils3Http;
import com.example.pg.fragment.QRDetail_ACF_Fragment;
import com.example.pg.fragment.QRDetail_Production_Fragment;
import com.example.pg.fragment.QRDetail_Receipt_Fragment;
import com.example.pg.fragment.QRDetail_Transport_Fragment;
import com.gyf.barlibrary.ImmersionBar;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * 扫描二维码
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

    @Override
    protected void getServerData() {
//        post(mActivity, xUtils3Http.BASE_URL + xUtils3Http.ACF);
    }

    @SuppressLint("WrongConstant")
    private void initMenuData() {
        ArrayList<String> tabName = new ArrayList<>();
        tabName.add("生产");
        tabName.add("运输");
        tabName.add("收货");
        tabName.add("ACFeature");
        for (int i = 0; i < tabName.size(); i++) {
            AppMenuBean bean = new AppMenuBean();
            bean.setName(tabName.get(i));
            menuList.add(bean);
        }
        fragments.add(QRDetail_Production_Fragment.newInstance(sn));
        fragments.add(QRDetail_Transport_Fragment.newInstance(sn));
        fragments.add(QRDetail_Receipt_Fragment.newInstance(sn));
        fragments.add(QRDetail_ACF_Fragment.newInstance(sn));
        initViewPager(0);
    }

    /**
     * acf
     *
     * @param context
     * @param baseUrl
     */
    public void post(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
//        sn = "http://weixin.qq.com/r/GTp-Z7zEmuHlrfgE928L/56903148138255/31389625292133367265/13191864H0/20241114/82313294";
        map.put("sn", sn);
        xUtils3Http.post(false,context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Acf_bean acf_bean = GsonUtil.getInstance().json2Bean(result, Acf_bean.class);
                if (acf_bean != null) {
                    MMKV mmkv = MMKV.defaultMMKV();
                    mmkv.encode(xUtils3Http.Acf_Bean,  result);
                }
            }
            @Override
            public void failed(String... args) {
                finish();
            }
        });
    }



}