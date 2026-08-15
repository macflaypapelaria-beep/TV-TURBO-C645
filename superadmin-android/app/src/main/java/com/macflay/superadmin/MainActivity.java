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

    private static final String MOBILE_CSS =
            ".maclogo{width:68px!important;background:transparent!important;border:0!important;box-shadow:none!important;border-radius:0!important;font-size:20px!important;letter-spacing:-2.6px!important;color:#e10600!important;font-style:italic;justify-content:flex-start!important}.maclogo span{color:#e10600}.maclogo i{color:#f4f4f4;font-style:italic}.macclaim{margin:-8px 0 4px;color:#fff;font-size:11px;font-weight:1000;letter-spacing:.5px}.macsub{margin:0 0 16px;color:#e10600;font-size:8px;font-weight:900;letter-spacing:.9px}" +
            "body{background:#000!important}.shell{max-width:560px;margin:0 auto;background:#08090b;min-height:100vh;box-shadow:0 0 70px rgba(0,0,0,.85)}.topbar{position:sticky!important;top:0!important;background:rgba(5,6,8,.98)!important;border-bottom:0!important;padding:calc(9px + env(safe-area-inset-top)) 13px 8px!important}.toprow{height:50px;gap:7px!important}.brand{gap:4px!important}.brandtxt b{font-size:15px!important;letter-spacing:.2px}.brandtxt span{display:none!important}.toprow>.btn{display:none!important}.macMenu{margin-left:auto;border:0;background:transparent;color:#fff;font-size:25px;line-height:1;padding:8px 5px}.main{padding:10px 12px calc(90px + env(safe-area-inset-bottom))!important}.hero{margin:1px 0 11px!important}.hero h1{font-size:23px!important}.hero p{display:none!important}" +
            ".metrics{display:grid!important;grid-template-columns:1fr 1fr!important;gap:9px!important;margin:0 0 15px!important}.metric{display:none!important}.macMetric{border-radius:13px;padding:13px 12px;min-height:91px;border:1px solid rgba(255,255,255,.08);box-shadow:inset 0 1px rgba(255,255,255,.04)}.macMetric .mLabel{font-size:11px;color:#f3f5f7}.macMetric .mValue{display:block;font-size:23px;font-weight:950;margin:4px 0 1px}.macMetric .mSub{font-size:10px;color:rgba(255,255,255,.72)}.macMetric.green{background:linear-gradient(145deg,#043c22,#076c3d)}.macMetric.red{background:linear-gradient(145deg,#531014,#8f161d)}.macMetric.blue{background:linear-gradient(145deg,#073662,#095da0)}.macMetric.orange{background:linear-gradient(145deg,#643300,#9a5200)}" +
            ".toolbar{margin:0 0 10px!important;gap:7px!important}.toolbar .search input{height:43px;background:#121519!important;border-color:#20242a!important}.toolbar select{max-width:112px;padding:8px!important;background:#14171b!important}.toolbar>.btn.primary{display:none!important}.macSection{display:flex;align-items:center;justify-content:space-between;margin:3px 1px 9px}.macSection h2{font-size:16px;margin:0}.macSection button{border:0;background:transparent;color:#ff2028;font-size:11px;font-weight:800;padding:5px}.clients{display:block!important}.macClient{display:flex;align-items:center;gap:10px;background:linear-gradient(180deg,#15191d,#111418);border:1px solid #20252b;border-radius:13px;padding:11px;margin:0 0 9px;cursor:pointer}.macAvatar{width:42px;height:42px;border-radius:50%;display:grid;place-items:center;background:#090a0c;border:1px solid #31363e;color:#ff2028;font-weight:1000;font-size:15px;flex:0 0 auto}.macClientMain{min-width:0;flex:1}.macClientName{font-size:13px;font-weight:900;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.macClientMeta{font-size:10px;color:#a3abb6;margin-top:2px}.macClientRight{text-align:right;flex:0 0 auto}.macPrice{font-size:12px;font-weight:900;margin-top:5px}.macArrow{color:#aeb5bf;font-size:20px;margin-left:2px}.pill{padding:4px 7px!important;font-size:9px!important}.empty{margin-top:10px!important}" +
            ".macHubTitle{font-size:14px;font-weight:900;margin:3px 0 10px}.macHubRow{display:flex;align-items:center;justify-content:space-between;gap:10px;background:#121519;border:1px solid #22272e;border-radius:13px;padding:13px 12px;margin-bottom:9px}.macHubRow b{font-size:13px}.macHubRow small{display:block;color:#8f98a4;font-size:10px;margin-top:3px}.macHubValue{text-align:right;font-size:12px;font-weight:900}.macMoreGrid{display:grid;grid-template-columns:1fr 1fr;gap:10px}.macMore{min-height:104px;background:#121519;border:1px solid #242a31;border-radius:15px;padding:14px;color:#fff;text-align:left}.macMore strong{display:block;font-size:14px;margin-top:20px}.macMore span{font-size:10px;color:#929ba7}.macMore.red{border-color:#65181b;background:#241012}.macMore .mi{font-size:23px;color:#ff2028}" +
            ".macBottom{position:fixed;left:50%;transform:translateX(-50%);bottom:0;width:min(560px,100%);height:calc(68px + env(safe-area-inset-bottom));padding:5px 8px env(safe-area-inset-bottom);z-index:60;background:rgba(8,9,11,.98);border-top:1px solid #20242a;display:grid;grid-template-columns:1fr 1fr 1fr 1fr 1fr;align-items:center}.macNav{border:0;background:transparent;color:#9099a5;font-size:9px;font-weight:800;display:flex;flex-direction:column;align-items:center;gap:2px;padding:5px 1px}.macNav .ico{font-size:20px;line-height:20px}.macNav.on{color:#ff2028}.macNav.plus .ico{width:50px;height:50px;margin-top:-25px;border-radius:50%;display:grid;place-items:center;background:#ff2028;color:#fff;font-size:31px;box-shadow:0 6px 25px rgba(255,32,40,.35)}.fab{display:none!important}" +
            ".drawer{width:min(560px,100%)!important;background:#0a0c0f!important}.drawerhead{background:rgba(10,12,15,.98)!important}.card{background:#121519!important;border-color:#232830!important}.tab.on,.btn.primary{background:#e10600!important;border-color:#e10600!important;color:#fff!important}.loginwrap{background:radial-gradient(circle at 50% 15%,#220507 0,#08090b 35%,#000 75%)!important}.login{background:#0c0e11!important;border-color:#282d34!important;box-shadow:0 30px 90px #000!important}.login .logo{width:90px!important;height:55px!important;margin:0 auto 18px!important}.login h1{text-align:center;font-size:22px!important}.login p{text-align:center}.login .macclaim{text-align:center;margin:0 0 5px}.login .macsub{text-align:center;margin-bottom:18px}.field input,.field select,.field textarea{background:#111419!important}.toast{bottom:calc(82px + env(safe-area-inset-bottom))!important}@media(min-width:700px){.shell{border-left:1px solid #20242a;border-right:1px solid #20242a}}";

    private static final String MOBILE_JS =
            "let macView='home';" +
            "function macStatus(c){if(c.status==='cancelled')return ['CANCELADO','gray'];if(c.license&&c.license.allowed)return ['ATIVO','green'];if(/vencid|mensalidade/i.test((c.license&&c.license.reason)||''))return ['VENCIDO','red'];return ['BLOQUEADO','red']}" +
            "function macSetup(){const app=E('appView');if(!app)return;if(!E('macSection')){const s=document.createElement('div');s.id='macSection';s.className='macSection';s.innerHTML='<h2>Clientes Recentes</h2><button onclick=\"macNavigate(\\\'clients\\\')\">Ver todos</button>';const list=E('clients');if(list&&list.parentNode)list.parentNode.insertBefore(s,list)}if(!E('macBottom')){app.insertAdjacentHTML('beforeend','<nav id=\"macBottom\" class=\"macBottom\"><button class=\"macNav on\" data-v=\"home\"><span class=\"ico\">⌂</span>Início</button><button class=\"macNav\" data-v=\"clients\"><span class=\"ico\">♙</span>Clientes</button><button class=\"macNav plus\" data-v=\"add\"><span class=\"ico\">+</span></button><button class=\"macNav\" data-v=\"devices\"><span class=\"ico\">▣</span>Aparelhos</button><button class=\"macNav\" data-v=\"more\"><span class=\"ico\">•••</span>Mais</button></nav>');E('macBottom').querySelectorAll('.macNav').forEach(b=>b.onclick=()=>b.dataset.v==='add'?openCreate():macNavigate(b.dataset.v))}const top=document.querySelector('.toprow');if(top&&!E('macMenu')){const b=document.createElement('button');b.id='macMenu';b.className='macMenu';b.textContent='☰';b.onclick=()=>macNavigate('more');top.appendChild(b)}macNavigate(macView)}" +
            "renderMetrics=function(){if(!DATA)return;const m=DATA.metrics||{},bs=DATA.businesses||[],dev=bs.reduce((n,c)=>n+Number(c.approved_devices||0),0);E('metrics').innerHTML='<div class=\"macMetric green\"><span class=\"mLabel\">Clientes Ativos</span><b class=\"mValue\">'+Number(m.active||0)+'</b><small class=\"mSub\">● Ativos</small></div><div class=\"macMetric red\"><span class=\"mLabel\">Vencidos</span><b class=\"mValue\">'+Number(m.overdue||0)+'</b><small class=\"mSub\">● Atenção</small></div><div class=\"macMetric blue\"><span class=\"mLabel\">Receita (Mês)</span><b class=\"mValue\">'+money(m.mrr||0)+'</b><small class=\"mSub\">MRR</small></div><div class=\"macMetric orange\"><span class=\"mLabel\">Aparelhos</span><b class=\"mValue\">'+dev+'</b><small class=\"mSub\">● Aprovados</small></div>'}" +
            "renderClients=function(){if(!DATA)return;if(macView==='devices'){macDevices();return}if(macView==='payments'){macPayments();return}if(macView==='more'){macMore();return}const q=(E('search')?E('search').value:'').trim().toLowerCase(),f=E('filter')?E('filter').value:'all';let a=DATA.businesses||[];if(macView==='clients'){a=a.filter(c=>!q||String(c.name||'').toLowerCase().includes(q)||String(c.slug||'').toLowerCase().includes(q));a=a.filter(c=>{const st=macStatus(c)[0];if(f==='active')return st==='ATIVO';if(f==='blocked')return st==='BLOQUEADO';if(f==='overdue')return st==='VENCIDO';if(f==='cancelled')return st==='CANCELADO';return true})}else{a=a.slice(0,5)}const box=E('clients');box.innerHTML=a.length?a.map(c=>{const st=macStatus(c),s=c.subscription||{},max=s.max_devices_override||(c.license&&c.license.max_devices)||'—',ini=String(c.name||'?').trim().charAt(0).toUpperCase();return '<article class=\"macClient\" data-client=\"'+esc(c.id)+'\"><div class=\"macAvatar\">'+esc(ini)+'</div><div class=\"macClientMain\"><div class=\"macClientName\">'+esc(c.name)+'</div><div class=\"macClientMeta\">Vence: '+date(s.paid_until)+'</div><div class=\"macClientMeta\">'+Number(c.approved_devices||0)+'/'+esc(max)+' aparelhos</div></div><div class=\"macClientRight\"><span class=\"pill '+st[1]+'\">'+st[0]+'</span><div class=\"macPrice\">'+money(s.monthly_fee)+'</div><div class=\"macClientMeta\">/ mês</div></div><div class=\"macArrow\">›</div></article>'}).join(''):'<div class=\"empty\">Nenhum cliente encontrado.</div>';box.querySelectorAll('[data-client]').forEach(x=>x.onclick=()=>openClient(x.dataset.client))}" +
            "function macDevices(){const a=DATA.businesses||[],box=E('clients');box.innerHTML='<div class=\"macHubTitle\">Aparelhos por cliente</div>'+a.map(c=>{const s=c.subscription||{},max=s.max_devices_override||(c.license&&c.license.max_devices)||'—';return '<div class=\"macHubRow\" data-dev=\"'+esc(c.id)+'\"><div><b>'+esc(c.name)+'</b><small>Toque para gerenciar aparelhos</small></div><div class=\"macHubValue\">'+Number(c.approved_devices||0)+' / '+esc(max)+'<small>'+Number(c.pending_devices||0)+' pendente(s)</small></div></div>'}).join('');box.querySelectorAll('[data-dev]').forEach(x=>x.onclick=()=>macClientTab(x.dataset.dev,'devices'))}" +
            "function macPayments(){const a=DATA.businesses||[],box=E('clients');box.innerHTML='<div class=\"macHubTitle\">Pagamentos e vencimentos</div>'+a.map(c=>{const s=c.subscription||{},st=macStatus(c);return '<div class=\"macHubRow\" data-pay=\"'+esc(c.id)+'\"><div><b>'+esc(c.name)+'</b><small>Vence '+date(s.paid_until)+'</small></div><div class=\"macHubValue\">'+money(s.monthly_fee)+'<small><span class=\"pill '+st[1]+'\">'+st[0]+'</span></small></div></div>'}).join('');box.querySelectorAll('[data-pay]').forEach(x=>x.onclick=()=>macClientTab(x.dataset.pay,'payments'))}" +
            "function macMore(){const box=E('clients');box.innerHTML='<div class=\"macMoreGrid\"><button class=\"macMore\" id=\"mNew\"><span class=\"mi\">＋</span><strong>Novo cliente</strong><span>Cadastrar e liberar acesso</span></button><button class=\"macMore\" id=\"mRefresh\"><span class=\"mi\">↻</span><strong>Atualizar</strong><span>Recarregar dados do servidor</span></button><button class=\"macMore\" id=\"mPay\"><span class=\"mi\">$</span><strong>Pagamentos</strong><span>Vencimentos e mensalidades</span></button><button class=\"macMore red\" id=\"mOut\"><span class=\"mi\">⇥</span><strong>Sair</strong><span>Encerrar sessão administrativa</span></button></div>';E('mNew').onclick=openCreate;E('mRefresh').onclick=loadDashboard;E('mPay').onclick=()=>macNavigate('payments');E('mOut').onclick=logout}" +
            "async function macClientTab(id,t){await openClient(id);tab=t;renderDetail()}" +
            "function macNavigate(v){macView=v;const hero=document.querySelector('.hero h1'),metrics=E('metrics'),toolbar=document.querySelector('.toolbar'),sec=E('macSection');if(hero)hero.textContent=v==='home'?'Dashboard':v==='clients'?'Clientes':v==='devices'?'Aparelhos':v==='payments'?'Pagamentos':'Mais';if(metrics)metrics.style.display=v==='home'?'grid':'none';if(toolbar)toolbar.style.display=v==='clients'?'flex':'none';if(sec){sec.style.display=v==='home'?'flex':'none';sec.querySelector('h2').textContent='Clientes Recentes'}const nav=E('macBottom');if(nav)nav.querySelectorAll('.macNav').forEach(b=>b.classList.toggle('on',b.dataset.v===v));if(DATA){if(v==='home')renderMetrics();renderClients()}window.scrollTo(0,0)}" +
            "const macOldShowApp=showApp;showApp=function(){macOldShowApp();setTimeout(macSetup,0)};const macOldLoadDashboard=loadDashboard;loadDashboard=async function(){await macOldLoadDashboard();macSetup()};setTimeout(macSetup,0);setTimeout(()=>{if(DATA){renderMetrics();renderClients()}},450);";

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
        root.addView(webView, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        FrameLayout.LayoutParams progressParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(3));
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
        settings.setUserAgentString(settings.getUserAgentString() + " MAC-SUPERADMIN-ANDROID/1.0.4");

        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) WebView.startSafeBrowsing(this, null);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                progressBar.setVisibility(newProgress >= 100 ? View.GONE : View.VISIBLE);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (uri != null && isSuperadminPage(uri)) { loadSuperadminHtml(); return true; }
                return handleNavigation(uri);
            }
            @Override public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) showOfflinePage();
            }
        });
    }

    private boolean isSuperadminPage(Uri uri) {
        return uri != null && "https".equalsIgnoreCase(uri.getScheme()) && ALLOWED_HOST.equalsIgnoreCase(uri.getHost())
                && "/functions/v1/superadmin-app".equals(uri.getPath()) && uri.getQuery() == null;
    }

    private void showBrandSplash() {
        String html = "<!doctype html><html><head><meta name='viewport' content='width=device-width,initial-scale=1'>" +
                "<style>body{margin:0;background:#000;color:#fff;font-family:Arial,sans-serif;display:flex;min-height:100vh;align-items:center;justify-content:center;padding:24px;box-sizing:border-box}.wrap{text-align:center;max-width:440px}.mark{position:relative;display:inline-flex;align-items:center;justify-content:center;width:154px;height:112px;color:#e10600;font-size:52px;font-weight:1000;letter-spacing:-7px;font-style:italic}.mark:before,.mark:after{content:'';position:absolute;left:8px;right:8px;height:2px;background:linear-gradient(90deg,transparent,#e10600,transparent)}.mark:before{top:4px}.mark:after{bottom:3px}.mark .c{color:#f3f3f3}.name{margin-top:10px;font-size:24px;font-weight:1000;letter-spacing:1.8px}.name b{color:#e10600}.tag{margin-top:14px;color:#fff;font-size:13px;font-weight:900;letter-spacing:.8px;text-transform:uppercase}.sub{margin-top:8px;color:#e10600;font-size:10px;font-weight:900;letter-spacing:1.4px}.bar{width:190px;height:3px;background:#181818;margin:28px auto 0;overflow:hidden;border-radius:9px}.bar:after{content:'';display:block;width:45%;height:100%;background:#e10600;animation:a 1s infinite alternate}@keyframes a{from{transform:translateX(-30px)}to{transform:translateX(130px)}}</style></head>" +
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
                connection.setRequestProperty("User-Agent", "MAC-SUPERADMIN-ANDROID/1.0.4");
                int status = connection.getResponseCode();
                if (status < 200 || status >= 400) throw new IllegalStateException("HTTP " + status);

                String html = readAll(connection.getInputStream());
                if (html == null || !html.toLowerCase().contains("<!doctype html")) throw new IllegalStateException("Resposta inválida do servidor");

                html = html.replace("MACFLAY SUPERADMIN", "MAC SUPERADMIN");
                html = html.replace("<div class=\"logo\">MF</div>", "<div class=\"logo maclogo\"><span>MA</span><i>C</i></div>");
                html = html.replace("<div class=\"logo\">MS</div>", "<div class=\"logo maclogo\"><span>MA</span><i>C</i></div>");
                html = html.replace("<p>Gestão central de clientes, licenças e aparelhos.</p>", "<p>Gestão central de clientes, licenças e aparelhos.</p><div class=\"macclaim\">O PODER DA GESTÃO NA PALMA DA SUA MÃO!</div><div class=\"macsub\">CONTROLE TOTAL • GESTÃO INTELIGENTE • RESULTADOS REAIS</div>");
                html = html.replace("--accent:#f5a800", "--accent:#e10600");
                html = html.replace("</style>", MOBILE_CSS + "</style>");
                html = html.replace("</body>", "<script>" + MOBILE_JS + "</script></body>");

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
            while ((line = reader.readLine()) != null) out.append(line).append('\n');
        }
        return out.toString();
    }

    private boolean handleNavigation(Uri uri) {
        if (uri == null) return true;
        if ("https".equalsIgnoreCase(uri.getScheme()) && ALLOWED_HOST.equalsIgnoreCase(uri.getHost())) return false;
        try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); }
        catch (Exception ex) { Toast.makeText(this, "Não foi possível abrir este link.", Toast.LENGTH_SHORT).show(); }
        return true;
    }

    private void showOfflinePage() {
        if (webView == null) return;
        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.GONE);
        String html = "<!doctype html><html><head><meta name='viewport' content='width=device-width,initial-scale=1'><style>body{margin:0;background:#000;color:#fff;font-family:Arial,sans-serif;display:flex;min-height:100vh;align-items:center;justify-content:center;padding:24px;box-sizing:border-box}.c{max-width:420px;text-align:center}.mac{font-size:60px;font-weight:1000;letter-spacing:-6px;color:#e10600;font-style:italic;margin-bottom:6px}.mac i{color:#f2f2f2;font-style:italic}.claim{font-size:12px;font-weight:900;margin:8px 0}.sub{font-size:9px;letter-spacing:1px;color:#e10600;margin-bottom:18px}.b{display:inline-block;margin-top:18px;padding:14px 20px;border-radius:12px;background:#e10600;color:#fff;font-weight:800;text-decoration:none}</style></head><body><div class='c'><div class='mac'>MA<i>C</i></div><h2>MAC SUPERADMIN</h2><div class='claim'>O PODER DA GESTÃO NA PALMA DA SUA MÃO!</div><div class='sub'>CONTROLE TOTAL • GESTÃO INTELIGENTE • RESULTADOS REAIS</div><p>Sem conexão com o servidor. Este aplicativo exige internet para administrar clientes e licenças.</p><a class='b' href='" + APP_URL + "'>TENTAR NOVAMENTE</a></div></body></html>";
        webView.loadDataWithBaseURL(APP_URL, html, "text/html", "UTF-8", APP_URL);
    }

    private int dp(int value) { return Math.max(1, Math.round(value * getResources().getDisplayMetrics().density)); }

    @Override public void onBackPressed() { if (webView != null && webView.canGoBack()) webView.goBack(); else super.onBackPressed(); }
    @Override protected void onResume() { super.onResume(); if (webView != null) webView.onResume(); }
    @Override protected void onPause() { if (webView != null) webView.onPause(); super.onPause(); }
    @Override protected void onDestroy() {
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
