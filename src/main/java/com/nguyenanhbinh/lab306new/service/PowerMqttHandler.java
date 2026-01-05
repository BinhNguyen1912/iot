package com.nguyenanhbinh.lab306new.service;

import com.nguyenanhbinh.lab306new.model.PowerData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

/**
 * ✅ Service xử lý dữ liệu từ ESP32
 * - Nhận current từ topic: device/current
 * - Nhận power từ topic: device/power
 * - Tự động tính relay state từ current
 * - Lưu vào DB + cache + gửi WebSocket
 */
@Service
public class PowerMqttHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(PowerMqttHandler.class);

    private final PowerDataService powerDataService;
    private final WebSocketService webSocketService;

    // ✅ Cache tạm để ghép current + power
    private final ConcurrentHashMap<String, Double> currentCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Double> powerCache = new ConcurrentHashMap<>();

    // ✅ Trạng thái relay hiện tại (tính từ current)
    private volatile Integer lastRelayState = 0;

    public PowerMqttHandler(PowerDataService powerDataService, WebSocketService webSocketService) {
        this.powerDataService = powerDataService;
        this.webSocketService = webSocketService;
    }

    /**
     * ✅ Xử lý message từ topic: device/current
     */
    public void handleCurrent(String payload) {
        try {
            Double current = Double.parseDouble(payload.trim());
            currentCache.put("latest", current);

            // ✅ Tính relay state từ dòng điện
            // Ngưỡng: > 0.1A = relay ON
            int relayState = (current > 0.1) ? 1 : 0;
            lastRelayState = relayState;

            LOGGER.info("📊 Received CURRENT: {} A (Relay: {})", current, relayState);

            // ✅ Kiểm tra xem đã có power chưa → lưu vào DB
            saveIfComplete();

        } catch (NumberFormatException e) {
            LOGGER.error("❌ Invalid current format: {}", payload);
        }
    }

    /**
     * ✅ Xử lý message từ topic: device/power
     */
    public void handlePower(String payload) {
        try {
            Double power = Double.parseDouble(payload.trim());
            powerCache.put("latest", power);

            LOGGER.info("⚡ Received POWER: {} W", power);

            // ✅ Kiểm tra xem đã có current chưa → lưu vào DB
            saveIfComplete();

        } catch (NumberFormatException e) {
            LOGGER.error("❌ Invalid power format: {}", payload);
        }
    }

    /**
     * ✅ Lưu vào DB khi đã có đủ current + power
     */
    private void saveIfComplete() {
        Double current = currentCache.get("latest");
        Double power = powerCache.get("latest");

        if (current != null && power != null) {
            // ✅ Lưu vào DB
            PowerData saved = powerDataService.savePowerData(current, power, lastRelayState);

            // ✅ Gửi realtime qua WebSocket (JSON format)
            String jsonPayload = String.format(
                    "{\"current\":%.3f,\"power\":%.1f,\"relay\":%d,\"timestamp\":\"%s\"}",
                    current, power, lastRelayState, saved.getTimestamp());

            webSocketService.sendPowerDataUpdate(jsonPayload);

            LOGGER.info("✅ Saved & sent WebSocket: I={} A, P={} W, Relay={}",
                    current, power, lastRelayState);

            // ✅ Clear cache (tùy chọn, có thể giữ để tránh mất dữ liệu)
            // currentCache.clear();
            // powerCache.clear();
        }
    }

    /**
     * ✅ Lấy relay state hiện tại
     */
    public Integer getLastRelayState() {
        return lastRelayState;
    }
}
