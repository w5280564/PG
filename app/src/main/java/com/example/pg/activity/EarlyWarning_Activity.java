package com.example.pg.activity;


import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.pg.R;
import com.example.pg.adapter.WarningCode_item_Adapter;
import com.example.pg.adapter.WarningName_item_Adapter;
import com.example.pg.baseview.BaseActivity;
import com.example.pg.bean.CustomerAlarm_Bean;
import com.example.pg.bean.CustomerName_Bean;
import com.example.pg.bean.EarlyWarning_Detail_Bean;
import com.example.pg.common.utils.GsonUtil;
import com.example.pg.common.utils.ListUtils;
import com.example.pg.common.utils.T;
import com.example.pg.common.utils.xUtils3Http;
import com.gyf.barlibrary.ImmersionBar;

import java.util.HashMap;
import java.util.Map;

import cn.bingoogolapple.transformerstip.TransformersTip;
import cn.bingoogolapple.transformerstip.gravity.TipGravity;


/**
 * 预警分析
 */
public class EarlyWarning_Activity extends BaseActivity implements View.OnClickListener {
    private String sn;
    private EditText customerName_Tv;
    private TextView  code_Tv, search_Tv;
    private String shiptoCode = "";
    private String taskNo = "";
    private WarningName_item_Adapter nameAdapter;
    private WarningCode_item_Adapter codeAdapter;
    private String nameCode = "";

    /**
     * @param context
     */
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, EarlyWarning_Activity.class);
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
        return R.layout.earlywarning_activity;
    }

    @Override
    protected void initLocalData() {
        sn = getIntent().getStringExtra("sn");
    }

    @Override
    protected void initView() {

        customerName_Tv = findMyViewById(R.id.customerName_Tv);
        code_Tv = findMyViewById(R.id.code_Tv);
        search_Tv = findMyViewById(R.id.search_Tv);
    }

    @Override
    protected void initEvent() {
        customerName_Tv.setOnTouchListener(new customerNameOnTouch());
        customerName_Tv.addTextChangedListener(new TextChangeListener());
        code_Tv.setOnClickListener(this);
        search_Tv.setOnClickListener(this);
    }

    @Override
    protected void getServerData() {
        post(mActivity, xUtils3Http.BASE_URL + xUtils3Http.Customer,false);
    }

    /**
     * 客户名称
     *
     * @param context
     * @param baseUrl
     * @param showPop 是否展示弹窗
     */
    CustomerName_Bean customerName_bean;

    public void post(Context context, String baseUrl,boolean showPop) {
        Map<String, Object> map = new HashMap<>();
        map.put("shiptoCode", nameCode);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                customerName_bean = GsonUtil.getInstance().json2Bean(result, CustomerName_Bean.class);
                if (showPop){
                    showPopWindow(showPop);
                }
            }
            @Override
            public void failed(String... args) {

            }
        });
    }

    /**
     * 业务编码
     *
     * @param context
     * @param baseUrl
     */
    CustomerAlarm_Bean customerAlarm_bean;

    public void postCode(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("shiptoCode", shiptoCode);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                customerAlarm_bean = GsonUtil.getInstance().json2Bean(result, CustomerAlarm_Bean.class);
            }

            @Override
            public void failed(String... args) {

            }
        });
    }

    /**
     * 警告详细信息
     *
     * @param context
     * @param baseUrl
     */
    public void postReport(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("shiptoCode", shiptoCode);
        map.put("taskNo", taskNo);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                EarlyWarning_Detail_Bean earlyWarningDetailBean = GsonUtil.getInstance().json2Bean(result, EarlyWarning_Detail_Bean.class);
                EarlyWarning_Detail_Activity.startActivity(context, earlyWarningDetailBean);
                beforeString = "";
                customerName_Tv.setText("");
                customerName_Tv.setHint("");
                code_Tv.setText("");
            }

            @Override
            public void failed(String... args) {

            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.code_Tv:
//                setCode(code_Tv);
                if (customerAlarm_bean == null) {
                    return;
                }
                if (ListUtils.isEmpty(customerAlarm_bean.getData())) {
                    T.s("没有业务", 2000);
                    return;
                }
                showCodePopWindow();
                break;
            case R.id.search_Tv:
//                if (TextUtils.isEmpty(customerName_Tv.getText())) {
//                    T.s("请选择客户名称", 3000);
//                    return;
//                }

                if (TextUtils.isEmpty(code_Tv.getText())) {
                    T.s("请选择业务", 3000);
                    return;
                }
                postReport(mActivity, xUtils3Http.BASE_URL + xUtils3Http.Tickets);
                break;
        }
    }

    //处理点击事件
    private class customerNameOnTouch implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            /**加这个判断，防止该事件被执行两次*/
            if (event.getAction() == MotionEvent.ACTION_UP) {
//                showPopWindow(false);
                isChange = false;//
                nameCode = beforeString;
                post(mActivity, xUtils3Http.BASE_URL + xUtils3Http.Customer,true);

            }
            return false;
        }
    }
    //点击弹窗以外区域 处理事件
    @Override
    public boolean dispatchTouchEvent(MotionEvent event){
        if(namePop!=null&&namePop.isShowing()){
            beforeString = customerName_Tv.getText().toString();
            namePop.dismissTip();
            closeInput();
            return false;
        }
        return super.dispatchTouchEvent(event);
    }


    String beforeString = "";
    //键盘输入监听
    private class TextChangeListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!isChange){
                isChange = false;
                nameCode = s.toString();
                post(mActivity, xUtils3Http.BASE_URL + xUtils3Http.Customer,true);
            }
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    }


    //关闭软键盘
    private void closeInput(){
        InputMethodManager imm = (InputMethodManager)EarlyWarning_Activity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(customerName_Tv.getWindowToken(), 0);
    }

    TransformersTip namePop;
    // 代码中设置浮窗位置，在布局文件中设置浮窗背景、箭头位置  选中客户名称弹窗
    private void showPopWindow(boolean showPop) {
        if (customerName_bean == null) {
            return;
        }
        if (namePop != null && namePop.isShowing()){
            namePop.dismissTip();
        }
        addPop();

    }

    private void addPop(){
            View anchorView = findViewById(R.id.customerName_Tv);
            namePop = new TransformersTip(anchorView, R.layout.customername_tip) {
                @Override
                protected void initView(View contentView) {
                    RecyclerView tip_Recycler = contentView.findViewById(R.id.tip_Recycler);
                    tipRecyclerView(tip_Recycler);
                    if (customerName_bean != null) {
                        nameAdapter.setNewData(customerName_bean.getData());
                    }
                }
            }
                    .setTipGravity(TipGravity.TO_BOTTOM_CENTER) // 设置浮窗相对于锚点控件展示的位置
                    .setTipOffsetXDp(0) // 设置浮窗在 x 轴的偏移量
                    .setTipOffsetYDp(-6) // 设置浮窗在 y 轴的偏移量
                    .setBackgroundDimEnabled(false) // 设置是否允许浮窗的背景变暗
                    .setDismissOnTouchOutside(true) // 设置点击浮窗外部时是否自动关闭浮窗
                    .show(); // 显示浮窗
            namePop.setFocusable(false);
            namePop.setOutsideTouchable(false);//设置让弹窗点击外部区域失效

    }

    boolean isChange = false;
    public void tipRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(linearLayoutManager);
        nameAdapter = new WarningName_item_Adapter();
        recyclerView.setAdapter(nameAdapter);
        nameAdapter.setEmptyView(addEmptyView("暂无数据", 0));
        nameAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                CustomerName_Bean.nameBean item = (CustomerName_Bean.nameBean) adapter.getItem(position);
                String name = item.getShiptoName();
                beforeString = customerName_Tv.getText().toString();
                 isChange = true;
                customerName_Tv.setText("");
//                customerName_Tv.setFocusable(false);
                customerName_Tv.setHint(name);
                shiptoCode = item.getShiptoCode();
                if (namePop != null) {
                    namePop.dismissTip();
                    closeInput();
                }
                postCode(mActivity, xUtils3Http.BASE_URL + xUtils3Http.CustomerAlarm);
            }
        });
    }

    TransformersTip codePop;
    // 代码中设置浮窗位置，在布局文件中设置浮窗背景、箭头位置  选中业务编码
    private void showCodePopWindow() {
        View anchorView = findViewById(R.id.code_Tv);
        codePop = new TransformersTip(anchorView, R.layout.customern_code_tip) {
            @Override
            protected void initView(View contentView) {
                RecyclerView tip_Recycler = contentView.findViewById(R.id.tip_Recycler);
                codeRecyclerView(tip_Recycler);
                if (customerAlarm_bean != null) {
                    codeAdapter.setNewData(customerAlarm_bean.getData());
                }
            }
        }
                .setTipGravity(TipGravity.TO_BOTTOM_CENTER) // 设置浮窗相对于锚点控件展示的位置
                .setTipOffsetXDp(0) // 设置浮窗在 x 轴的偏移量
                .setTipOffsetYDp(-6) // 设置浮窗在 y 轴的偏移量
                .setBackgroundDimEnabled(true) // 设置是否允许浮窗的背景变暗
                .setDismissOnTouchOutside(true) // 设置点击浮窗外部时是否自动关闭浮窗
                .show(); // 显示浮窗
    }


    public void codeRecyclerView(RecyclerView recyclerView) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(linearLayoutManager);
        codeAdapter = new WarningCode_item_Adapter();
        recyclerView.setAdapter(codeAdapter);
        codeAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String item = (String) adapter.getItem(position);
                String code = item;
                code_Tv.setText(code);
                taskNo = code;
                if (codePop != null) {
                    codePop.dismissTip();
                }
            }
        });
    }



}