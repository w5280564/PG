package com.example.pg.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.pg.R;
import com.example.pg.baseview.BaseActivity;
import com.example.pg.baseview.GlideUtils;
import com.example.pg.baseview.livedatas.LiveDataBus;
import com.example.pg.baseview.livedatas.MyConstant;
import com.gyf.barlibrary.ImmersionBar;


/**
 * 拍照马肯码分析结果
 */
public class MarkenCode_Activity extends BaseActivity {
    private String makenStr = "";
    private String imgUrl;
    private String image_source;

    /**
     * @param context
     * @param makenStr     马肯码分析结果
     * @param imgUrl       拍照照片
     * @param image_source 拍照 1 或者相册 2
     */
    public static void startActivity(Context context, String makenStr, String imgUrl, String image_source) {
        Intent intent = new Intent(context, MarkenCode_Activity.class);
        intent.putExtra("makenStr", makenStr);
        intent.putExtra("imgUrl", imgUrl);
        intent.putExtra("image_source", image_source);
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
        return R.layout.marken_takepicture;
    }


    @Override
    protected void initLocalData() {
        super.initLocalData();
        makenStr = getIntent().getStringExtra("makenStr");
        imgUrl = getIntent().getStringExtra("imgUrl");
        image_source = getIntent().getStringExtra("image_source");
    }

    @Override
    protected void initView() {
        ImageView maken_Img = findMyViewById(R.id.maken_Img);
        TextView maken_Tv = findMyViewById(R.id.maken_Tv);
        TextView again_Tv = findMyViewById(R.id.again_Tv);
        TextView quit_Tv = findMyViewById(R.id.quit_Tv);

        if (!TextUtils.isEmpty(imgUrl)) {
            GlideUtils.loadImage(mActivity, maken_Img, imgUrl);
        }
        if (TextUtils.equals(image_source, "1")) {
            if (TextUtils.equals(makenStr, "0") || TextUtils.equals(makenStr, "1")) {
                again_Tv.setText("再拍一个");
            } else {
                again_Tv.setText("重新拍照");
            }
        } else {
            again_Tv.setText("重新上传");
        }

        if (TextUtils.equals(makenStr, "0")) {
            String txt = "初步判断为假货";
            int color = mActivity.getColor(R.color.text_color_e15858);
            setName(txt, maken_Tv, color);
        } else if (TextUtils.equals(makenStr, "1")) {
            String txt = "初步判断为正品";
            int color = mActivity.getColor(R.color.text_color_79a846);
            setName(txt, maken_Tv, color);

        } else if (TextUtils.equals(makenStr, "2") || TextUtils.equals(makenStr, "3")) {
            maken_Tv.setText("识别失败，请重新拍照");

        }


        again_Tv.setOnClickListener(v -> {
            if (TextUtils.equals(image_source, "1")) {
                LiveDataBus.get().with(MyConstant.Again_TakePicture).setValue(true);
            } else {
                LiveDataBus.get().with(MyConstant.Choose_Photo).setValue(true);
            }
            finish();
        });

        quit_Tv.setOnClickListener(v -> {
            finish();
        });
    }


    /**
     * @param name     要显示的数据
     * @param viewName
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setName(String name, TextView viewName, int color) {
        int nameLength = 2;
        int endLength = name.length();
        int startLength = endLength - nameLength;
        SpannableString spannableString = new SpannableString(name);
        spannableString.setSpan(new ForegroundColorSpan(color), startLength, endLength, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        viewName.setText(spannableString);
    }


}