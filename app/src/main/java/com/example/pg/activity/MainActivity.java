package com.example.pg.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.pg.R;
import com.example.pg.baseview.BaseActivity;
import com.example.pg.baseview.FileUploadTask;
import com.example.pg.baseview.GlideEnGine;
import com.example.pg.baseview.LocationUtils;
import com.example.pg.baseview.NetWorkUtils;
import com.example.pg.baseview.TwoButtonDialogBlue;
import com.example.pg.bean.Maken_Bean;
import com.example.pg.bean.User_Bean;
import com.example.pg.common.utils.GsonUtil;
import com.example.pg.common.utils.L;
import com.example.pg.common.utils.ListUtils;
import com.example.pg.common.utils.T;
import com.example.pg.common.utils.xUtils3Http;
import com.gyf.barlibrary.ImmersionBar;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.tencent.mmkv.MMKV;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private long clickTime;
    private ConstraintLayout maken_Con;
    private TextView QR_Tv, earlyWarning_Tv, Statistics_Tv;
    private String province = "", city = "", address = "";
    private String ip = "";
    private String code = "";
    private String name = "";
    private String dept_name = "";

    @Override
    protected void setStatusBar() {
        setBar();
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
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        QR_Tv = findMyViewById(R.id.QR_Tv);
        earlyWarning_Tv = findMyViewById(R.id.earlyWarning_Tv);
        Statistics_Tv = findMyViewById(R.id.Statistics_Tv);
        maken_Con = findMyViewById(R.id.maken_Con);
        getIP();
//        getLocation();
        startLocation();
    }

    @Override
    protected void getServerData() {
        String s = xUtils3Http.SSO_BASE_URL + xUtils3Http.User_By;
        getV3Login(this, s);
    }

    @Override
    protected void initEvent() {
        QR_Tv.setOnClickListener(this);
        earlyWarning_Tv.setOnClickListener(this);
        Statistics_Tv.setOnClickListener(this);
        maken_Con.setOnClickListener(this);
    }

    /**
     * 获取用户信息
     *
     * @param context
     * @param baseUrl
     */
    User_Bean login_bean;

    public void getV3Login(Context context, String baseUrl) {
        MMKV mmkv = MMKV.defaultMMKV();
        String data = mmkv.decodeString(xUtils3Http.Data);
        baseUrl = baseUrl + data;
        Map<String, Object> map = new HashMap<>();
//        map.put("code", data);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                login_bean = GsonUtil.getInstance().json2Bean(result, User_Bean.class);
                mmkv.encode(xUtils3Http.UserData, result);
                if (login_bean.getData() != null) {

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
                    maken_Con.setVisibility(View.VISIBLE);
                }

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
        switch (v.getId()) {
            case R.id.QR_Tv:
                startCamera(mActivity);
                break;
            case R.id.earlyWarning_Tv:
                skipAnotherActivity(EarlyWarning_Activity.class);
                break;
            case R.id.Statistics_Tv:
                skipAnotherActivity(DcReport_Activity.class);
                break;
            case R.id.maken_Con:
                startMakenCamera();
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

    /**
     * 打开马肯码
     */
    public void startMakenCamera() {
        //监听授权
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.CAMERA);
        }

        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[0]);
            ActivityCompat.requestPermissions(mActivity, permissions, 1);
        } else {
            //打开相机录制视频
            Intent captureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            //判断相机是否正常。
            if (captureIntent.resolveActivity(mActivity.getPackageManager()) != null) {
                setPhotoMetod(mActivity);
            }

        }
    }

    private void setPhotoMetod(Context context) {
        int choice = 1;
        PictureSelector.create((Activity) context)
                .openGallery(PictureConfig.TYPE_IMAGE)
                .imageEngine(GlideEnGine.createGlideEngine()) //图片加载空白 加入Glide加载图片
                .imageSpanCount(4)// 每行显示个数 int
                .maxSelectNum(choice)
                .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
                .isSingleDirectReturn(true)//PictureConfig.SINGLE模式下是否直接返回
                .isAndroidQTransform(true)//Android Q版本下是否需要拷贝文件至应用沙盒内
                .isPreviewImage(true)// 是否可预览图片 true or false
                .isCamera(true)// 是否显示拍照按钮 true or false
                .isEnableCrop(true)//开启裁剪
                .cropImageWideHigh(200, 200)//裁剪尺寸
                .withAspectRatio(1, 1)//裁剪比例1：1是正方形
                .freeStyleCropEnabled(true)//裁剪框是否可拖拽
//                .bindCustomCameraInterfaceListener((context1, config, type) -> {//检测拍照按钮
//                    ptype = "1";
//                    Intent intent = new Intent(context1, PictureSelectorCameraEmptyActivity.class);
//                    intent.putExtra(PictureConfig.CAMERA_FACING, PictureConfig.CAMERA_BEFORE);
//                    startActivityForResult(intent, PictureConfig.REQUEST_CAMERA);
//                })
//               .isCameraRotateImage(true)// 拍照是否纠正旋转图片
//                .imageFormat(PictureMimeType.PNG_Q)//拍照图片格式后缀,默认jpeg, PictureMimeType.PNG，Android Q使用PictureMimeType.PNG_Q
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .isCompress(true)// 是否压缩 true or false
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }


    String ptype = "";
    private List<String> photoPaths;
    String androidToPath = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
//                case PictureConfig.REQUEST_CAMERA:
//                    selectList = PictureSelector.obtainMultipleResult(data);
//                    GlideUtils.loadImage(mActivity, maken_Img, selectList.get(0).getAndroidQToPath());
//                    break;
                case PictureConfig.CHOOSE_REQUEST:
                    // 结果回调
                    photoPaths = new ArrayList<>();
                    List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                    if (!ListUtils.isEmpty(selectList)) {

                        if (isQ()) {
                            androidToPath = selectList.get(0).getAndroidQToPath();
                        } else {
                            androidToPath = selectList.get(0).getRealPath();
                        }
//                        GlideUtils.loadImage(mActivity, maken_Img, androidToPath);
                        photoPaths.add(androidToPath);

                        showCancelRoleDialog();
                    }
                    break;
            }
        }
    }

    private boolean isQ() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true;
        }
        return false;
    }


    private void showCancelRoleDialog() {
        TwoButtonDialogBlue mDialog = new TwoButtonDialogBlue(this, "照片请勿含有个人隐私信息", "取消", "继续", new TwoButtonDialogBlue.ConfirmListener() {
            @Override
            public void onClickRight() {
                uploadPhoto();
            }

            @Override
            public void onClickLeft() {

            }
        });
        mDialog.show();
    }

    private void uploadPhoto() {
        if (ListUtils.isEmpty(photoPaths)) {
            T.s("请选择图片", 3000);
            return;
        }
        try {
            if (!ListUtils.isEmpty(photoPaths)) {
                //上传
                File saveFile = new File(photoPaths.get(0));
                FileUploadTask fileUploadTask = new FileUploadTask(mActivity, saveFile, new FileUploadTask.UploadCallback() {
                    @Override
                    public void success(String result) {
//                        getInfo(result);

                        postMaken(mActivity, xUtils3Http.QA_Marken_URL, result);
                    }

                    @Override
                    public void failed(String... args) {

                    }
                });
                fileUploadTask.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传分析
     *
     * @param context
     * @param baseUrl
     */
    public void postMaken(Context context, String baseUrl, String blobImgUrl) {
        if (login_bean != null) {
            code = login_bean.getData().getCode();
            name = login_bean.getData().getName();
            dept_name = login_bean.getData().getDeptName();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("province", province);
        map.put("city", city);
        map.put("address", address);
        map.put("ip", ip);
        map.put("code", code);
        map.put("name", name);
        map.put("dept_name", dept_name);
        map.put("image", blobImgUrl);
        map.put("image_source", "2");
        map.put("scan_date", getTime());
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Maken_Bean maken_bean = GsonUtil.getInstance().json2Bean(result, Maken_Bean.class);
                if (maken_bean != null) {
                    String data = maken_bean.getData();
                    MarkenCode_Activity.startActivity(context,data);
                }
            }

            @Override
            public void failed(String... args) {

            }
        });
    }

    private void getIP() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);//获取系统的连接服务
        NetworkInfo mActiveNetInfo = mConnectivityManager.getActiveNetworkInfo();//获取网络连接的信息
        if (mActiveNetInfo == null) {
            NetWorkUtils.myDialog(this);
        } else {
            String s = NetWorkUtils.setUpInfo(mActiveNetInfo, mConnectivityManager);
            ip = s;
            L.d("WIFI", s);
        }
    }


    private void getLocation() {
        Location location = LocationUtils.getInstance(this).showLocation();
        if (location != null) {
            String address = "纬度：" + location.getLatitude() + "经度：" + location.getLongitude();
            Log.d("TAG", address);
//            text.setText( address );
        } else {
            Log.d("TAG", "没有地址");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocationUtils.getInstance(this).removeLocationUpdatesListener();
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }


    public void startLocation() {
        //监听授权
        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {//摄像与录制
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!permissionList.isEmpty()) {
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this, permissions, 1);
        } else {
            Location();
        }
    }

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    private AMapLocation MapLocation;

    private void Location() {

//        updatePrivacyShow、updatePrivacyAgree
        try {
            //初始化定位
            mLocationClient = new AMapLocationClient(getApplicationContext());
            //设置定位回调监听
            mLocationClient.setLocationListener(mLocationListener);
            //初始化AMapLocationClientOption对象
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //获取一次定位结果：
            //该方法默认为false。
            mLocationOption.setOnceLocation(true);
            //获取最近3s内精度最高的一次定位结果：
            //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
            mLocationOption.setOnceLocationLatest(true);
            //给定位客户端对象设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            //启动定位
            mLocationClient.startLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        //声明定位回调监听器
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //可在其中解析amapLocation获取相应内容。
                    MapLocation = aMapLocation;
                    province = aMapLocation.getProvince();
                    city = aMapLocation.getCity();
                    address = aMapLocation.getAddress();
                    L.d("PG", aMapLocation.getAddress());
                } else {
                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };


    private String getTime() {
        String rel = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        rel = formatter.format(curDate);
        return rel;
    }


}