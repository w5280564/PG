package com.example.pg.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.pg.R;
import com.example.pg.baseview.BaseActivity;
import com.example.pg.bean.User_Bean;
import com.example.pg.common.utils.GsonUtil;
import com.example.pg.common.utils.T;
import com.example.pg.common.utils.xUtils3Http;
import com.tencent.mmkv.MMKV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private long clickTime;
    private TextView QR_Tv, earlyWarning_Tv, Statistics_Tv, make_Tv;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        QR_Tv = findMyViewById(R.id.QR_Tv);
        earlyWarning_Tv = findMyViewById(R.id.earlyWarning_Tv);
        Statistics_Tv = findMyViewById(R.id.Statistics_Tv);
        make_Tv = findMyViewById(R.id.make_Tv);

    }

    @Override
    protected void getServerData() {
        String s = xUtils3Http.SSO_BASE_URL + xUtils3Http.User_By;
        getV3Login(this,s);
    }

    @Override
    protected void initEvent() {
        QR_Tv.setOnClickListener(this);
        earlyWarning_Tv.setOnClickListener(this);
        Statistics_Tv.setOnClickListener(this);
    }

    /**
     * 获取用户信息
     *
     * @param context
     * @param baseUrl
     */
    public void getV3Login(Context context, String baseUrl) {
        MMKV mmkv = MMKV.defaultMMKV();
        String data = mmkv.decodeString(xUtils3Http.Data);
        baseUrl = baseUrl + data;
        Map<String, Object> map = new HashMap<>();
//        map.put("code", data);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                User_Bean login_bean = GsonUtil.getInstance().json2Bean(result, User_Bean.class);
                mmkv.encode(xUtils3Http.UserData, result);

                List<String> codeList = new ArrayList<>();
                for (User_Bean.DataDTO.MenuListDTO menuListDTO : login_bean.getData().getMenuList()) {
                    codeList.add(menuListDTO.getCode());
                }
                if (codeList.contains("440001")) {
                    QR_Tv.setVisibility(View.VISIBLE);
                }
                if (codeList.contains("440002")) {
                    earlyWarning_Tv.setVisibility(View.VISIBLE);
                }
                if (codeList.contains("440003")) {
                    Statistics_Tv.setVisibility(View.VISIBLE);
                }
                if (codeList.contains("440004")) {
                    make_Tv.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void failed(String... args) {
            }
        });
    }


    @Override
    public void onBackPressed() {
        if ((System.currentTimeMillis() - clickTime) > 2000) {
            T.ss(getString(R.string.Home_press_again));
            clickTime = System.currentTimeMillis();
        } else {
            closeApp();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.QR_Tv:
                startCamera(mActivity);
                break;
                case R.id.earlyWarning_Tv:
                    skipAnotherActivity(EarlyWarning_Activity.class);
                break;
                case R.id.Statistics_Tv:
                    skipAnotherActivity(DcReport_Activity.class);
                break;

        }
    }


    public void startCamera(Activity context) {
        //监听授权
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(context, permissions, 1);
        } else {
            //打开相机录制视频
            Intent captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            //判断相机是否正常。
//            if (captureIntent.resolveActivity(context.getPackageManager()) != null) {
                Discover_QRCode.actionStart(context);
//            }

        }
    }
}