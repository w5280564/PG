package com.example.pg.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.pg.R;
import com.example.pg.adapter.MyAcfAdapter;
import com.example.pg.adapter.MyAcfTimeAdapter;
import com.example.pg.baseview.BaseLazyFragment;
import com.example.pg.baseview.MyArrowItemView;
import com.example.pg.bean.Acf_bean;
import com.example.pg.bean.Transport_Bean;
import com.example.pg.common.utils.GsonUtil;
import com.example.pg.common.utils.xUtils3Http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 扫码—ACF
 */
public class QRDetail_ACF_Fragment extends BaseLazyFragment {
    private String sn;
    private RecyclerView mRecyclerView,threeRecyclerVie,TimeRecyclerVie;
    private MyAcfAdapter mAdapter;
    private MyAcfAdapter threeAdapter;
    private MyAcfTimeAdapter TimeAdapter;

    /**
     * @param sn 扫码信息
     * @return
     */
    public static QRDetail_ACF_Fragment newInstance(String sn) {
        Bundle args = new Bundle();
        args.putString("sn", sn);
        QRDetail_ACF_Fragment fragment = new QRDetail_ACF_Fragment();
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
        return R.layout.acf_fragment;
    }

    @Override
    protected void initView(View mView) {
        mRecyclerView = mView.findViewById(R.id.mRecyclerView);
        threeRecyclerVie = mView.findViewById(R.id.threeRecyclerVie);
        TimeRecyclerVie = mView.findViewById(R.id.TimeRecyclerVie);
        initRecyclerView();
        initThreeRecyclerView();
        initTimeRecyclerVie();
    }

    @Override
    protected void loadData() {
        post(requireActivity(), xUtils3Http.BASE_URL + xUtils3Http.ACF);
    }


    public void initRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(),5);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mAdapter = new MyAcfAdapter();
        mRecyclerView.setAdapter(mAdapter);
    }
    public void initThreeRecyclerView() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireActivity(),3);
        threeRecyclerVie.setLayoutManager(gridLayoutManager);
        threeAdapter = new MyAcfAdapter();
        threeRecyclerVie.setAdapter(threeAdapter);
    }

        public void initTimeRecyclerVie() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        TimeRecyclerVie.setLayoutManager(linearLayoutManager);
        TimeAdapter = new MyAcfTimeAdapter();
        TimeRecyclerVie.setAdapter(TimeAdapter);
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
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Acf_bean acf_bean = GsonUtil.getInstance().json2Bean(result, Acf_bean.class);
                if (acf_bean != null) {
                    List<String> topImgList = new ArrayList<>();
                    for (int i=0;i<15;i++){
                        topImgList.add(acf_bean.getData().get(i));
                    }
                    mAdapter.addData(topImgList);
                    List<String> threeImgList = new ArrayList<>();
                     for (int i=0;i<3;i++){
                         threeImgList.add(acf_bean.getData().get(i+topImgList.size()));
                    }
                    threeAdapter.addData(threeImgList);

                    List<String> timeImgList = new ArrayList<>();
                     for (int i=0;i<6;i++){
                         int index = topImgList.size()+threeImgList.size()+i;
                         timeImgList.add(acf_bean.getData().get(index));
                    }
                    TimeAdapter.addData(timeImgList);



                }
            }

            @Override
            public void failed(String... args) {

            }
        });
    }


}
