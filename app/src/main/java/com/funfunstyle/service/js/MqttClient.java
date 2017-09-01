package com.funfunstyle.service.js;

import android.app.Activity;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.funfunstyle.service.service.PushService;

/**
 * Created by lovebing on 2017/9/1.
 */
public class MqttClient {

    private final String TAG = getClass().getSimpleName();
    private String host;
    private String port;
    private Activity activity;
    private String clientId;
    private String topic;

    public MqttClient(Activity activity) {
        this.activity = activity;
    }

    @JavascriptInterface
    public void init(String host, String port, String businessId, String clientId, String topic) {
        this.host = host;
        this.port = port;
        this.clientId = clientId;
        this.topic = topic;
        Log.i(TAG, "init => host: " + host + ", port=" + port + ", businessId: "
                + businessId + ", clientId: " + clientId + ", topic=" + topic);
    }

    @JavascriptInterface
    public void connect() {
        Log.i(TAG, "connect: " + clientId + ", " + topic);
        PushService.start(activity, host, port, clientId, topic);
    }

    @JavascriptInterface
    public void disconnect() {
        Log.i(TAG, "disconnect");
        PushService.stop(activity);
    }

    public static String getName() {
        return "androidMqtt";
    }
}
