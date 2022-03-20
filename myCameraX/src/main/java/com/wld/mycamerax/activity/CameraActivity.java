package com.wld.mycamerax.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.FocusMeteringResult;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.MeteringPoint;
import androidx.camera.core.MeteringPointFactory;
import androidx.camera.core.Preview;
import androidx.camera.core.SurfaceOrientedMeteringPointFactory;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Options;
import com.google.common.util.concurrent.ListenableFuture;
import com.wld.mycamerax.R;
import com.wld.mycamerax.util.CameraConstant;
import com.wld.mycamerax.util.CameraParam;
import com.wld.mycamerax.util.FocusView;
import com.wld.mycamerax.util.NoPermissionException;
import com.wld.mycamerax.util.Tools;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;

public class CameraActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ImageView img_switch;
    private LinearLayout ll_picture_parent;
    private ImageView img_picture;
    private FocusView focus_view;
    private View view_mask;
    private ConstraintLayout rl_result_picture;
    private TextView img_picture_cancel;
    private TextView img_picture_save;
    private RelativeLayout rl_start;
    private TextView tv_back;
    private ImageView img_take_photo;

    private ImageCapture imageCapture;
    private CameraControl mCameraControl;
    private ProcessCameraProvider cameraProvider;
    private CameraParam mCameraParam;
    private boolean front;
    private Disposable mTimerDisposable;
    private View top_View, bottom_View;
    private TextView quit_TV;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_camera);
        mCameraParam = getIntent().getParcelableExtra(CameraConstant.CAMERA_PARAM_KEY);
        if (mCameraParam == null) {
            throw new IllegalArgumentException("CameraParam is null");
        }
        if (!Tools.checkPermission(this)) {
            throw new NoPermissionException("Need to have permission to take pictures and storage");
        }
        front = mCameraParam.isFront();
        initView();
        setViewParam();
        intCamera();
    }

    private void setViewParam() {
        //是否显示切换按钮
        if (mCameraParam.isShowSwitch()) {
            img_switch.setVisibility(View.VISIBLE);
            if (mCameraParam.getSwitchSize() != -1 || mCameraParam.getSwitchLeft() != -1 || mCameraParam.getSwitchTop() != -1) {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) img_switch.getLayoutParams();
                if (mCameraParam.getSwitchSize() != -1) {
                    layoutParams.width = layoutParams.height = mCameraParam.getSwitchSize();
                }
                if (mCameraParam.getSwitchLeft() != -1) {
                    layoutParams.leftMargin = mCameraParam.getSwitchLeft();
                }
                if (mCameraParam.getSwitchTop() != -1) {
                    layoutParams.topMargin = mCameraParam.getSwitchTop();
                }
                img_switch.setLayoutParams(layoutParams);
            }
            if (mCameraParam.getSwitchImgId() != -1) {
                img_switch.setImageResource(mCameraParam.getSwitchImgId());
            }
        } else {
            img_switch.setVisibility(View.GONE);
        }

        //是否显示裁剪框
        if (mCameraParam.isShowMask()) {
            view_mask.setVisibility(View.VISIBLE);
            if (mCameraParam.getMaskMarginLeftAndRight() != -1 || mCameraParam.getMaskMarginTop() != -1
                    || mCameraParam.getMaskRatioH() != -1) {
                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) view_mask.getLayoutParams();

                if (mCameraParam.getMaskMarginLeftAndRight() != -1) {
                    layoutParams.leftMargin = layoutParams.rightMargin = mCameraParam.getMaskMarginLeftAndRight();
                }

                if (mCameraParam.getMaskMarginTop() != -1) {
                    layoutParams.topMargin = mCameraParam.getMaskMarginTop();
                }

                if (mCameraParam.getMaskRatioH() != -1) {
                    Tools.reflectMaskRatio(view_mask, mCameraParam.getMaskRatioW(), mCameraParam.getMaskRatioH());
                }
                view_mask.setLayoutParams(layoutParams);
            }
            if (mCameraParam.getMaskImgId() != -1) {
                view_mask.setBackgroundResource(mCameraParam.getMaskImgId());
            }
        } else {
            view_mask.setVisibility(View.GONE);
        }

        if (mCameraParam.getBackText() != null) {
            tv_back.setText(mCameraParam.getBackText());
        }
        if (mCameraParam.getBackColor() != -1) {
            tv_back.setTextColor(mCameraParam.getBackColor());
        }
        if (mCameraParam.getBackSize() != -1) {
            tv_back.setTextSize(mCameraParam.getBackSize());
        }

        if (mCameraParam.getTakePhotoSize() != -1) {
            int size = mCameraParam.getTakePhotoSize();

            ViewGroup.LayoutParams pictureCancelParams = img_picture_cancel.getLayoutParams();
            pictureCancelParams.width = pictureCancelParams.height = size;
            img_picture_cancel.setLayoutParams(pictureCancelParams);

            ViewGroup.LayoutParams pictureSaveParams = img_picture_save.getLayoutParams();
            pictureSaveParams.width = pictureSaveParams.height = size;
            img_picture_save.setLayoutParams(pictureSaveParams);

            ViewGroup.LayoutParams takePhotoParams = img_take_photo.getLayoutParams();
            takePhotoParams.width = takePhotoParams.height = size;
            img_take_photo.setLayoutParams(takePhotoParams);
        }

        focus_view.setParam(mCameraParam.getFocusViewSize(), mCameraParam.getFocusViewColor(),
                mCameraParam.getFocusViewTime(), mCameraParam.getFocusViewStrokeSize(), mCameraParam.getCornerViewSize());


        if (mCameraParam.getCancelImgId() != -1) {
//            img_picture_cancel.setImageResource(mCameraParam.getCancelImgId());
        }
        if (mCameraParam.getSaveImgId() != -1) {
//            img_picture_save.setImageResource(mCameraParam.getSaveImgId());
        }
        if (mCameraParam.getTakePhotoImgId() != -1) {
            img_take_photo.setImageResource(mCameraParam.getTakePhotoImgId());
        }

        if (mCameraParam.getResultBottom() != -1) {
            ConstraintLayout.LayoutParams resultPictureParams = (ConstraintLayout.LayoutParams) rl_result_picture.getLayoutParams();
            resultPictureParams.bottomMargin = mCameraParam.getResultBottom();
            rl_result_picture.setLayoutParams(resultPictureParams);

            ConstraintLayout.LayoutParams startParams = (ConstraintLayout.LayoutParams) rl_start.getLayoutParams();
            startParams.bottomMargin = mCameraParam.getResultBottom();
            rl_start.setLayoutParams(startParams);
        }

        if (mCameraParam.getResultLeftAndRight() != -1) {
            RelativeLayout.LayoutParams pictureCancelParams = (RelativeLayout.LayoutParams) img_picture_cancel.getLayoutParams();
            pictureCancelParams.leftMargin = mCameraParam.getResultLeftAndRight();
            img_picture_cancel.setLayoutParams(pictureCancelParams);

            RelativeLayout.LayoutParams pictureSaveParams = (RelativeLayout.LayoutParams) img_picture_save.getLayoutParams();
            pictureSaveParams.rightMargin = mCameraParam.getResultLeftAndRight();
            img_picture_save.setLayoutParams(pictureSaveParams);
        }

        if (mCameraParam.getBackLeft() != -1) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) tv_back.getLayoutParams();
            layoutParams.leftMargin = mCameraParam.getBackLeft();
            tv_back.setLayoutParams(layoutParams);
        }
        Tools.reflectPreviewRatio(previewView, Tools.aspectRatio(this));
    }

    private void initView() {
        previewView = findViewById(R.id.previewView);
        img_switch = findViewById(R.id.img_switch);
        ll_picture_parent = findViewById(R.id.ll_picture_parent);
        img_picture = findViewById(R.id.img_picture);
        focus_view = findViewById(R.id.focus_view);
        view_mask = findViewById(R.id.view_mask);
        rl_result_picture = findViewById(R.id.rl_result_picture);
        img_picture_cancel = findViewById(R.id.img_picture_cancel);
        img_picture_save = findViewById(R.id.img_picture_save);
        rl_start = findViewById(R.id.rl_start);
        tv_back = findViewById(R.id.tv_back);
        img_take_photo = findViewById(R.id.img_take_photo);
        top_View = findViewById(R.id.top_View);
        bottom_View = findViewById(R.id.bottom_View);
        quit_TV = findViewById(R.id.quit_TV);

        //切换相机
        img_switch.setOnClickListener(v -> {
            switchOrition();
            bindCameraUseCases();
        });

        //拍照成功然后点取消
        img_picture_cancel.setOnClickListener(v -> {
            img_picture.setImageBitmap(null);
            rl_start.setVisibility(View.VISIBLE);
            rl_result_picture.setVisibility(View.GONE);
            ll_picture_parent.setVisibility(View.GONE);
            view_mask.setVisibility(View.VISIBLE);
            top_View.setVisibility(View.VISIBLE);
            bottom_View.setVisibility(View.VISIBLE);
            autoFocusTimer();
        });
        //拍照成功然后点保存
        img_picture_save.setOnClickListener(v -> {
            savePicture();
        });
        //还没拍照就点取消
        tv_back.setOnClickListener(v -> {
            finish();
        });
        //点击拍照
        img_take_photo.setOnClickListener(v -> {
            takePhoto(mCameraParam.getPictureTempPath());
        });
        //拍完照退出
        quit_TV.setOnClickListener(v -> {
            finish();
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            autoFocus((int) event.getX(), (int) event.getY(), false);
        }
        return super.onTouchEvent(event);
    }

    private void switchOrition() {
        if (front) {
            front = false;
        } else {
            front = true;
        }
    }

    private void intCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCameraUseCases();
            } catch (Exception e) {
                Log.d("wld________", e.toString());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCameraUseCases() {
        int screenAspectRatio = Tools.aspectRatio(this);
        int rotation = previewView.getDisplay() == null ? Surface.ROTATION_0 : previewView.getDisplay().getRotation();

        Preview preview = new Preview.Builder()
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build();

        imageCapture = new ImageCapture.Builder()
                //优化捕获速度，可能降低图片质量
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(rotation)
                .build();
        // 在重新绑定之前取消绑定用例
        cameraProvider.unbindAll();
        int cameraOrition = front ? CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK;
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(cameraOrition).build();
        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        mCameraControl = camera.getCameraControl();
//        mCameraInfo = camera.getCameraInfo();
//        autoFocus(outLocation[0] + (view_mask.getMeasuredWidth()) / 2, outLocation[1] + (view_mask.getMeasuredHeight()) / 2, true);
        autoFocusTimer();
    }

    private void autoFocusTimer() {
        int[] outLocation = Tools.getViewLocal(view_mask);
        autoFocusCancel();
        mTimerDisposable = Observable.interval(0, 3, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        autoFocus(outLocation[0] + (view_mask.getMeasuredWidth()) / 2, outLocation[1] + (view_mask.getMeasuredHeight()) / 2, true);
                    }
                });
    }

    private void autoFocusCancel() {
        if (mTimerDisposable != null)
            mTimerDisposable.dispose();
    }

    private void takePhoto(String photoFile) {
        // 保证相机可用
        if (imageCapture == null)
            return;

        autoFocusCancel();

        ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(new File(photoFile)).build();

        //  设置图像捕获监听器，在拍照后触发
        imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        rl_start.setVisibility(View.GONE);
                        rl_result_picture.setVisibility(View.VISIBLE);
                        ll_picture_parent.setVisibility(View.VISIBLE);
                        top_View.setVisibility(View.GONE);
                        bottom_View.setVisibility(View.GONE);
//                        Bitmap bitmap = Tools.bitmapClip(CameraActivity.this, photoFile, front);
//                        img_picture.setImageBitmap(bitmap);
                        cutPicture();
                        view_mask.setVisibility(View.GONE);
                        Bitmap bitmap =  BitmapFactory.decodeFile(mCameraParam.getPicturePath());
                        Glide.with(CameraActivity.this).load(bitmap).into(img_picture);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e("wld_____", "Photo capture failed: ${exc.message}", exception);
                    }
                });
    }

    /**
     * 剪切照片
     */
    private void cutPicture() {
        Rect rect = null;
        if (mCameraParam.isShowMask()) {
            int[] outLocation = Tools.getViewLocal(view_mask);
            rect = new Rect(outLocation[0], outLocation[1],
                    view_mask.getMeasuredWidth(), view_mask.getMeasuredHeight());
        }
        Tools.saveBitmap(this, mCameraParam.getPictureTempPath(), mCameraParam.getPicturePath(), rect, front);
        Tools.deletTempFile(mCameraParam.getPictureTempPath());
    }

    private void savePicture() {
        if (TextUtils.isEmpty(mCameraParam.getPicturePath())) {
            cutPicture();//拍照后已剪切无需再调用
        }
        Intent intent = new Intent();
        intent.putExtra(CameraConstant.PICTURE_PATH_KEY, mCameraParam.getPicturePath());
        setResult(RESULT_OK, intent);
        finish();
    }

    //https://developer.android.com/training/camerax/configuration
    private void autoFocus(int x, int y, boolean first) {
//        MeteringPointFactory factory = previewView.getMeteringPointFactory();
        MeteringPointFactory factory = new SurfaceOrientedMeteringPointFactory(x, y);
        MeteringPoint point = factory.createPoint(x, y);
        FocusMeteringAction action = new FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
//                .disableAutoCancel()
//                .addPoint(point2, FocusMeteringAction.FLAG_AE)
                // 3秒内自动调用取消对焦
                .setAutoCancelDuration(mCameraParam.getFocusViewTime(), TimeUnit.SECONDS)
                .build();
//        mCameraControl.cancelFocusAndMetering();
        ListenableFuture<FocusMeteringResult> future = mCameraControl.startFocusAndMetering(action);
        future.addListener(() -> {
            try {
                FocusMeteringResult result = future.get();
                if (result.isFocusSuccessful()) {
                    focus_view.showFocusView(x, y);
                    if (!first && mCameraParam.isShowFocusTips()) {
                        Toast mToast = Toast.makeText(getApplicationContext(), mCameraParam.getFocusSuccessTips(this), Toast.LENGTH_LONG);
                        mToast.setGravity(Gravity.CENTER, 0, 0);
                        mToast.show();
                    }
                } else {
                    if (mCameraParam.isShowFocusTips()) {
                        Toast mToast = Toast.makeText(getApplicationContext(), mCameraParam.getFocusFailTips(this), Toast.LENGTH_LONG);
                        mToast.setGravity(Gravity.CENTER, 0, 0);
                        mToast.show();
                    }
                    focus_view.hideFocusView();
                }
            } catch (Exception e) {
                e.printStackTrace();
                focus_view.hideFocusView();
            }
        }, ContextCompat.getMainExecutor(this));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        autoFocusCancel();
        cameraProvider.unbindAll();
//        if (cameraExecutor != null)
//            cameraExecutor.shutdown();
    }
}
