package com.example.pg.baseview;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.pg.R;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.engine.CropEngine;
import com.luck.picture.lib.entity.LocalMedia;
import com.luck.picture.lib.utils.DateUtils;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCropImageEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

/**
 * 自定义裁剪
 */
public class ImageCropEngine implements CropEngine {

    @Override
    public void onStartCrop(Fragment fragment, LocalMedia currentLocalMedia,
                            ArrayList<LocalMedia> dataSource, int requestCode) {
        String currentCropPath = currentLocalMedia.getAvailablePath();
        Uri inputUri;
        if (PictureMimeType.isContent(currentCropPath) || PictureMimeType.isHasHttp(currentCropPath)) {
            inputUri = Uri.parse(currentCropPath);
        } else {
            inputUri = Uri.fromFile(new File(currentCropPath));
        }
        String fileName = DateUtils.getCreateFileName("CROP_") + ".jpg";
        Uri destinationUri = Uri.fromFile(new File(getSandboxPath(fragment), fileName));
        UCrop.Options options = buildOptions(fragment);
        ArrayList<String> dataCropSource = new ArrayList<>();
        for (int i = 0; i < dataSource.size(); i++) {
            LocalMedia media = dataSource.get(i);
            dataCropSource.add(media.getAvailablePath());
        }
        UCrop uCrop = UCrop.of(inputUri, destinationUri, dataCropSource);
        //options.setMultipleCropAspectRatio(buildAspectRatios(dataSource.size()));
        uCrop.withOptions(options);
        uCrop.setImageEngine(new UCropImageEngine() {
            @Override
            public void loadImage(Context context, String url, ImageView imageView) {
                if (!assertValidRequest(context)) {
                    return;
                }
                Glide.with(context).load(url).override(180, 180).into(imageView);
            }

            @Override
            public void loadImage(Context context, Uri url, int maxWidth, int maxHeight, OnCallbackListener<Bitmap> call) {
                if (!assertValidRequest(context)) {
                    return;
                }
                Glide.with(context).asBitmap().override(maxWidth, maxHeight).load(url).into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (call != null) {
                            call.onCall(resource);
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        if (call != null) {
                            call.onCall(null);
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                    }
                });
            }
        });
        uCrop.start(fragment.getActivity(), fragment, requestCode);
    }

    /**
     * 创建自定义输出目录
     *
     * @return
     */
    private String getSandboxPath(Fragment fragment) {
        File externalFilesDir = fragment.requireContext().getExternalFilesDir("");
        File customFile = new File(externalFilesDir.getAbsolutePath(), "Sandbox");
        if (!customFile.exists()) {
            customFile.mkdirs();
        }
        return customFile.getAbsolutePath() + File.separator;
    }

    /**
     * 配制UCrop，可根据需求自我扩展
     *
     * @return
     */
    private UCrop.Options buildOptions(Fragment fragment) {
        UCrop.Options options = new UCrop.Options();
        options.setHideBottomControls(true);//裁剪菜单栏
        options.setFreeStyleCropEnabled(true);//裁剪框动或者图能拖动
//        options.setShowCropFrame(cb_showCropFrame.isChecked());
//        options.setShowCropGrid(cb_showCropGrid.isChecked());
//        options.setCircleDimmedLayer(cb_crop_circular.isChecked());
//        options.withAspectRatio(aspect_ratio_x, aspect_ratio_y);
//        options.setCropOutputPathDir(getSandboxPath());
        options.isCropDragSmoothToCenter(false);
//        options.isUseCustomLoaderBitmap(cb_crop_use_bitmap.isChecked());
//        options.setSkipCropMimeType(getNotSupportCrop());
//        options.isForbidCropGifWebp(cb_not_gif.isChecked());
//        options.isForbidSkipMultipleCrop(false);
//        options.setMaxScaleMultiplier(100);
//        if (selectorStyle != null && selectorStyle.getSelectMainStyle().getStatusBarColor() != 0) {
//            SelectMainStyle mainStyle = selectorStyle.getSelectMainStyle();
//            boolean isDarkStatusBarBlack = mainStyle.isDarkStatusBarBlack();
//            int statusBarColor = mainStyle.getStatusBarColor();
//            options.isDarkStatusBarBlack(isDarkStatusBarBlack);
//            if (StyleUtils.checkStyleValidity(statusBarColor)) {
//                options.setStatusBarColor(statusBarColor);
//                options.setToolbarColor(statusBarColor);
//            } else {
//                options.setStatusBarColor(ContextCompat.getColor(getContext(), R.color.ps_color_grey));
//                options.setToolbarColor(ContextCompat.getColor(getContext(), R.color.ps_color_grey));
//            }
//            TitleBarStyle titleBarStyle = selectorStyle.getTitleBarStyle();
//            if (StyleUtils.checkStyleValidity(titleBarStyle.getTitleTextColor())) {
//                options.setToolbarWidgetColor(titleBarStyle.getTitleTextColor());
//            } else {
//                options.setToolbarWidgetColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
//            }
//        } else {
//            options.setStatusBarColor(ContextCompat.getColor(fragment.requireContext(), R.color.transparent));
            options.setStatusBarColor(Color.BLACK);
//            options.setToolbarColor(ContextCompat.getColor(getContext(), R.color.ps_color_grey));
//            options.setToolbarWidgetColor(ContextCompat.getColor(getContext(), R.color.ps_color_white));
//        }
        return options;
    }

    public static boolean assertValidRequest(Context context) {
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return !isDestroy(activity);
        } else if (context instanceof ContextWrapper){
            ContextWrapper contextWrapper = (ContextWrapper) context;
            if (contextWrapper.getBaseContext() instanceof Activity){
                Activity activity = (Activity) contextWrapper.getBaseContext();
                return !isDestroy(activity);
            }
        }
        return true;
    }

    private static boolean isDestroy(Activity activity) {
        if (activity == null) {
            return true;
        }
        return activity.isFinishing() || activity.isDestroyed();
    }


}