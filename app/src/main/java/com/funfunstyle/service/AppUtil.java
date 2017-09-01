package com.funfunstyle.service;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by lovebing on 2017/9/2.
 */
public class AppUtil {

    public static void checkPermission(Activity activity, String permission) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(activity.getApplicationInfo().targetSdkVersion >= Build.VERSION_CODES.M) {
                if(ContextCompat.checkSelfPermission(activity, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(activity, new String[]{permission}, 1);
                }
            }
        }
    }
}
