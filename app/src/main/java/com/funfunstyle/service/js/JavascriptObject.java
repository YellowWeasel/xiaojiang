package com.funfunstyle.service.js;

import android.app.Activity;
import android.content.Intent;
import android.webkit.JavascriptInterface;

import com.funfunstyle.service.Constants;
import com.funfunstyle.service.ScanActivity;

/**
 * Created by lovebing on 2017/9/1.
 */

public class JavascriptObject {

    private Activity activity;

    public JavascriptObject(Activity activity) {
        this.activity = activity;
    }

    public static String getName() {
        return "android";
    }

    @JavascriptInterface
    public void scanQrCode(){
        Intent intent = new Intent(activity, ScanActivity.class);
        activity.startActivityForResult(intent, Constants.REQUEST_CODE_SCANNING_QR_CODE);
    }
}
