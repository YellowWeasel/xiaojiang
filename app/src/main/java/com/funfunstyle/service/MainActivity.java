package com.funfunstyle.service;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.funfunstyle.service.js.JavascriptObject;
import com.funfunstyle.service.js.MqttClient;

public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getSimpleName();
    private WebView webView;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }
            switch (BroadcastAction.valueOf(intent.getAction())) {
                case MESSAGE:
                    String data = intent.getStringExtra("data");
                    Log.i(TAG, "message: " + data);
                    String js = "top.raiseEvent('onMessage', '" + data + "')";
                    webView.evaluateJavascript(js, null);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        webView = (WebView) findViewById(R.id.web_view);
        initWebView();
        registerReceiver(broadcastReceiver, new IntentFilter(BroadcastAction.MESSAGE.name()));
        AppUtil.checkPermission(this, Manifest.permission.CAMERA);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                moveTaskToBack(true);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onPause() {
        super.onPause();
        //webView.onPause();
        //webView.pauseTimers();
    }

    @Override
    public void onResume() {
        super.onResume();
        //webView.resumeTimers();
        //webView.onResume();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private void initWebView() {
        webView.setWebChromeClient(new WebChromeClient());
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //view.loadUrl(url);
                return false;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }
        });
        webView.loadUrl("file:///android_asset/service-mobile/iframe.html");
        webView.addJavascriptInterface(new JavascriptObject(MainActivity.this), JavascriptObject.getName());
        webView.addJavascriptInterface(new MqttClient(MainActivity.this), MqttClient.getName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_SCANNING_QR_CODE:
                handleQrCodeScanningResult(resultCode, data);
                break;
        }
    }

    private void handleQrCodeScanningResult(int resultCode, Intent data) {
        if (data != null) {
            if (resultCode == Constants.RESULT_CODE_SUCCESS) {
                String result = data.getStringExtra("result");
                String js = "top.raiseEvent('onQrCodeScanSuccess', '" + result + "')";
                webView.evaluateJavascript(js, null);
            } else {
                Toast.makeText(this, data.getStringExtra("message"), Toast.LENGTH_LONG);
            }
        }
    }

}
