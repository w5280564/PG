package com.example.pg.activity;

import android.content.Intent;
import android.webkit.WebView;

import com.example.pg.R;
import com.example.pg.baseview.BaseActivity;
import com.example.pg.baseview.WebViewPools;
import com.gyf.barlibrary.ImmersionBar;

import java.util.Timer;
import java.util.TimerTask;


public class SplashActivity extends BaseActivity {


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
    protected void setStatusBar() {
        setBar();
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
        //long l = System.currentTimeMillis();
        WebView webView = WebViewPools.getInstance().acquireWebView(this);
        //LogUtil.logD("第一次初始化：" + (System.currentTimeMillis() - l));
        WebViewPools.getInstance().recycle(webView);

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        },1000);
    }
}
