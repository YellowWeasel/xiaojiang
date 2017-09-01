package com.funfunstyle.service;

import org.junit.Test;

import com.ibm.mqtt.IMqttClient;
import com.ibm.mqtt.MqttClient;

/**
 * Created by lovebing on 2017/9/2.
 */
public class MqttTest {

    @Test
    public void test() throws Exception {
        IMqttClient client = MqttClient.createMqttClient("tcp://www.funfunstyle.com@1883", null);
        client.connect("06239152928551759", true, (short) (60 * 15));
    }
}
