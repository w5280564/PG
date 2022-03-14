package com.example.pg.activity;


import android.content.Context;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pg.R;
import com.example.pg.adapter.Customer_item_Adapter;
import com.example.pg.baseview.BaseRecyclerViewSplitActivity;
import com.example.pg.baseview.PagTab;
import com.example.pg.bean.Statistical_List_Bean;
import com.example.pg.common.utils.GsonUtil;
import com.example.pg.common.utils.xUtils3Http;
import com.gyf.immersionbar.ImmersionBar;

import java.util.HashMap;
import java.util.Map;


/**
 * 客户扫码率
 */
public class Customer_Detail_Activity extends BaseRecyclerViewSplitActivity {
    private RecyclerView mRecyclerView;
    private PagTab pagTab;

    /**
     * @param context

     */
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, Customer_Detail_Activity.class);
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
        return R.layout.customer_detail;
    }



    @Override
    protected void initView() {
        pagTab = findViewById(R.id.pagTab);
        pagTab.setmListener(new pagTabOnChange());
        pagTab.setFirstAndLastListener(new fAndLClick());



        mRecyclerView = findViewById(R.id.mRecyclerView);
        initRecyclerView();
//        initSwipeRefreshLayoutAndAdapter("暂无数据", 0, true);
    }

    @Override
    protected void getServerData() {
        post(mActivity, xUtils3Http.BASE_URL+xUtils3Http.Customer_Scan);
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
        mAdapter = new Customer_item_Adapter();
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     *
     *客户
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
//                if (page == 1 && mSwipeRefreshLayout.isRefreshing()) {
//                    mSwipeRefreshLayout.setRefreshing(false);
//                }
                String data = GsonUtil.getInstance().getValue(result, "data");
                Statistical_List_Bean statisticalListBean = GsonUtil.getInstance().json2Bean(data, Statistical_List_Bean.class);
                if (statisticalListBean != null) {
                    mAdapter.setNewData(statisticalListBean.getContent());
                    Integer totalElements = Integer.valueOf(statisticalListBean.getTotalElements());
                    pagTab.setTotalCount(totalElements);
                }

            }
            @Override
            public void failed(String... args) {

            }
        });
    }

    public void postOther(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("current", page);
        map.put("size", limit);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                String data = GsonUtil.getInstance().getValue(result, "data");
                Statistical_List_Bean statisticalListBean = GsonUtil.getInstance().json2Bean(data, Statistical_List_Bean.class);
                if (statisticalListBean != null) {
                    mRecyclerView.removeAllViews();
                    mAdapter.setNewData(statisticalListBean.getContent());
                }
            }
            @Override
            public void failed(String... args) {

            }
        });
    }

    /**
     * 分页选择器中间选中
     */
    private class pagTabOnChange implements PagTab.OnChangedListener {
        @Override
        public void onPageSelectedChanged(int currentPapePos, int lastPagePos, int totalPageCount, int total) {
            page = currentPapePos;
            postOther(mActivity, xUtils3Http.BASE_URL+xUtils3Http.Customer_Scan);
        }

        @Override
        public void onPerPageCountChanged(int perPageCount) {
            // x条/页 选项改变时触发
        }
    }

    /**
     * 选择器第一页最后一页
     */
    private class fAndLClick implements PagTab.FirstAndLastListener {
        @Override
        public void OnFirstClick(int currentPapePos) {
            page = currentPapePos;
            postOther(mActivity, xUtils3Http.BASE_URL+xUtils3Http.Customer_Scan);
        }

        @Override
        public void OnLastClick(int currentPapePos) {
            page = currentPapePos;
            postOther(mActivity, xUtils3Http.BASE_URL+xUtils3Http.Customer_Scan);
        }
    }


}