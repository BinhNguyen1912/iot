package com.nguyenanhbinh.lab306new.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * ✅ Service điều khiển relay ESP32
 * - Publish lệnh lên topic: device/light/control
 * - Payload: "1" = ON, "0" = OFF
 */
@Service
public class RelayControlService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RelayControlService.class);
    private static final String RELAY_TOPIC = "device/light/control";

    private final MqttPublisherService mqttPublisherService;

    public RelayControlService(MqttPublisherService mqttPublisherService) {
        this.mqttPublisherService = mqttPublisherService;
    }

    /**
     * ✅ Bật relay
     */
    public void turnOn() {
        mqttPublisherService.publish(RELAY_TOPIC, "1");
        LOGGER.info("🔌 RELAY COMMAND SENT: ON");
    }

    /**
     * ✅ Tắt relay
     */
    public void turnOff() {
        mqttPublisherService.publish(RELAY_TOPIC, "0");
        LOGGER.info("🔌 RELAY COMMAND SENT: OFF");
    }

    /**
     * ✅ Điều khiển relay theo state (0/1)
     */
    public void setRelay(int state) {
        if (state == 1) {
            turnOn();
        } else {
            turnOff();
        }
    }
}