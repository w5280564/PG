package com.example.pg.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.example.pg.MainActivity;
import com.example.pg.R;
import com.example.pg.baseview.BaseLazyFragment;
import com.example.pg.bean.Login_Bean;
import com.example.pg.bean.Production_Bean;
import com.example.pg.common.utils.GsonUtil;
import com.example.pg.common.utils.xUtils3Http;
import com.tencent.mmkv.MMKV;

import java.util.HashMap;
import java.util.Map;


/**
 * 扫码-生产
 */
public class QRDetail_Production_Fragment extends BaseLazyFragment {
    private String sn;

    /**
     *
     * @param sn 扫码信息
     * @return
     */
    public static QRDetail_Production_Fragment newInstance(String sn) {
        Bundle args = new Bundle();
        args.putString("sn", sn);
        QRDetail_Production_Fragment fragment = new QRDetail_Production_Fragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    protected void initLocalData() {
        super.initLocalData();
        sn = getArguments().getString("sn");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.production_fragment;
    }

    @Override
    protected void initView(View mView) {

    }

    @Override
    protected void loadData() {
        post(requireActivity(),xUtils3Http.BASE_URL+xUtils3Http.Product);
    }


    /**
     * 生产信息
     *
     * @param context
     * @param baseUrl
     */
    public void post(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        sn = "http://weixin.qq.com/r/GTp-Z7zEmuHlrfgE928L/56903148138255/31389625292133367265/13191864H0/20241114/82313294";
        map.put("sn", sn);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Production_Bean login_bean = GsonUtil.getInstance().json2Bean(result, Production_Bean.class);
                if (login_bean != null) {

                }
            }

            @Override
            public void failed(String... args) {

            }
        });
    }







}
