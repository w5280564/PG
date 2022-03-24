package com.example.pg.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.Observer;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.pg.R;
import com.example.pg.baseview.BaseActivity;
import com.example.pg.baseview.FileUploadTask;
import com.example.pg.baseview.GlideEnGine;
import com.example.pg.baseview.ImageCropEngine;
import com.example.pg.baseview.LocationUtils;
import com.example.pg.baseview.NetWorkUtils;
import com.example.pg.baseview.TwoButtonDialogBlue;
import com.example.pg.baseview.livedatas.LiveDataBus;
import com.example.pg.baseview.livedatas.MyConstant;
import com.example.pg.bean.Maken_Bean;
import com.example.pg.bean.User_Bean;
import com.example.pg.common.utils.GsonUtil;
import com.example.pg.common.utils.L;
import com.example.pg.common.utils.ListUtils;
import com.example.pg.common.utils.T;
import com.example.pg.common.utils.xUtils3Http;
import com.gyf.barlibrary.ImmersionBar;
import com.luck.picture.lib.basic.PictureSelector;
import com.luck.picture.lib.config.SelectMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.interfaces.OnResultCallbackListener;
import com.permissionx.guolindev.PermissionX;
import com.tencent.mmkv.MMKV;
import com.wld.mycamerax.util.CameraConstant;
import com.wld.mycamerax.util.CameraParam;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bingoogolapple.transformerstip.TransformersTip;
import cn.bingoogolapple.transformerstip.gravity.TipGravity;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private long clickTime;
    private ConstraintLayout maken_Con;
    private TextView QR_Tv, earlyWarning_Tv, Statistics_Tv;
    private String province = "", city = "", address = "";
    private String ip = "";
    private String code = "";
    private String name = "";
    private String dept_name = "";
    private String image_source = "";

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

        LiveDataBus.get().with(MyConstant.Again_TakePicture, boolean.class).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    cameraXMethod();
                }
            }
        });
        LiveDataBus.get().with(MyConstant.Choose_Photo, boolean.class).observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    setPhotoMetod(mActivity);
                }
            }
        });

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
        PermissionX.init(this)
                .permissions( Manifest.permission.CAMERA)
                .request((boolean allGranted, List<String> grantedList, List<String> deniedList) -> {
                    if (allGranted) {
                        Discover_QRCode.actionStart(context);
                    } else {
                        Toast.makeText(getApplicationContext(), "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show();
                    }
                });
    }



    /**
     * 打开马肯码
     */
    public void startMakenCamera() {
        PermissionX.init(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .request((boolean allGranted, List<String> grantedList, List<String> deniedList) -> {
                    if (allGranted) {
                        startLocation();
                        showPhotoPopWindow();
                    } else {
                        Toast.makeText(getApplicationContext(), "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * cameraX拍照获取照片
     */
    private void cameraXMethod() {
        PermissionX.init(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .request((boolean allGranted, List<String> grantedList, List<String> deniedList) -> {
                    if (allGranted) {
                        CameraParam mCameraParam = new CameraParam.Builder()
                                .setShowFocusTips(false)
                                .setActivity(MainActivity.this)
                                .build();

                    } else {
                        Toast.makeText(getApplicationContext(), "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show();
                    }
                });
    }


    /**
     * 相册选择图片
     *
     * @param context
     */
    private void setPhotoMetod(Context context) {
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEnGine.createGlideEngine())
                .setSelectionMode(1)//单选或多选
                .isDirectReturnSingle(true)
                .setCropEngine(getCropEngine())//自定义裁剪
                .setImageSpanCount(4) //相册列表每行显示个数
                .isDisplayCamera(false) //是否显示相机入口
//                .isPreviewImage(false)//图片预览 单选模式下不需要
                .forResult(new OnResultCallbackListener<LocalMedia>() {
                    @Override
                    public void onResult(ArrayList<LocalMedia> result) {
                        image_source = "2";
                        addPhoto(result);
                    }

                    @Override
                    public void onCancel() {

                    }
                });

    }

    /**
     * 裁剪引擎
     *
     * @return
     */
    private ImageCropEngine getCropEngine() {
        return new ImageCropEngine();
    }


    private List<String> photoPaths;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CameraConstant.REQUEST_CODE:
                    //获取图片路径
                    String picturePath = data.getStringExtra(CameraConstant.PICTURE_PATH_KEY);
                    //显示出来
                    image_source = "1";
                    addPhoto(picturePath);
                    break;

            }
        }
    }

    /**
     * 获取相册返回照片
     */
    private void addPhoto(List<LocalMedia> selectList) {
        photoPaths = new ArrayList<>();
        String androidToPath = "";
        if (!ListUtils.isEmpty(selectList)) {
            LocalMedia localMedia = selectList.get(0);
            if (isQ()) {
                androidToPath = localMedia.getCutPath();
            } else {
                androidToPath = localMedia.getRealPath();
            }
            photoPaths.add(androidToPath);
            showCancelRoleDialog();
        }
    }

    /**
     * 获取拍照返回照片路径
     * @param pic
     */
    private void addPhoto(String pic) {
        photoPaths = new ArrayList<>();
        photoPaths.add(pic);
//        showCancelRoleDialog();去掉隐私提醒框
        uploadPhoto();
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
            if (getFileSize(photoPaths.get(0))) {
                T.s("图片超过10M无法识别", 3000);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (!ListUtils.isEmpty(photoPaths)) {
                //上传
                File saveFile = new File(photoPaths.get(0));
                FileUploadTask fileUploadTask = new FileUploadTask(mActivity, saveFile, new FileUploadTask.UploadCallback() {
                    @Override
                    public void success(String result) {
//                        getInfo(result);
                        postMaken(mActivity, xUtils3Http.PRD_Marken_URL, result);
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
        if (mapLocation != null) {
            province = mapLocation.getProvince();
            city = mapLocation.getCity();
            address = mapLocation.getAddress();
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
        map.put("image_source", image_source);
        map.put("scan_date", getTime());
//        String toast = "code=" + code + "name=" + name + "dept_name=" + dept_name
//                + "province=" + province + "city=" + city + "address=" + address
//                + "ip=" + ip + "image=" + blobImgUrl + "image_source=" + image_source + "scan_date=" + getTime();
//        T.s(toast, 5000);
//        L.d("PG",toast);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Maken_Bean maken_bean = GsonUtil.getInstance().json2Bean(result, Maken_Bean.class);
                if (maken_bean != null) {
                    String data = maken_bean.getData();
                    MarkenCode_Activity.startActivity(context, data, blobImgUrl, image_source);
//                    if (TextUtils.equals(image_source, "1")) {
//                    } else {
//                        MarkenCode_Activity.startActivity(context, data);
//                    }
                }
            }

            @Override
            public void failed(String... args) {

            }
        });
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
//        LocationUtils.getInstance(this).removeLocationUpdatesListener();
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
    }


    public void startLocation() {
        PermissionX.init(this)
                .permissions( Manifest.permission.ACCESS_FINE_LOCATION)
                .request((boolean allGranted, List<String> grantedList, List<String> deniedList) -> {
                    if (allGranted) {
                        Location();
                    } else {
                        Toast.makeText(getApplicationContext(), "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show();
                    }
                });
    }

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    private void Location() {
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

    private AMapLocation mapLocation;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = aMapLocation -> {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //可在其中解析amapLocation获取相应内容。
                mapLocation = aMapLocation;
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
    };

    // 上传图选择弹窗
    private void showPhotoPopWindow() {
        View anchorView = findViewById(R.id.photo_Tv);
        TransformersTip photoPop = new TransformersTip(anchorView, R.layout.up_photo_tip) {
            @Override
            protected void initView(View contentView) {
                TextView camera_Tv = contentView.findViewById(R.id.camera_Tv);
                TextView photo_Tv = contentView.findViewById(R.id.photo_Tv);
                TextView cancel_Tv = contentView.findViewById(R.id.cancel_Tv);
                camera_Tv.setOnClickListener(v -> {
                    cameraXMethod();
                    dismissTip();
                });
                photo_Tv.setOnClickListener(v -> {
                    setPhotoMetod(mActivity);
                    dismissTip();
                });
                cancel_Tv.setOnClickListener(v -> {
                    dismissTip();
                });

            }
        }
                .setTipGravity(TipGravity.TO_BOTTOM_CENTER) // 设置浮窗相对于锚点控件展示的位置
                .setTipOffsetXDp(0) // 设置浮窗在 x 轴的偏移量
                .setTipOffsetYDp(6) // 设置浮窗在 y 轴的偏移量
                .setBackgroundDimEnabled(true) // 设置是否允许浮窗的背景变暗
                .setDismissOnTouchOutside(true) // 设置点击浮窗外部时是否自动关闭浮窗
                .show(); // 显示浮窗
    }


    private String getTime() {
        String rel = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        rel = formatter.format(curDate);
        return rel;
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

    /**
     * 判断文件是否大于10M
     *
     * @param fileName
     * @return
     * @throws Exception
     */
    public static boolean getFileSize(String fileName) throws Exception {
        File file = new File(fileName);
        double cacheSize = 0;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            if (file.exists()) {
                FileInputStream fis = new FileInputStream(file);
                cacheSize = fis.available();//获取字节
            }

        }
        double fileM = cacheSize / 1024 / 1024;//计算多少M
        if (fileM > 10) {
            return true;
        }
        return false;
    }


}