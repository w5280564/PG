package com.example.pg.activity;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.example.pg.R;
import com.example.pg.baseview.BaseActivity;
import com.example.pg.bean.CustomerAlarm_Bean;
import com.example.pg.bean.CustomerName_Bean;
import com.example.pg.bean.EarlyWarning_Detail_Bean;
import com.example.pg.common.utils.GsonUtil;
import com.example.pg.common.utils.ListUtils;
import com.example.pg.common.utils.T;
import com.example.pg.common.utils.xUtils3Http;
import com.gyf.barlibrary.ImmersionBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 预警分析
 */
public class EarlyWarning_Activity extends BaseActivity implements View.OnClickListener {
    private String sn;
    private TextView customerName_Tv,code_Tv,search_Tv;
    private String shiptoCode="";
    private String taskNo="";

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
        customerName_Tv =   findMyViewById(R.id.customerName_Tv);
        code_Tv =   findMyViewById(R.id.code_Tv);
        search_Tv =   findMyViewById(R.id.search_Tv);
    }

    @Override
    protected void initEvent() {
        customerName_Tv.setOnClickListener(this);
        code_Tv.setOnClickListener(this);
        search_Tv.setOnClickListener(this);
    }

    @Override
    protected void getServerData() {
        post(mActivity,xUtils3Http.BASE_URL+xUtils3Http.Customer);

    }

    /**
     * 客户名称
     *
     * @param context
     * @param baseUrl
     */
    CustomerName_Bean customerName_bean;
    public void post(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("shiptoCode","");
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                 customerName_bean = GsonUtil.getInstance().json2Bean(result, CustomerName_Bean.class);
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
        map.put("shiptoCode",shiptoCode);
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
        map.put("shiptoCode",shiptoCode);
        map.put("taskNo",taskNo);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                EarlyWarning_Detail_Bean earlyWarningDetailBean = GsonUtil.getInstance().json2Bean(result, EarlyWarning_Detail_Bean.class);
                EarlyWarning_Detail_Activity.startActivity(context,earlyWarningDetailBean);
            }
            @Override
            public void failed(String... args) {

            }
        });
    }

    /** 客户名称
     * @param textView
     */
    private void setName(TextView textView) {
        if (customerName_bean == null) {
            return;
        }
         List<String> mOptionsItems = new ArrayList<>();
        for (CustomerName_Bean.DataDTO dataDTO : customerName_bean.getData()) {
            mOptionsItems.add(dataDTO.getShiptoName());
        }
        OptionsPickerView pvOptions = new OptionsPickerBuilder(mActivity, (options1, option2, options3, v) -> {
            code_Tv.setText("");
            String s = mOptionsItems.get(options1);
            textView.setText(s);
            shiptoCode = customerName_bean.getData().get(options1).getShiptoCode();
            postCode(mActivity,xUtils3Http.BASE_URL+xUtils3Http.CustomerAlarm);

        }).build();
        pvOptions.setPicker(mOptionsItems);
        pvOptions.show();
    }

    //业务编码
    private void setCode(TextView textView) {
        if (customerAlarm_bean == null){
            return;
        }
        if (ListUtils.isEmpty(customerAlarm_bean.getData())){
            T.s("没有业务",2000);
            return;
        }
        final List<String> mOptionsItems = new ArrayList<>();
        mOptionsItems.addAll(customerAlarm_bean.getData());
        OptionsPickerView pvOptions = new OptionsPickerBuilder(mActivity, (options1, option2, options3, v) -> {
             String code = mOptionsItems.get(options1);
            textView.setText(code);
            taskNo = code;
        }).build();
        pvOptions.setPicker(mOptionsItems);
        pvOptions.show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.customerName_Tv:
                setName(customerName_Tv);
                break;
            case R.id.code_Tv:
               setCode(code_Tv);
                break;
            case R.id.search_Tv:
                if (TextUtils.isEmpty(customerName_Tv.getText())){
                    T.s("请选择客户名称",3000);
                    return;
                }

                if (TextUtils.isEmpty(code_Tv.getText())){
                    T.s("请选择业务",3000);
                    return;
                }
                postReport(mActivity,xUtils3Http.BASE_URL+xUtils3Http.Tickets);
                break;
        }
    }


}