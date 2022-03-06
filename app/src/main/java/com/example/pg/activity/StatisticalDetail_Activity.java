package com.example.pg.activity;


import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.pg.R;
import com.example.pg.adapter.Statistical_item_Adapter;
import com.example.pg.baseview.BaseRecyclerViewSplitActivity;
import com.example.pg.bean.Statistical_Bean;
import com.example.pg.bean.Statistical_List_Bean;
import com.example.pg.common.utils.GsonUtil;
import com.example.pg.common.utils.xUtils3Http;
import com.gyf.barlibrary.ImmersionBar;

import java.util.HashMap;
import java.util.Map;


/**
 * DC扫码率
 */
public class StatisticalDetail_Activity extends BaseRecyclerViewSplitActivity {
    private RecyclerView mRecyclerView;

    /**
     * @param context

     */
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, StatisticalDetail_Activity.class);
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
        return R.layout.statistical_detail;
    }



    @Override
    protected void initView() {
        mRecyclerView = findViewById(R.id.mRecyclerView);
        mSwipeRefreshLayout = findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setRefreshing(true);
        initRecyclerView();
        initSwipeRefreshLayoutAndAdapter("暂无数据", 0, true);
    }

    @Override
    protected void getServerData() {
        post(mActivity, xUtils3Http.BASE_URL+xUtils3Http.Dcreport);
    }

    @Override
    protected void onRefreshData() {
        page = 1;
       getServerData();
    }
    @Override
    protected void onLoadMoreData() {
        page++;
        getServerData();
    }

    public void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new Statistical_item_Adapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Statistical_Bean statistical_bean = (Statistical_Bean) adapter.getItem(position);
                StatisticalDetail_One_Activity.startActivity(mActivity,statistical_bean.getDcCode(),statistical_bean.getDcName());
            }
        });
    }

    /**
     *
     *DC列表
     * @param context
     * @param baseUrl
     */

    public void post(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("current",page);
        map.put("size",limit);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                if (page == 1 && mSwipeRefreshLayout.isRefreshing()) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
                String data = GsonUtil.getInstance().getValue(result, "data");
                Statistical_List_Bean statisticalListBean = GsonUtil.getInstance().json2Bean(data, Statistical_List_Bean.class);
                handleSplitListData(statisticalListBean, mAdapter, limit);

            }
            @Override
            public void failed(String... args) {

            }
        });
    }




}