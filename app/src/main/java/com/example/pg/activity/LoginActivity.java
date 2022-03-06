package com.example.pg.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.pg.R;
import com.example.pg.baseview.BaseActivity;
import com.example.pg.bean.Login_Bean;
import com.example.pg.common.utils.GsonUtil;
import com.example.pg.common.utils.L;
import com.example.pg.common.utils.xUtils3Http;
import com.tencent.mmkv.MMKV;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity {
    private WebView web_view;
    private String authCode = "";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected void initView() {
        web_view = findViewById(R.id.web_view);
        initJS();
        String url = xUtils3Http.SSO_PingID;
        initWebView(url);
        getCode();
    }

    @Override
    protected void getServerData() {
    }

    /**
     * 加载地址
     *
     * @param url
     */
    private void initWebView(String url) {
        web_view.loadUrl(url);
    }

    /**
     * webview配置
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void initJS() {
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.getSettings().setDomStorageEnabled(true);
    }

    /**
     * 拦截地址后的authCode
     */
    private void getCode() {
        web_view.setWebViewClient(new WebViewClient() {
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