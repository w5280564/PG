package com.example.pg.baseview;

import com.example.pg.R;
import com.example.pg.common.utils.L;
import com.example.pg.common.utils.StringUtil;

/**
 * 默认的H5展示页面
 */
public class WebviewActivity extends BaseWebviewActivity {

    private String urlHead = "https://";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    protected void initView() {
        mWebview = findViewById(R.id.webview);
        mProgress = findViewById(R.id.progress);
        initWebView();
        String linkUrl = getIntent().getStringExtra("url");
        L.e(linkUrl + "");

        if (!StringUtil.isBlank(linkUrl)) {
//            if(!linkUrl.startsWith("http")){
//            linkUrl = Config.Link.getWholeUrl() + linkUrl;
//            }
            if (!linkUrl.startsWith("http")) {
                linkUrl = urlHead + linkUrl;
            }
            loadUrl(linkUrl);
        }
    }


    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        titleBar.hideRight();
    }

}
