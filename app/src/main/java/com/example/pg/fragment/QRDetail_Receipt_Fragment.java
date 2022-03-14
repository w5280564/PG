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
import com.example.pg.bean.Transport_Bean;
import com.example.pg.common.utils.GsonUtil;
import com.example.pg.common.utils.xUtils3Http;

import java.util.HashMap;
import java.util.Map;


/**
 * 扫码-收货
 */
public class QRDetail_Receipt_Fragment extends BaseLazyFragment {
    private String sn;
    private MyArrowItemView vehicleNo_MyView,shiptoCode_MyView,shiptoName_MyView,
            state_MyView,sendTime_MyView;

    /**
     *
     * @param sn 扫码信息
     * @return
     */
    public static QRDetail_Receipt_Fragment newInstance(String sn) {
        Bundle args = new Bundle();
        args.putString("sn", sn);
        QRDetail_Receipt_Fragment fragment = new QRDetail_Receipt_Fragment();
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
        return R.layout.receipt_fragment;
    }

    @Override
    protected void initView(View mView) {
        vehicleNo_MyView =  mView.findViewById(R.id.vehicleNo_MyView);
        shiptoCode_MyView =  mView.findViewById(R.id.shiptoCode_MyView);
        shiptoName_MyView =  mView.findViewById(R.id.shiptoName_MyView);
        state_MyView =  mView.findViewById(R.id.state_MyView);
        sendTime_MyView =  mView.findViewById(R.id.sendTime_MyView);
    }

    @Override
    protected void loadData() {
        post(requireActivity(),xUtils3Http.BASE_URL+xUtils3Http.Receipt);
    }


    /**
     * 收货
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
                Transport_Bean transport_bean = GsonUtil.getInstance().json2Bean(result, Transport_Bean.class);
                if (transport_bean != null) {
                    originalValue(transport_bean.getData().getVehicleNo(), "暂无数据", "", vehicleNo_MyView.getTvContent());
                    originalValue(transport_bean.getData().getShiptoCode(), "暂无数据", "", shiptoCode_MyView.getTvContent());
                    originalValue(transport_bean.getData().getShiptoName(), "暂无数据", "", shiptoName_MyView.getTvContent());
                    originalValue(transport_bean.getData().getShipmentOrderStatus(), "暂无数据", "", state_MyView.getTvContent());
                    originalValue(transport_bean.getData().getShipmentReceivedTime(), "暂无数据", "", sendTime_MyView.getTvContent());
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
