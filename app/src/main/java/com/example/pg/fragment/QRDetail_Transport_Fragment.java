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
 * 扫码-
 */
public class QRDetail_Transport_Fragment extends BaseLazyFragment {
    private String sn;
    private MyArrowItemView taskNo_MyView,vehicleNo_MyView,shiptoCode_MyView,shiptoName_MyView,deliveryCode_MyView,
            state_MyView,sendCode_MyView,sendName_MyView,sendTime_MyView;

    /**
     *
     * @param sn 扫码信息
     * @return
     */
    public static QRDetail_Transport_Fragment newInstance(String sn) {
        Bundle args = new Bundle();
        args.putString("sn", sn);
        QRDetail_Transport_Fragment fragment = new QRDetail_Transport_Fragment();
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
        return R.layout.transport_fragment;
    }

    @Override
    protected void initView(View mView) {
        taskNo_MyView =  mView.findViewById(R.id.taskNo_MyView);
        vehicleNo_MyView =  mView.findViewById(R.id.vehicleNo_MyView);
        shiptoCode_MyView =  mView.findViewById(R.id.shiptoCode_MyView);
        shiptoName_MyView =  mView.findViewById(R.id.shiptoName_MyView);
        deliveryCode_MyView =  mView.findViewById(R.id.deliveryCode_MyView);
        state_MyView =  mView.findViewById(R.id.state_MyView);
        sendCode_MyView =  mView.findViewById(R.id.sendCode_MyView);
        sendName_MyView =  mView.findViewById(R.id.sendName_MyView);
        sendTime_MyView =  mView.findViewById(R.id.sendTime_MyView);
    }

    @Override
    protected void loadData() {
        post(requireActivity(),xUtils3Http.BASE_URL+xUtils3Http.Transport);
    }


    /**
     * 运输
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

                    originalValue(transport_bean.getData().getTaskNo(), "暂未填写", "", taskNo_MyView.getTvContent());
                    originalValue(transport_bean.getData().getVehicleNo(), "暂未填写", "", vehicleNo_MyView.getTvContent());
                    originalValue(transport_bean.getData().getShiptoCode(), "暂未填写", "", shiptoCode_MyView.getTvContent());
                    originalValue(transport_bean.getData().getShiptoName(), "暂未填写", "", shiptoName_MyView.getTvContent());
                    originalValue(transport_bean.getData().getDeliveryCode(), "暂未填写", "", deliveryCode_MyView.getTvContent());
                    originalValue(transport_bean.getData().getSendCode(), "暂未填写", "", sendCode_MyView.getTvContent());
                    originalValue(transport_bean.getData().getSendName(), "暂未填写", "", sendName_MyView.getTvContent());
                    originalValue(transport_bean.getData().getSendTime(), "暂未填写", "", sendTime_MyView.getTvContent());
                    originalValue(transport_bean.getData().getShipmentOrderStatus(), "暂未填写", "", state_MyView.getTvContent());

//                    Object shipmentOrderStatus = transport_bean.getData().getShipmentOrderStatus();
//                    //1:received 2:receiving 3:reject 4:no QR
//                    String status = "";
//                    if (TextUtils.equals((CharSequence) shipmentOrderStatus,null)){
//                        status = "暂未填写";
//                    }else if (TextUtils.equals((CharSequence)shipmentOrderStatus,"1")){
//                        status = "received";
//                    }else if (TextUtils.equals((CharSequence)shipmentOrderStatus,"2")){
//                        status = "receiving";
//                    }else if (TextUtils.equals((CharSequence)shipmentOrderStatus,"3")){
//                        status = "reject";
//                    }else if (TextUtils.equals((CharSequence)shipmentOrderStatus,"4")){
//                        status = "no QR";
//                    }
//                        state_MyView.getTvContent().setText(status);

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
