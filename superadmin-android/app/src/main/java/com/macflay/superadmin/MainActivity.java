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
        settings.setUserAgentString(settings.getUserAgentString() + " MAC-SUPERADMIN-ANDROID/1.0.3");

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
                ".wrap{text-align:center;max-width:440px}.mark{position:relative;display:inline-flex;align-items:center;justify-content:center;width:154px;height:112px;color:#e10600;font-size:52px;font-weight:1000;letter-spacing:-7px;font-style:italic}" +
                ".mark:before,.mark:after{content:'';position:absolute;left:8px;right:8px;height:2px;background:linear-gradient(90deg,transparent,#e10600,transparent)}.mark:before{top:4px}.mark:after{bottom:3px}.mark .c{color:#f3f3f3}" +
                ".name{margin-top:10px;font-size:24px;font-weight:1000;letter-spacing:1.8px}.name b{color:#e10600}.tag{margin-top:14px;color:#fff;font-size:13px;font-weight:900;letter-spacing:.8px;text-transform:uppercase}.sub{margin-top:8px;color:#e10600;font-size:10px;font-weight:900;letter-spacing:1.4px}.bar{width:190px;height:3px;background:#181818;margin:28px auto 0;overflow:hidden;border-radius:9px}.bar:after{content:'';display:block;width:45%;height:100%;background:#e10600;animation:a 1s infinite alternate}@keyframes a{from{transform:translateX(-30px)}to{transform:translateX(130px)}}</style></head>" +
                "<body><div class='wrap'><div class='mark'>MA<span class='c'>C</span></div><div class='name'><b>MAC</b> SUPERADMIN</div><div class='tag'>O poder da gestão na palma da sua mão!</div><div class='sub'>CONTROLE TOTAL • GESTÃO INTELIGENTE • RESULTADOS REAIS</div><div class='bar'></div></div></body></html>";
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
                connection.setRequestProperty("User-Agent", "MAC-SUPERADMIN-ANDROID/1.0.3");

                int status = connection.getResponseCode();
                if (status < 200 || status >= 400) {
                    throw new IllegalStateException("HTTP " + status);
                }

                String html = readAll(connection.getInputStream());
                if (html == null || !html.toLowerCase().contains("<!doctype html")) {
                    throw new IllegalStateException("Resposta inválida do servidor");
                }

                // Identidade comercial MAC: rubro-negra, própria e sem símbolos de terceiros.
                html = html.replace("MACFLAY SUPERADMIN", "MAC SUPERADMIN");
                html = html.replace("<div class=\"logo\">MF</div>", "<div class=\"logo maclogo\"><span>MA</span><i>C</i></div>");
                html = html.replace("<div class=\"logo\">MS</div>", "<div class=\"logo maclogo\"><span>MA</span><i>C</i></div>");
                html = html.replace(
                        "<p>Gestão central de clientes, licenças e aparelhos.</p>",
                        "<p>Gestão central de clientes, licenças e aparelhos.</p><div class=\"macclaim\">O PODER DA GESTÃO NA PALMA DA SUA MÃO!</div><div class=\"macsub\">CONTROLE TOTAL • GESTÃO INTELIGENTE • RESULTADOS REAIS</div>"
                );
                html = html.replace(
                        "background:linear-gradient(145deg,#ffbf33,#f19600);color:#111;",
                        "background:#050505;color:#e10600;border:1px solid #6b0808;"
                );
                html = html.replace("--accent:#f5a800", "--accent:#e10600");
                html = html.replace(
                        "</style>",
                        ".maclogo{font-size:12px!important;letter-spacing:-1.4px;color:#e10600!important;background:linear-gradient(145deg,#050505,#151515)!important;border:1px solid #700b0b!important;box-shadow:0 8px 30px rgba(225,6,0,.22)!important;font-style:italic}.maclogo span{color:#e10600}.maclogo i{color:#f2f2f2;font-style:italic}.macclaim{margin:-8px 0 4px;color:#fff;font-size:11px;font-weight:1000;letter-spacing:.5px}.macsub{margin:0 0 16px;color:#e10600;font-size:8px;font-weight:900;letter-spacing:.9px}.brandtxt span:after{content:' • CONTROLE TOTAL';color:#e10600}.btn.primary,.tab.on,.fab{background:#e10600!important;border-color:#e10600!important;color:#fff!important}</style>"
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
                "<style>body{margin:0;background:#000;color:#fff;font-family:Arial,sans-serif;display:flex;min-height:100vh;align-items:center;justify-content:center;padding:24px;box-sizing:border-box}.c{max-width:420px;text-align:center}.mac{font-size:60px;font-weight:1000;letter-spacing:-6px;color:#e10600;font-style:italic;margin-bottom:6px}.mac i{color:#f2f2f2;font-style:italic}.claim{font-size:12px;font-weight:900;margin:8px 0;color:#fff}.sub{font-size:9px;letter-spacing:1px;color:#e10600;margin-bottom:18px}.b{display:inline-block;margin-top:18px;padding:14px 20px;border-radius:12px;background:#e10600;color:#fff;font-weight:800;text-decoration:none}</style></head>" +
                "<body><div class='c'><div class='mac'>MA<i>C</i></div><h2>MAC SUPERADMIN</h2><div class='claim'>O PODER DA GESTÃO NA PALMA DA SUA MÃO!</div><div class='sub'>CONTROLE TOTAL • GESTÃO INTELIGENTE • RESULTADOS REAIS</div><p>Sem conexão com o servidor. Este aplicativo exige internet para administrar clientes e licenças.</p>" +
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
