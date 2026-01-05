package com.nguyenanhbinh.lab306new.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * ✅ Gửi telemetry realtime (GIỮ NGUYÊN - cho device cũ)
     */
    public void sendTelemetryUpdate(Long deviceId, String payload) {
        String destination = "/topic/device/" + deviceId + "/telemetry";
        messagingTemplate.convertAndSend(destination, payload);
    }

    /**
     * ✅ Gửi power data realtime (CHO ESP32)
     * Destination: /topic/power/realtime
     */
    public void sendPowerDataUpdate(String jsonPayload) {
        String destination = "/topic/power/realtime";
        messagingTemplate.convertAndSend(destination, jsonPayload);
    }
}