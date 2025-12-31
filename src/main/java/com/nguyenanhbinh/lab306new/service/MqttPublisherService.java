package com.nguyenanhbinh.lab306new.service;

import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

@Service
public class MqttPublisherService {

    private final MessageChannel mqttOutboundChannel;

    public MqttPublisherService(MessageChannel mqttOutboundChannel) {
        this.mqttOutboundChannel = mqttOutboundChannel;
    }

    // gửi dữ liệu MQTT
    public void publish(String topic, String payload) {

        mqttOutboundChannel.send(
                MessageBuilder.withPayload(payload)
                        .setHeader("mqtt_topic", topic)
                        .build());
    }
}
