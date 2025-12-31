
package com.nguyenanhbinh.lab306new.mqtt;

import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class MqttTopicManager {

    private final MqttPahoMessageDrivenChannelAdapter mqttAdapter;

    // lưu các topic đã subscribe
    private final Set<String> subscribedTopics = new HashSet<>();

    public MqttTopicManager(MqttPahoMessageDrivenChannelAdapter mqttAdapter) {
        this.mqttAdapter = mqttAdapter;
    }

    // subscribe nếu chưa tồn tại
    public synchronized void subscribeIfNotExists(String topic) {
        if (!subscribedTopics.contains(topic)) {
            mqttAdapter.addTopic(topic, 1);
            subscribedTopics.add(topic);
        }
    }
}
