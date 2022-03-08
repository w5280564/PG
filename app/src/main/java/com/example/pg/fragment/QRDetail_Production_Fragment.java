package com.example.pg.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.example.pg.R;
import com.example.pg.baseview.BaseLazyFragment;
import com.example.pg.baseview.MyArrowItemView;
import com.example.pg.bean.Production_Bean;
import com.example.pg.common.utils.GsonUtil;
import com.example.pg.common.utils.xUtils3Http;

import java.util.HashMap;
import java.util.Map;


/**
 * 扫码-生产
 */
public class QRDetail_Production_Fragment extends BaseLazyFragment {
    private String sn;
    private MyArrowItemView productNo_MyView,productName_MyView,PlantName_MyView,plantCode_MyView,agLevel_MyView,
            gtin_MyView,batchNo_MyView,productDate_MyView,lineCode_MyView;

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
        productNo_MyView =  mView.findViewById(R.id.productNo_MyView);
        productName_MyView =  mView.findViewById(R.id.productName_MyView);
        PlantName_MyView =  mView.findViewById(R.id.PlantName_MyView);
        plantCode_MyView =  mView.findViewById(R.id.plantCode_MyView);
        agLevel_MyView =  mView.findViewById(R.id.agLevel_MyView);
        gtin_MyView =  mView.findViewById(R.id.gtin_MyView);
        batchNo_MyView =  mView.findViewById(R.id.batchNo_MyView);
        productDate_MyView =  mView.findViewById(R.id.productDate_MyView);
        lineCode_MyView =  mView.findViewById(R.id.lineCode_MyView);
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
//        sn = "http://weixin.qq.com/r/GTp-Z7zEmuHlrfgE928L/56903148138255/31389625292133367265/13191864H0/20241114/82313294";
        map.put("sn", sn);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Production_Bean login_bean = GsonUtil.getInstance().json2Bean(result, Production_Bean.class);
                if (login_bean != null) {

                    originalValue(login_bean.getData().getProductNo(), "暂未填写", "", productNo_MyView.getTvContent());
                    originalValue(login_bean.getData().getProductName(), "暂未填写", "", productName_MyView.getTvContent());
                    originalValue(login_bean.getData().getPlantName(), "暂未填写", "", PlantName_MyView.getTvContent());
                    originalValue(login_bean.getData().getPlantCode(), "暂未填写", "", plantCode_MyView.getTvContent());

//                    originalValue(login_bean.getData().getAggregationLevel(), "暂未填写", "", agLevel_MyView.getTvContent());
                    String aggregationLevel = login_bean.getData().getAggregationLevel();
                    if (TextUtils.equals(aggregationLevel,"3")){
                        agLevel_MyView.getTvContent().setText("Case");
                    }else {
                        agLevel_MyView.getTvContent().setText("Item");
                    }
                    originalValue(login_bean.getData().getLineCode(), "暂未填写", "", lineCode_MyView.getTvContent());
                    originalValue(login_bean.getData().getGtin(), "暂未填写", "", gtin_MyView.getTvContent());
                    originalValue(login_bean.getData().getBatchNo(), "暂未填写", "", batchNo_MyView.getTvContent());
                    originalValue(login_bean.getData().getProductDate(), "暂未填写", "", productDate_MyView.getTvContent());
                }
            }

            @Override
            public void failed(String... args) {

            }
        });
    }


    /**
     * 没有数据添加默认值
     *
     * @param value
     * @param originalStr
     */
    private void originalValue(Object value, String originalStr, String hint, TextView tv) {
        if (TextUtils.isEmpty((CharSequence) value)) {
            tv.setText(hint + originalStr);
        } else {
            tv.setText(hint +  value);
        }
    }





}
