package com.funfunstyle.service;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class ScanActivity extends AppCompatActivity implements QRCodeView.Delegate {

    private final String TAG = getClass().getSimpleName();
    private ZXingView zXingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        setTitle(getString(R.string.title_scanning_qrcode));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        zXingView = (ZXingView) findViewById(R.id.zxingview);
        zXingView.setDelegate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        zXingView.startCamera();
        zXingView.showScanRect();
        zXingView.startSpot();
    }

    @Override
    protected void onStop() {
        zXingView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        zXingView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Intent data = new Intent();
        data.putExtra("result", result);
        setResult(Constants.RESULT_CODE_SUCCESS, data);
        finish();
    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        String message = "打开相机出错";
        Intent data = new Intent();
        data.putExtra("message", message);
        setResult(Constants.RESULT_CODE_ERROR, data);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
