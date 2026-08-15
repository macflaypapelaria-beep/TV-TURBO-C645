package com.macflay.superadmin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MainActivityV2 extends Activity {
    private static final String BASE_URL = "https://pwzmywuxdgutslqeypas.supabase.co/functions/v1/superadmin-app";
    private static final String ALLOWED_HOST = "pwzmywuxdgutslqeypas.supabase.co";
    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.BLACK);
        getWindow().setNavigationBarColor(Color.BLACK);

        FrameLayout root = new FrameLayout(this);
        root.setBackgroundColor(Color.BLACK);

        webView = new WebView(this);
        root.addView(webView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        FrameLayout.LayoutParams pp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                Math.max(2, Math.round(3 * getResources().getDisplayMetrics().density))
        );
        pp.gravity = android.view.Gravity.TOP;
        root.addView(progressBar, pp);
        setContentView(root);

        configureWebView();
        loadBundledApp();
    }

    private void configureWebView() {
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(false);
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        s.setSupportMultipleWindows(false);
        s.setJavaScriptCanOpenWindowsAutomatically(false);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setSupportZoom(false);
        s.setLoadWithOverviewMode(true);
        s.setUseWideViewPort(true);
        s.setUserAgentString(s.getUserAgentString() + " MAC-SUPERADMIN-ANDROID/1.0.5");

        CookieManager cm = CookieManager.getInstance();
        cm.setAcceptCookie(true);
        cm.setAcceptThirdPartyCookies(webView, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            WebView.startSafeBrowsing(this, null);
        }

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                progressBar.setVisibility(progress >= 100 ? View.GONE : View.VISIBLE);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return handleNavigation(request.getUrl());
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) showFatalPage();
            }
        });
    }

    private void loadBundledApp() {
        try {
            String html = readAll(getAssets().open("superadmin.html"));
            progressBar.setVisibility(View.VISIBLE);
            webView.loadDataWithBaseURL(BASE_URL, html, "text/html", "UTF-8", null);
        } catch (Exception e) {
            showFatalPage();
        }
    }

    private String readAll(InputStream in) throws Exception {
        StringBuilder out = new StringBuilder(65536);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) out.append(line).append('\n');
        }
        return out.toString();
    }

    private boolean handleNavigation(Uri uri) {
        if (uri == null) return true;
        String scheme = uri.getScheme();
        String host = uri.getHost();
        if ("https".equalsIgnoreCase(scheme) && ALLOWED_HOST.equalsIgnoreCase(host)) return false;
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, uri));
        } catch (Exception e) {
            Toast.makeText(this, "Não foi possível abrir este link.", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void showFatalPage() {
        if (webView == null) return;
        progressBar.setVisibility(View.GONE);
        String html = "<!doctype html><meta name='viewport' content='width=device-width,initial-scale=1'>" +
                "<body style='margin:0;background:#050608;color:#fff;font-family:Arial;display:grid;place-items:center;min-height:100vh;padding:24px;box-sizing:border-box'>" +
                "<div style='text-align:center'><div style='font-size:54px;font-weight:1000;font-style:italic;color:#ef1b24'>MA<span style='color:#fff'>C</span></div>" +
                "<h2>MAC SUPERADMIN</h2><p>Não foi possível iniciar a interface do aplicativo.</p>" +
                "<button onclick='location.reload()' style='border:0;border-radius:12px;background:#ef1b24;color:#fff;padding:13px 18px;font-weight:900'>TENTAR NOVAMENTE</button></div></body>";
        webView.loadDataWithBaseURL(BASE_URL, html, "text/html", "UTF-8", null);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) webView.goBack(); else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            webView.loadUrl("about:blank");
            webView.stopLoading();
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
