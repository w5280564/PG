package com.example.pg.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.pg.R;
import com.example.pg.baseview.BaseActivity;
import com.example.pg.common.utils.L;
import com.example.pg.common.utils.T;
import com.example.pg.common.utils.xUtils3Http;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends BaseActivity {
    private WebView web_view;
    private String username = "";
    private String password = "";
    private long clickTime;
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
//        String url = "/appUserLogin/beforeLogin";
//        getUrl(this,url);
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
//                    view.loadUrl(request.toString());
                    url = request.toString();
                    L.d("PG", url);
                }
                return toUrlOrAct(url, code, view);
            }
        });
    }

    /**
     * 拦截url地址 含有code= 截取code登录
     * @param url
     * @param code
     * @param view
     * @return
     */
    private boolean toUrlOrAct(String url,String code,WebView view) {
        boolean is = false; //false 继续使用webview加载
        if (url.contains(code)) {
            int ceIndex = url.indexOf(code);
            if (ceIndex != -1) {
                int startIndex = ceIndex + 5;
                int lastIndex = url.length();
                String substring = url.substring(startIndex, lastIndex);
                L.d("PG", url);
                authCode = substring;
                getV3Login(LoginActivity.this, xUtils3Http.SSO_Login);
                is = true;
            } else {
                view.loadUrl(url);
                is = false;
            }
        }else {
            view.loadUrl(url);
        }
        return is;
    }


    public void getUrl(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        map.put("password", password);
        xUtils3Http.post(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                String re = result;
//                Model = new Gson().fromJson(result, AddFriend_FriendGroup_Model.class);
//                if (Model.getData() != null) {
//
//                } else {
//                    Toast.makeText(context, "没有搜到", Toast.LENGTH_SHORT).show();
//                }
            }

            @Override
            public void failed(String... args) {
            }
        });
    }


    public void getV3Login(Context context, String baseUrl) {
        Map<String, Object> map = new HashMap<>();
        map.put("authCode", authCode);
        xUtils3Http.get(context, baseUrl, map, new xUtils3Http.GetDataCallback() {
            @Override
            public void success(String result) {
                String re = result;
                L.d("PG", re);

//                Model = new Gson().fromJson(result, AddFriend_FriendGroup_Model.class);
//                if (Model.getData() != null) {
//
//                } else {
//                    Toast.makeText(context, "没有搜到", Toast.LENGTH_SHORT).show();
//                }

//                skipAnotherActivity(MainActivity.class);
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


}