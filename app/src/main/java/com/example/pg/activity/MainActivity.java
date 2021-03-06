package com.example.pg.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
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
import com.example.pg.baseview.DemoUtils;
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
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
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
import java.util.regex.Pattern;

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
     * ???????????????
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

        // ??????????????????
        TencentLocationManager.setUserAgreePrivacy(true);
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
//        String s = xUtils3Http.SSO_BASE_URL + xUtils3Http.User_By;
//        getV3Login(this, s);
    }

    @Override
    protected void initEvent() {
        QR_Tv.setOnClickListener(this);
        earlyWarning_Tv.setOnClickListener(this);
        Statistics_Tv.setOnClickListener(this);
        maken_Con.setOnClickListener(this);
    }

    /**
     * ??????????????????
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
     * ???????????????
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
     * cameraX??????????????????
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
     * ??????????????????
     *
     * @param context
     */
    private void setPhotoMetod(Context context) {
        PictureSelector.create(this)
                .openGallery(SelectMimeType.ofImage())
                .setImageEngine(GlideEnGine.createGlideEngine())
                .setSelectionMode(1)//???????????????
                .isDirectReturnSingle(true)
                .setCropEngine(getCropEngine())//???????????????
                .setImageSpanCount(4) //??????????????????????????????
                .isDisplayCamera(false) //????????????????????????
//                .isPreviewImage(false)//???????????? ????????????????????????
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
     * ????????????
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
                    //??????????????????
                    String picturePath = data.getStringExtra(CameraConstant.PICTURE_PATH_KEY);
                    //????????????
                    image_source = "1";
                    addPhoto(picturePath);
                    break;

            }
        }
    }

    /**
     * ????????????????????????
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
     * ??????????????????????????????
     * @param pic
     */
    private void addPhoto(String pic) {
        photoPaths = new ArrayList<>();
        photoPaths.add(pic);
//        showCancelRoleDialog();?????????????????????
        uploadPhoto();
    }


    private boolean isQ() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true;
        }
        return false;
    }


    private void showCancelRoleDialog() {
        TwoButtonDialogBlue mDialog = new TwoButtonDialogBlue(this, "????????????????????????????????????", "??????", "??????", new TwoButtonDialogBlue.ConfirmListener() {
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
            T.s("???????????????", 3000);
            return;
        }
        try {
            if (getFileSize(photoPaths.get(0))) {
                T.s("????????????10M????????????", 3000);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (!ListUtils.isEmpty(photoPaths)) {
                //??????
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
     * ????????????
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
            String address = "?????????" + location.getLatitude() + "?????????" + location.getLongitude();
            Log.d("TAG", address);
//            text.setText( address );
        } else {
            Log.d("TAG", "????????????");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        LocationUtils.getInstance(this).removeLocationUpdatesListener();
        mLocationClient.onDestroy();//?????????????????????????????????????????????????????????
        stopLocation();
    }


    public void startLocation() {
        PermissionX.init(this)
                .permissions( Manifest.permission.ACCESS_FINE_LOCATION)
                .request((boolean allGranted, List<String> grantedList, List<String> deniedList) -> {
                    if (allGranted) {
//                        Location();
                        startTenCentLocation();
                    } else {
                        Toast.makeText(getApplicationContext(), "These permissions are denied: $deniedList", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private TencentLocationManager mLocationManager;
    // "??????"??????
    public void startTenCentLocation() {
        mLocationManager = TencentLocationManager.getInstance(this);
//		mLocationManager.setDeviceID(this, "7E35C989E01E1E48A8D9059C355F7C4A");
        // ?????????????????? gcj-02, ??????????????? gcj-02, ????????????????????????????????????
        mLocationManager.setCoordinateType(TencentLocationManager.COORDINATE_TYPE_GCJ02);
        // ??????????????????
        TencentLocationRequest request = TencentLocationRequest.create()
                .setInterval(5*1000) // ??????????????????
                .setAllowGPS(true)  //??????false?????????????????????GPS???????????????
//                .setQQ("10001")
                .setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA); // ????????????level

        // ????????????
        mLocationManager.requestLocationUpdates(request, new LocationChanged(),getMainLooper());

        updateLocationStatus("????????????: " + request + ", ?????????=" + DemoUtils.toString(mLocationManager.getCoordinateType()));
    }

    private class LocationChanged implements TencentLocationListener {
        @Override
        public void onLocationChanged(TencentLocation tencentLocation, int error, String reason) {
            String msg = null;
            if (error == TencentLocation.ERROR_OK) {
                // ????????????
//                msg = toString(tencentLocation, TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA);
                StringBuilder sb = new StringBuilder();
                sb.append("latitude=").append(tencentLocation.getLatitude()).append(",");
                sb.append("longitude=").append(tencentLocation.getLongitude()).append(",");
                sb.append("altitude=").append(tencentLocation.getAltitude()).append(",");
                sb.append("accuracy=").append(tencentLocation.getAccuracy()).append(",");
                sb.append("name=").append(tencentLocation.getName()).append(",");
                sb.append("address=").append(tencentLocation.getAddress()).append(",");

                sb.append("nation=").append(tencentLocation.getNation()).append(",");
                sb.append("province=").append(tencentLocation.getProvince()).append(",");
                sb.append("city=").append(tencentLocation.getCity()).append(",");
                sb.append("district=").append(tencentLocation.getDistrict()).append(",");
                sb.append("town=").append(tencentLocation.getTown()).append(",");
                sb.append("village=").append(tencentLocation.getVillage()).append(",");
                sb.append("street=").append(tencentLocation.getStreet()).append(",");
                sb.append("streetNo=").append(tencentLocation.getStreetNo()).append(",");
                sb.append("citycode=").append(tencentLocation.getCityCode()).append(",");


                province = tencentLocation.getProvince();
                city = tencentLocation.getCity();
                address = tencentLocation.getAddress();

                L.d("location",sb.toString());
                msg = sb.toString();
            } else {
                // ????????????
                msg = "????????????: " + reason;
                L.d("location",msg);
            }
            updateLocationStatus(msg);
        }

        @Override
        public void onStatusUpdate(String s, int i, String s1) {

        }
    }

    private void updateLocationStatus(String message) {
        L.d("location",message);
//        mLocationStatus.append(message);
//        mLocationStatus.append("\n---\n");
    }

    // ????????????"??????"
    public void stopLocation() {
        mLocationManager.removeUpdates(new LocationChanged());
        updateLocationStatus("????????????");
    }



    //??????AMapLocationClientOption??????
    public AMapLocationClientOption mLocationOption = null;
    //??????AMapLocationClient?????????
    public AMapLocationClient mLocationClient = null;

    private void Location() {
        try {
            //???????????????
            mLocationClient = new AMapLocationClient(getApplicationContext());
            //????????????????????????
            mLocationClient.setLocationListener(mLocationListener);
            //?????????AMapLocationClientOption??????
            mLocationOption = new AMapLocationClientOption();
            //?????????????????????AMapLocationMode.Hight_Accuracy?????????????????????
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //???????????????????????????
            //??????????????????false???
            mLocationOption.setOnceLocation(true);
            //????????????3s???????????????????????????????????????
            //??????setOnceLocationLatest(boolean b)?????????true??????????????????SDK???????????????3s?????????????????????????????????????????????????????????true???setOnceLocation(boolean b)????????????????????????true???????????????????????????false???
            mLocationOption.setOnceLocationLatest(true);
            //??????????????????????????????????????????
            mLocationClient.setLocationOption(mLocationOption);
            //????????????
            mLocationClient.startLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private AMapLocation mapLocation;
    //???????????????????????????
    public AMapLocationListener mLocationListener = aMapLocation -> {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //??????????????????amapLocation?????????????????????
                mapLocation = aMapLocation;
                province = aMapLocation.getProvince();
                city = aMapLocation.getCity();
                address = aMapLocation.getAddress();
                L.d("PG", aMapLocation.getAddress());
            } else {
                //???????????????????????????ErrCode????????????????????????????????????????????????errInfo???????????????????????????????????????
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    };

    // ?????????????????????
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
                .setTipGravity(TipGravity.TO_BOTTOM_CENTER) // ????????????????????????????????????????????????
                .setTipOffsetXDp(0) // ??????????????? x ???????????????
                .setTipOffsetYDp(6) // ??????????????? y ???????????????
                .setBackgroundDimEnabled(true) // ???????????????????????????????????????
                .setDismissOnTouchOutside(true) // ???????????????????????????????????????????????????
                .show(); // ????????????
    }


    private String getTime() {
        String rel = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        rel = formatter.format(curDate);
        return rel;
    }

    private void getIP() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);//???????????????????????????
        NetworkInfo mActiveNetInfo = mConnectivityManager.getActiveNetworkInfo();//???????????????????????????
        if (mActiveNetInfo == null) {
            NetWorkUtils.myDialog(this);
        } else {
            String s = NetWorkUtils.setUpInfo(mActiveNetInfo, mConnectivityManager);
            ip = s;
            L.d("WIFI", s);
        }
    }

    /**
     * ????????????????????????10M
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
                cacheSize = fis.available();//????????????
            }

        }
        double fileM = cacheSize / 1024 / 1024;//????????????M
        if (fileM > 10) {
            return true;
        }
        return false;
    }


}