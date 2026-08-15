package com.macflay.tvturbo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Locale;

import rikka.shizuku.Shizuku;
import rikka.shizuku.ShizukuRemoteProcess;

public class MainActivity extends Activity {

    private static final int SHIZUKU_PERMISSION_CODE = 9017;

    private TextView ramValue;
    private TextView storageValue;
    private TextView shizukuValue;
    private TextView resultView;
    private Button optimizeButton;
    private Button permissionButton;

    private final Shizuku.OnBinderReceivedListener binderReceivedListener = this::refreshPrivilegeState;
    private final Shizuku.OnBinderDeadListener binderDeadListener = this::refreshPrivilegeState;
    private final Shizuku.OnRequestPermissionResultListener permissionResultListener = (requestCode, grantResult) -> {
        if (requestCode == SHIZUKU_PERMISSION_CODE) {
            runOnUiThread(() -> {
                refreshPrivilegeState();
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    resultView.setText("Modo profundo autorizado. Agora selecione OTIMIZAR AGORA.");
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        buildUi();

        Shizuku.addBinderReceivedListener(binderReceivedListener);
        Shizuku.addBinderDeadListener(binderDeadListener);
        Shizuku.addRequestPermissionResultListener(permissionResultListener);

        refreshDiagnostics();
        refreshPrivilegeState();
    }

    @Override
    protected void onDestroy() {
        Shizuku.removeBinderReceivedListener(binderReceivedListener);
        Shizuku.removeBinderDeadListener(binderDeadListener);
        Shizuku.removeRequestPermissionResultListener(permissionResultListener);
        super.onDestroy();
    }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(Color.rgb(7, 16, 24));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(64), dp(38), dp(64), dp(38));
        scroll.addView(root, new ScrollView.LayoutParams(
                ScrollView.LayoutParams.MATCH_PARENT,
                ScrollView.LayoutParams.WRAP_CONTENT));

        TextView title = label("⚡ TV TURBO C645", 36, Color.WHITE, true);
        root.addView(title);

        TextView subtitle = label("Otimização segura para Google TV • sem root", 18, Color.rgb(145, 190, 205), false);
        subtitle.setPadding(0, dp(4), 0, dp(24));
        root.addView(subtitle);

        LinearLayout stats = new LinearLayout(this);
        stats.setOrientation(LinearLayout.HORIZONTAL);
        root.addView(stats, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(118)));

        ramValue = addStatCard(stats, "RAM DISPONÍVEL");
        storageValue = addStatCard(stats, "ARMAZENAMENTO LIVRE");
        shizukuValue = addStatCard(stats, "MODO PROFUNDO");

        LinearLayout actions = new LinearLayout(this);
        actions.setOrientation(LinearLayout.HORIZONTAL);
        actions.setPadding(0, dp(26), 0, dp(18));
        root.addView(actions, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        optimizeButton = button("🚀  OTIMIZAR AGORA");
        optimizeButton.setOnClickListener(v -> optimizeNow());
        actions.addView(optimizeButton, weightedButtonParams());

        permissionButton = button("🔐  ATIVAR MODO PROFUNDO");
        permissionButton.setOnClickListener(v -> requestShizukuAccess());
        actions.addView(permissionButton, weightedButtonParams());

        Button refreshButton = button("↻  ATUALIZAR");
        refreshButton.setOnClickListener(v -> {
            refreshDiagnostics();
            refreshPrivilegeState();
        });
        actions.addView(refreshButton, weightedButtonParams());

        resultView = label(
                "O TV TURBO não desativa serviços essenciais. A otimização profunda encerra apenas processos que o próprio Android classifica como seguros para matar, reduz caches e acelera animações da interface.",
                18, Color.rgb(210, 225, 230), false);
        resultView.setPadding(dp(24), dp(22), dp(24), dp(22));
        resultView.setBackgroundColor(Color.rgb(13, 31, 42));
        root.addView(resultView, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        setContentView(scroll);
        optimizeButton.requestFocus();
    }

    private TextView addStatCard(LinearLayout parent, String caption) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER);
        card.setPadding(dp(18), dp(12), dp(18), dp(12));
        card.setBackgroundColor(Color.rgb(13, 31, 42));

        TextView value = label("—", 28, Color.rgb(0, 229, 255), true);
        value.setGravity(Gravity.CENTER);
        card.addView(value);

        TextView cap = label(caption, 14, Color.rgb(150, 180, 192), false);
        cap.setGravity(Gravity.CENTER);
        card.addView(cap);

        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f);
        p.setMargins(dp(6), 0, dp(6), 0);
        parent.addView(card, p);
        return value;
    }

    private Button button(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(17);
        b.setTextColor(Color.WHITE);
        b.setAllCaps(false);
        b.setFocusable(true);
        b.setPadding(dp(16), dp(10), dp(16), dp(10));
        b.setBackgroundColor(Color.rgb(19, 75, 91));
        b.setOnFocusChangeListener((v, hasFocus) -> {
            Button btn = (Button) v;
            btn.setScaleX(hasFocus ? 1.06f : 1f);
            btn.setScaleY(hasFocus ? 1.06f : 1f);
            btn.setBackgroundColor(hasFocus ? Color.rgb(0, 121, 145) : Color.rgb(19, 75, 91));
        });
        return b;
    }

    private LinearLayout.LayoutParams weightedButtonParams() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(72), 1f);
        p.setMargins(dp(6), 0, dp(6), 0);
        return p;
    }

    private TextView label(String text, int sp, int color, boolean bold) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(sp);
        tv.setTextColor(color);
        if (bold) tv.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return tv;
    }

    private void refreshDiagnostics() {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        ramValue.setText(formatBytes(mi.availMem));

        StatFs statFs = new StatFs(Environment.getDataDirectory().getAbsolutePath());
        storageValue.setText(formatBytes(statFs.getAvailableBytes()));
    }

    private void refreshPrivilegeState() {
        runOnUiThread(() -> {
            boolean binder = false;
            boolean granted = false;
            try {
                binder = Shizuku.pingBinder();
                granted = binder && Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED;
            } catch (Throwable ignored) { }

            if (granted) {
                int uid;
                try { uid = Shizuku.getUid(); } catch (Throwable e) { uid = -1; }
                shizukuValue.setText(uid == 0 ? "ROOT" : "ADB ✓");
                optimizeButton.setEnabled(true);
                permissionButton.setText("✓  MODO PROFUNDO ATIVO");
            } else if (binder) {
                shizukuValue.setText("SEM PERMISSÃO");
                optimizeButton.setEnabled(false);
                permissionButton.setText("🔐  AUTORIZAR");
            } else {
                shizukuValue.setText("INATIVO");
                optimizeButton.setEnabled(false);
                permissionButton.setText("🔐  ABRIR SHIZUKU");
            }
        });
    }

    private void requestShizukuAccess() {
        try {
            if (!Shizuku.pingBinder()) {
                Intent launch = getPackageManager().getLaunchIntentForPackage("moe.shizuku.privileged.api");
                if (launch != null) {
                    startActivity(launch);
                    resultView.setText("Inicie o Shizuku usando Depuração sem fio e depois volte ao TV TURBO.");
                } else {
                    resultView.setText("Shizuku não está instalado. Instale o aplicativo oficial Shizuku na TV e ative-o por Depuração sem fio.");
                }
                return;
            }

            if (Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED) {
                refreshPrivilegeState();
                resultView.setText("Modo profundo já está autorizado.");
            } else if (Shizuku.shouldShowRequestPermissionRationale()) {
                resultView.setText("A permissão do Shizuku foi negada. Abra o Shizuku e autorize o TV TURBO manualmente.");
            } else {
                Shizuku.requestPermission(SHIZUKU_PERMISSION_CODE);
            }
        } catch (Throwable e) {
            resultView.setText("Não foi possível acessar o Shizuku: " + safe(e.getMessage()));
        }
    }

    private void optimizeNow() {
        try {
            if (!Shizuku.pingBinder() || Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
                requestShizukuAccess();
                return;
            }
        } catch (Throwable e) {
            requestShizukuAccess();
            return;
        }

        optimizeButton.setEnabled(false);
        resultView.setText("Otimizando… não desligue a TV.");

        new Thread(() -> {
            try {
                runPrivileged("am kill-all");
                runPrivileged("pm trim-caches 1500M");
                runPrivileged("settings put global window_animation_scale 0.25");
                runPrivileged("settings put global transition_animation_scale 0.25");
                runPrivileged("settings put global animator_duration_scale 0.25");

                Thread.sleep(800);
                runOnUiThread(() -> {
                    refreshDiagnostics();
                    resultView.setText(
                            "✓ OTIMIZAÇÃO CONCLUÍDA\n\n" +
                            "• Processos em cache seguros foram encerrados.\n" +
                            "• O Android aparou caches visando até 1,5 GB livres.\n" +
                            "• Animações da interface foram reduzidas para 0,25x.\n\n" +
                            "Nenhum app foi desinstalado e nenhum dado pessoal foi apagado.");
                    optimizeButton.setEnabled(true);
                    optimizeButton.requestFocus();
                });
            } catch (Throwable e) {
                runOnUiThread(() -> {
                    resultView.setText("Falha durante a otimização: " + safe(e.getMessage()) +
                            "\n\nO firmware da TV pode restringir alguma ação do shell ADB.");
                    optimizeButton.setEnabled(true);
                });
            }
        }, "tv-turbo-optimizer").start();
    }

    @SuppressWarnings("deprecation")
    private String runPrivileged(String command) throws Exception {
        Method method = Shizuku.class.getDeclaredMethod(
                "newProcess", String[].class, String[].class, String.class);
        method.setAccessible(true);

        Object obj = method.invoke(null,
                new Object[]{new String[]{"sh", "-c", command}, null, null});
        ShizukuRemoteProcess process = (ShizukuRemoteProcess) obj;

        StringBuilder out = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
             BufferedReader err = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) out.append(line).append('\n');
            while ((line = err.readLine()) != null) out.append(line).append('\n');
        }
        process.waitFor();
        process.destroy();
        return out.toString().trim();
    }

    private String formatBytes(long value) {
        double gb = value / 1073741824.0;
        if (gb >= 1.0) return String.format(Locale.getDefault(), "%.2f GB", gb);
        return String.format(Locale.getDefault(), "%.0f MB", value / 1048576.0);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private static String safe(String s) {
        return s == null || s.trim().isEmpty() ? "erro não especificado" : s;
    }
}
