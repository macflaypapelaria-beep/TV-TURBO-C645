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
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {
    private static final String APP_URL = "https://pwzmywuxdgutslqeypas.supabase.co/functions/v1/superadmin-app";
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
        FrameLayout.LayoutParams webParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        root.addView(webView, webParams);

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dp(3)
        );
        progressParams.gravity = android.view.Gravity.TOP;
        root.addView(progressBar, progressParams);

        setContentView(root);
        configureWebView();
        showBrandSplash();
        loadSuperadminHtml();
    }

    private void configureWebView() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(false);
        settings.setAllowContentAccess(false);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
        settings.setSupportMultipleWindows(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);
        settings.setBuiltInZoomControls(false);
        settings.setDisplayZoomControls(false);
        settings.setSupportZoom(false);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setUserAgentString(settings.getUserAgentString() + " MAC-SUPERADMIN-ANDROID/1.0.2");

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            WebView.startSafeBrowsing(this, null);
        }

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                progressBar.setVisibility(newProgress >= 100 ? View.GONE : View.VISIBLE);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (uri != null && isSuperadminPage(uri)) {
                    loadSuperadminHtml();
                    return true;
                }
                return handleNavigation(uri);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) {
                    showOfflinePage();
                }
            }
        });
    }

    private boolean isSuperadminPage(Uri uri) {
        return uri != null
                && "https".equalsIgnoreCase(uri.getScheme())
                && ALLOWED_HOST.equalsIgnoreCase(uri.getHost())
                && "/functions/v1/superadmin-app".equals(uri.getPath())
                && uri.getQuery() == null;
    }

    private void showBrandSplash() {
        String html = "<!doctype html><html><head><meta name='viewport' content='width=device-width,initial-scale=1'>" +
                "<style>body{margin:0;background:#000;color:#fff;font-family:Arial,sans-serif;display:flex;min-height:100vh;align-items:center;justify-content:center;padding:24px;box-sizing:border-box}" +
                ".wrap{text-align:center}.mark{display:inline-flex;align-items:center;justify-content:center;width:122px;height:122px;border:2px solid #e10600;border-radius:30px;background:linear-gradient(145deg,#090909,#171717);box-shadow:0 0 42px rgba(225,6,0,.28);color:#e10600;font-size:42px;font-weight:1000;letter-spacing:-4px}" +
                ".name{margin-top:18px;font-size:22px;font-weight:900;letter-spacing:1px}.name b{color:#e10600}.producer{margin-top:7px;color:#b9b9b9;font-size:11px;letter-spacing:2px}.bar{width:180px;height:3px;background:#191919;margin:24px auto 0;overflow:hidden;border-radius:9px}.bar:after{content:'';display:block;width:45%;height:100%;background:#e10600;animation:a 1s infinite alternate}@keyframes a{from{transform:translateX(-20px)}to{transform:translateX(120px)}}</style></head>" +
                "<body><div class='wrap'><div class='mark'>MAC</div><div class='name'><b>MAC</b> SUPERADMIN</div><div class='producer'>MATHEUS ARAUJO CASTRO • PRODUTOR</div><div class='bar'></div></div></body></html>";
        webView.loadDataWithBaseURL(APP_URL, html, "text/html", "UTF-8", APP_URL);
    }

    private void loadSuperadminHtml() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setIndeterminate(true);

        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(APP_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(20000);
                connection.setInstanceFollowRedirects(true);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "text/html,application/xhtml+xml;q=0.9,*/*;q=0.8");
                connection.setRequestProperty("Cache-Control", "no-cache");
                connection.setRequestProperty("User-Agent", "MAC-SUPERADMIN-ANDROID/1.0.2");

                int status = connection.getResponseCode();
                if (status < 200 || status >= 400) {
                    throw new IllegalStateException("HTTP " + status);
                }

                String html = readAll(connection.getInputStream());
                if (html == null || !html.toLowerCase().contains("<!doctype html")) {
                    throw new IllegalStateException("Resposta inválida do servidor");
                }

                // Identidade proprietária MAC: sem símbolos de terceiros.
                html = html.replace("MACFLAY SUPERADMIN", "MAC SUPERADMIN");
                html = html.replace("<div class=\"logo\">MF</div>", "<div class=\"logo maclogo\">MAC</div>");
                html = html.replace("<div class=\"logo\">MS</div>", "<div class=\"logo maclogo\">MAC</div>");
                html = html.replace(
                        "<p>Gestão central de clientes, licenças e aparelhos.</p>",
                        "<p>Gestão central de clientes, licenças e aparelhos.</p><div class=\"macproducer\">MATHEUS ARAUJO CASTRO • PRODUTOR</div>"
                );
                html = html.replace(
                        "background:linear-gradient(145deg,#ffbf33,#f19600);color:#111;",
                        "background:#050505;color:#e10600;border:1px solid #6b0808;"
                );
                html = html.replace("--accent:#f5a800", "--accent:#e10600");
                html = html.replace(
                        "</style>",
                        ".maclogo{font-size:13px!important;letter-spacing:-1px;color:#e10600!important;background:linear-gradient(145deg,#050505,#151515)!important;border:1px solid #700b0b!important;box-shadow:0 8px 30px rgba(225,6,0,.22)!important}.macproducer{margin:-10px 0 16px;color:#e10600;font-size:10px;font-weight:900;letter-spacing:1.6px}.brandtxt span:after{content:' • MAC PRODUTOR';color:#e10600}.btn.primary,.tab.on,.fab{background:#e10600!important;border-color:#e10600!important;color:#fff!important}</style>"
                );

                final String finalHtml = html;
                runOnUiThread(() -> {
                    if (webView == null) return;
                    progressBar.setIndeterminate(false);
                    webView.loadDataWithBaseURL(APP_URL, finalHtml, "text/html", "UTF-8", APP_URL);
                });
            } catch (Exception ex) {
                runOnUiThread(this::showOfflinePage);
            } finally {
                if (connection != null) connection.disconnect();
            }
        }).start();
    }

    private String readAll(InputStream stream) throws Exception {
        StringBuilder out = new StringBuilder(32768);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line).append('\n');
            }
        }
        return out.toString();
    }

    private boolean handleNavigation(Uri uri) {
        if (uri == null) return true;
        String scheme = uri.getScheme();
        String host = uri.getHost();

        if ("https".equalsIgnoreCase(scheme) && ALLOWED_HOST.equalsIgnoreCase(host)) {
            return false;
        }

        try {
            Intent external = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(external);
        } catch (Exception ex) {
            Toast.makeText(this, "Não foi possível abrir este link.", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void showOfflinePage() {
        if (webView == null) return;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);
        String html = "<!doctype html><html><head><meta name='viewport' content='width=device-width,initial-scale=1'>" +
                "<style>body{margin:0;background:#000;color:#fff;font-family:Arial,sans-serif;display:flex;min-height:100vh;align-items:center;justify-content:center;padding:24px;box-sizing:border-box}.c{max-width:420px;text-align:center}.mac{font-size:60px;font-weight:1000;letter-spacing:-5px;color:#e10600;margin-bottom:10px}.producer{font-size:10px;letter-spacing:2px;color:#aaa;margin-top:8px}.b{display:inline-block;margin-top:18px;padding:14px 20px;border-radius:12px;background:#e10600;color:#fff;font-weight:800;text-decoration:none}</style></head>" +
                "<body><div class='c'><div class='mac'>MAC</div><h2>MAC SUPERADMIN</h2><div class='producer'>MATHEUS ARAUJO CASTRO • PRODUTOR</div><p>Sem conexão com o servidor. Este aplicativo exige internet para administrar clientes e licenças.</p>" +
                "<a class='b' href='" + APP_URL + "'>TENTAR NOVAMENTE</a></div></body></html>";
        webView.loadDataWithBaseURL(APP_URL, html, "text/html", "UTF-8", APP_URL);
    }

    private int dp(int value) {
        float density = getResources().getDisplayMetrics().density;
        return Math.max(1, Math.round(value * density));
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (webView != null) webView.onResume();
    }

    @Override
    protected void onPause() {
        if (webView != null) webView.onPause();
        super.onPause();
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
