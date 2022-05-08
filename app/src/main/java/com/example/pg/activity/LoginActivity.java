package com.example.pg.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.http.SslError;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private ConstraintLayout network_Con;
    private TextView reload_Tv;


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
        network_Con = findViewById(R.id.network_Con);
        reload_Tv = findViewById(R.id.reload_Tv);
        web_Con = findViewById(R.id.web_Con);
        x5Webview = WebViewPools.getInstance().acquireWebView(this);
        web_Con.addView(x5Webview, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        initJS();
        String url = xUtils3Http.SSO_PingID;
        initWebView(url);
        getCode();
    }

    @Override
    protected void initEvent() {
        super.initEvent();
        reload_Tv.setOnClickListener(new reload_TvClick());
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

    private class reload_TvClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            isWebViewloadError = false;
            String url = xUtils3Http.SSO_PingID;
            initWebView(url);
        }
    }


    private boolean isWebViewloadError = false;//记录webView是否已经加载出错

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

            //  加载失败
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                isWebViewloadError = true;
                view.loadUrl("about:blank");// 避免出现默认的错误界面
                webLoad();
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                isWebViewloadError = true;
                view.loadUrl("about:blank");// 避免出现默认的错误界面
                webLoad();
            }

            //加载证书错误网页失败
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                super.onReceivedSslError(view, handler, error);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    x5Webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                }
//                handler.proceed();
//                isWebViewloadError = true;
//                network_Con.setVisibility(View.VISIBLE);
            }
        });

        /**
         * 加载进度条
         */
        x5Webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    //加载100%
                    if (!isWebViewloadError && View.VISIBLE == network_Con.getVisibility()) {
                        network_Con.setVisibility(View.GONE);//重新加载按钮
                        x5Webview.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
    }

    int index = 0;
    /**
     * 重新加载webview 加载三次显示网络不好
     */
    private void webLoad() {
        L.d("index", index + "");
        if (index < 3) {
            x5Webview.reload();
        } else {
//            index = 0;
            webShow();
        }
        index++;
    }

    /**
     * 重新加载按钮
     */
    private void webShow() {
        x5Webview.setVisibility(View.GONE);
        network_Con.setVisibility(View.VISIBLE);
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