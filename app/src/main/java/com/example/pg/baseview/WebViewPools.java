package com.example.pg.baseview;


import android.app.Activity;
import android.content.MutableContextWrapper;
import android.webkit.WebView;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicReference;

public class WebViewPools {
    //AtomicReference提供了以无锁方式访问共享资源的能力
    private static WebViewPools mWebPools = null;

    /**
     * 引用类型的原子类
     **/
    private static final AtomicReference<WebViewPools> mAtomicReference = new AtomicReference<>();

    private final Queue<WebView> mWebViews;

    private Object lock = new Object();

    private WebViewPools() {
        mWebViews = new LinkedBlockingQueue<>();
    }

    public static WebViewPools getInstance() {
        for (; ; ) {
            if (mWebPools != null)
                return mWebPools;
            if (mAtomicReference.compareAndSet(null, new WebViewPools()))
                return mWebPools = mAtomicReference.get();
        }
    }

    /**
     * 页面销毁
     * （1）去除WebView的上下文，避免内存泄漏
     * （2）加入缓存
     **/
    public void recycle(WebView webView) {
        recycleInternal(webView);
    }

    /**
     * 页面加入浏览器
     * （1）缓存没有，则新建webView
     **/
    public WebView acquireWebView(Activity activity) {
        return acquireWebViewInternal(activity);
    }


    private WebView acquireWebViewInternal(Activity activity) {

        WebView mWebView = mWebViews.poll();

        if (mWebView == null) {
            synchronized (lock) {
                return new WebView(new MutableContextWrapper(activity));
            }
        } else {
            MutableContextWrapper mMutableContextWrapper = (MutableContextWrapper) mWebView.getContext();
            mMutableContextWrapper.setBaseContext(activity);
            return mWebView;
        }
    }


    private void recycleInternal(WebView webView) {
        try {
            if (webView.getContext() instanceof MutableContextWrapper) {
                MutableContextWrapper mContext = (MutableContextWrapper) webView.getContext();
                mContext.setBaseContext(mContext.getApplicationContext());
                mWebViews.offer(webView);
            }
            if (webView.getContext() instanceof Activity) {
                throw new RuntimeException("leaked");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}


