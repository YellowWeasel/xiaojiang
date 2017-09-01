package com.funfunstyle.service.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import com.funfunstyle.service.BroadcastAction;
import com.funfunstyle.service.R;
import com.ibm.mqtt.IMqttClient;
import com.ibm.mqtt.MqttAdvancedCallback;
import com.ibm.mqtt.MqttClient;
import com.ibm.mqtt.MqttException;

public class PushService extends Service {

    private final String TAG = getClass().getSimpleName();

    private Notification notification;
    private NotificationManager notificationManager;

    private static Context mContext;
    private volatile boolean runnig = false;
    private enum Action {
        start,
        stop
    }

    private IMqttClient mqttClient;
    private String host;
    private String port;
    private String clientId;
    private String topic;

    public PushService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        host = intent.getStringExtra("host");
        port = intent.getStringExtra("port");
        clientId = intent.getStringExtra("clientId");
        topic = intent.getStringExtra("topic");

        new Thread(() -> {
            switch (Action.valueOf(intent.getAction())) {
                case start:
                    connect();
                    break;
                case stop:
                    disconnect();
                    break;
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        disconnect();
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private void intMqttClient() throws MqttException {
        if (mqttClient == null) {
            String serviceURI = "tcp://" + host + "@" + port;
            mqttClient = MqttClient.createMqttClient(serviceURI, null);
            Log.i(TAG, "init|serviceURI=" + serviceURI + "|clientId=" + clientId);
            mqttClient.registerSimpleHandler(new MqttAdvancedCallback() {
                @Override
                public void published(int i) {

                }

                @Override
                public void subscribed(int i, byte[] bytes) {
                    Log.i(TAG, "subscribed");
                }

                @Override
                public void unsubscribed(int i) {

                }

                @Override
                public void connectionLost() throws Exception {
                    Log.i(TAG, "connectionLost");
                    if (runnig) {
                        connect();
                    }
                }

                @Override
                public void publishArrived(String topicName, byte[] payload, int qos, boolean retained)
                        throws Exception {
                    Log.i(TAG, "publishArrived|" + topicName + "|i=" + qos);
                    Intent intent = new Intent();
                    intent.setAction(BroadcastAction.MESSAGE.name());
                    intent.putExtra("data", new String(payload, "UTF-8"));
                    mContext.sendBroadcast(intent);
                    notification = new Notification.Builder(getApplication())
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setContentTitle(getString(R.string.new_message))
                            .setContentText(getString(R.string.please_pay_attention))
                            .setSubText(getString(R.string.app_name))
                            .setVibrate(new long[]{0, 100, 1000})
                            .build();
                    notification.sound = Uri.parse("Android.resource://" + getPackageName() + "/" + R.raw.new_call);
                    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(1, notification);
                }
            });

        }
    }

    private void connect() {
        Log.i(TAG, "connect");
        try {
            runnig = true;
            intMqttClient();
            if (!mqttClient.isConnected()) {
                Log.i(TAG, "do connect|clientId=" + clientId);
                mqttClient.connect(clientId, true, (short) (60 * 15));
                Log.i(TAG, "subscribe");
                mqttClient.subscribe(new String[]{topic + "/" + clientId}, new int[]{0});
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            runnig = false;
        }
    }

    private void disconnect() {
        Log.i(TAG, "disconnect");
        runnig = false;
        try {
            mqttClient.disconnect();
            mqttClient = null;
        } catch (Exception e) {
            Log.i(TAG, e.getMessage(), e);
        }
    }

    public static void start(Context context, String host, String port, String clientId, String topic) {
        mContext = context;
        Intent intent = new Intent(context, PushService.class);
        intent.setAction(Action.start.name());
        intent.putExtra("host", host);
        intent.putExtra("port", port);
        intent.putExtra("clientId", clientId);
        intent.putExtra("topic", topic);
        context.startService(intent);
    }

    public static void stop(Context context) {
        mContext = context;
        Intent intent = new Intent(context, PushService.class);
        intent.setAction(Action.stop.name());
        context.startService(intent);
    }
}
