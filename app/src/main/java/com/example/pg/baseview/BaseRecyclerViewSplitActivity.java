package com.example.pg.baseview;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.pg.R;
import com.example.pg.baseview.bean.BaseSplitIndexBean;
import com.example.pg.common.utils.ListUtils;
import com.example.pg.common.utils.StringUtil;


/**
 * 带分页的基类Activity
 */
public abstract class BaseRecyclerViewSplitActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener{
    protected SwipeRefreshLayout mSwipeRefreshLayout;
    protected RecyclerView mRecyclerView;
    protected BaseQuickAdapter mAdapter;
    protected int page = 1;
    protected int limit = 10;


    @Override
    protected int getLayoutId() {
        return 0;
    }




    protected void initSwipeRefreshLayoutAndAdapter(String emptyToastText,int emptyViewImgResource,boolean isHaveRefresh) {
        mAdapter.setEmptyView(addEmptyView(emptyToastText, emptyViewImgResource));
        mAdapter.setLoadMoreView(new CustomLoadMoreView());
        mAdapter.setOnLoadMoreListener(this,mRecyclerView);
        if (isHaveRefresh && mSwipeRefreshLayout!=null) {
            mSwipeRefreshLayout.setColorSchemeColors(ContextCompat.getColor(mActivity, R.color.animal_color));
            mSwipeRefreshLayout.setOnRefreshListener(this);
        }
    }



    /**
     * 全局处理分页的公共方法
     * @param obj  具体的分页对象  列表适配器
     * @param mAdapter
     */
    protected void handleSplitListData(BaseSplitIndexBean obj, BaseQuickAdapter mAdapter, int mPageSize) {
        if(obj!=null){
            int allCount = StringUtil.isBlank(obj.getTotalPages())?0:Integer.parseInt(obj.getTotalPages());
            int bigPage = 0;//最大页
            if(allCount%mPageSize!=0){
                bigPage=allCount/mPageSize+1;
            }else{
                bigPage=allCount/mPageSize;
            }
            if(page==bigPage){
                mAdapter.loadMoreEnd();//显示“没有更多数据”
            }

            boolean isRefresh = page==1?true:false;
            if(!ListUtils.isEmpty(obj.getContent())){
                int size = obj.getContent().size();

                if (isRefresh) {
                    mAdapter.setNewData(obj.getContent());
                } else {
                    mAdapter.addData(obj.getContent());
                }


                if (size < mPageSize) {
                    mAdapter.loadMoreEnd(isRefresh);//第一页的话隐藏“没有更多数据”，否则显示“没有更多数据”
                } else {
                    mAdapter.loadMoreComplete();
                }
            }else{

                if (isRefresh) {
                    mAdapter.setNewData(null);//刷新列表
                } else {
                    mAdapter.loadMoreEnd(false);//显示“没有更多数据”
                }
            }
        }
    }


    @Override
    public void onLoadMoreRequested() {
        onLoadMoreData();
    }

    @Override
    public void onRefresh() {
        onRefreshData();
    }


    /**
     * 下拉刷新
     */
    protected abstract void onRefreshData();


    /**
     * 上拉分页
     */
    protected abstract void onLoadMoreData();
}
