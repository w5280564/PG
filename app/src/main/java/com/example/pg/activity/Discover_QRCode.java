package com.example.pg.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pg.R;
import com.example.pg.baseview.BaseActivity;
import com.gyf.barlibrary.ImmersionBar;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class Discover_QRCode extends BaseActivity implements QRCodeView.Delegate {
    private ZXingView mZXingView;
    private View return_Btn;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, Discover_QRCode.class);
        context.startActivity(intent);
    }

    /**
     * 设置状态栏
     */
    private void setBar() {
        ImmersionBar.with(this)
                .fitsSystemWindows(false)
                .statusBarDarkFont(true)
                .init();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_discover_qrcode;
    }


    @Override
    protected void setStatusBar() {
        setBar();
    }

    @Override
    protected void initView() {
        return_Btn = findViewById(R.id.return_Btn);
        return_Btn.setOnClickListener(new return_BtnClick());
        mZXingView = findViewById(R.id.zxingview);
        mZXingView.hiddenScanRect();
        mZXingView.setDelegate(this);
//        select_Picture_Img = findViewById(R.id.select_Picture_Img);
//        select_Picture_Img.setOnClickListener(new select_Picture_ImgClick());
    }

    @Override
    protected void onStart() {
        super.onStart();
        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
//        mZXingView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT); // 打开前置摄像头开始预览，但是并未开始识别
        mZXingView.startSpotAndShowRect(); // 显示扫描框，并开始识别
//        mZXingView.startSpot(); // 开始识别
    }

    @Override
    protected void onStop() {
        if (mZXingView != null) {
            mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (mZXingView != null) {
            mZXingView.onDestroy(); // 销毁二维码扫描控件
        }
        super.onDestroy();
    }


    @Override
    public void onScanQRCodeSuccess(String result) {
        if (TextUtils.isEmpty(result)) {
            Toast.makeText(mContext, "无法识别", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!TextUtils.isEmpty(result)) {
            QRDetail_Activity.startActivity(mActivity, result);
            finish();
        }
        vibrate();

//        mZXingView.startSpot(); // 开始识别
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(500);
    }


    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {

    }

    @Override
    public void onScanQRCodeOpenCameraError() {

    }


    private class return_BtnClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }


}

