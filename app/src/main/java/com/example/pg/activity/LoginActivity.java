package com.example.pg.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.pg.R;
import com.example.pg.baseview.BaseActivity;
import com.example.pg.baseview.WebViewPools;
import com.example.pg.bean.Login_Bean;
import com.example.pg.common.utils.GsonUtil;
import com.example.pg.common.utils.L;
import com.example.pg.common.utils.xUtils3Http;
import com.gyf.barlibrary.ImmersionBar;
import com.tencent.mmkv.MMKV;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity {
    private WebView x5Webview;
    private String authCode = "";
    private ConstraintLayout web_Con;


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
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        web_Con = findViewById(R.id.web_Con);
        x5Webview = WebViewPools.getInstance().acquireWebView(this);
        web_Con.addView(x5Webview, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initJS();
        String url = xUtils3Http.SSO_PingID;
        initWebView(url);
        getCode();
    }

    @Override
    protected void getServerData() {
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        web_Con.removeAllViews();
        WebViewPools.getInstance().recycle(x5Webview);
    }

    /**
     * 加载地址
     *
     * @param url
     */
    private void initWebView(String url) {
        x5Webview.loadUrl(url);
    }

    /**
     * webview配置
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initJS() {
        x5Webview.getSettings().setJavaScriptEnabled(true);
        x5Webview.getSettings().setDomStorageEnabled(true);
    }

    /**
     * 拦截地址后的authCode
     */
    private void getCode() {
        x5Webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String code = "code=";
                String url;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    url = request.getUrl().toString();
                } else {
                    url = request.toString();
                    L.d("PG", url);
                }
                return toUrlOrAct(url, code, view);
            }
        });
    }

    /**
     * 拦截url地址 含有code= 截取code登录
     *
     * @param url
     * @param code
     * @param view
     * @return
     */
    private boolean toUrlOrAct(String url, String code, WebView view) {
        boolean is = false; //false 继续使用webview加载
        if (url.contains(code)) {
            int ceIndex = url.indexOf(code);
            if (ceIndex != -1) {
                int startIndex = ceIndex + 5;
                int lastIndex = url.length();
                String substring = url.substring(startIndex, lastIndex);
                L.d("PG", url);
                authCode = substring;
                String s = xUtils3Http.SSO_BASE_URL + xUtils3Http.SSO_Login;
                getV3Login(LoginActivity.this, s);
                is = true;
            } else {
                view.loadUrl(url);
                is = false;
            }
        } else {
            view.loadUrl(url);
        }
        return is;
    }

    /**
     * 拦截url登录获取token
     *
     * @param context
     * @param baseUrl
     */
    public void getV3Login(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("authCode", authCode);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                Login_Bean login_bean = GsonUtil.getInstance().json2Bean(result, Login_Bean.class);
                if (login_bean != null) {
                    MMKV mmkv = MMKV.defaultMMKV();
                    mmkv.encode(xUtils3Http.TOKEN, "Bearer " + login_bean.getToken());
                    mmkv.encode(xUtils3Http.Data, login_bean.getData());
                    skipAnotherActivity(MainActivity.class);
                }
            }

            @Override
            public void failed(String... args) {
            }
        });
    }


}