package com.example.pg.baseview;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public abstract class BaseFragment extends Fragment {
    protected MyApplication mApplication;
    protected Context mContext;
    protected BaseActivity mActivity;
//    private Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(getLayoutId(), container, false);
//        unbinder = ButterKnife.bind(this, mRootView);
        mApplication = MyApplication.getInstance();
//        mContext = mApplication.getContext();
        mActivity = (BaseActivity) getActivity();
        initLocalData();
        //view与数据绑定
        initView();
        initView(mRootView);
        //设置监听
        initEvent();
        //请求服务端接口数据
        getServerData();
        return mRootView;

    }



    /**
     * 绑定布局文件
     */
    protected abstract int getLayoutId();


    /**
     * 初始化本地数据
     */
    protected void initLocalData() {

    }


    /**
     * 初始化view
     */
    protected void initView() {

    }

    /**
     * 初始化view
     */
    protected void initView(View mRootView) {

    }


    /**
     * 设置监听
     */
    protected void initEvent() {

    }


    /**
     * 请求服务端接口数据
     */
    protected void getServerData() {

    }





    /**
     * 注册EventBus
     */
    protected void registerEventBus() {
//        if (!EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().register(this);
//        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 建议在`自定义页面`的页面结束函数中调用
//        unbinder.unbind();
//        if (EventBus.getDefault().isRegistered(this)) {
//            EventBus.getDefault().unregister(this);
//        }
    }


    /**
     * 功能描述:简单地Activity的跳转(不携带任何数据)
     */
    protected void skipAnotherActivity(Class<? extends Activity> targetActivity) {
        Intent intent = new Intent(mActivity, targetActivity);
        startActivity(intent);
    }


    /**
     * 跳转到默认H5展示页面
     */
    protected void jumpToWebView(String title, String url) {
        Intent intent = new Intent(mActivity, WebviewActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("url", url);
        startActivity(intent);
    }





    protected void showCommonToastDialog(String description) {
//        ToastDialog mDialog = new ToastDialog(mActivity, getString(R.string.toast_warm_prompt), description, getString(R.string.Sure), new ToastDialog.DialogConfirmListener() {
//            @Override
//            public void onConfirmClick() {
//
//            }
//        });
//        mDialog.show();
    }





//    protected View addEmptyView(String text, int imgResource) {
//        View emptyView = LayoutInflater.from(mActivity).inflate(R.layout.empty_view_layout, null);
//        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT));
//        ImageView iv_noData = emptyView.findViewById(R.id.iv_noData);
//
//        if (!StringUtil.isBlank(text)) {
//            TextView tv_noData = emptyView.findViewById(R.id.tv_noData);
//            tv_noData.setText(text);
//        }
//
//        if (imgResource != 0) {
//            iv_noData.setImageResource(imgResource);
//        } else {
//            iv_noData.setImageResource(R.mipmap.icon_no_date);
//        }
//
//        return emptyView;
//    }



//    @Override
//    public void initImmersionBar() {
//
//    }













}

