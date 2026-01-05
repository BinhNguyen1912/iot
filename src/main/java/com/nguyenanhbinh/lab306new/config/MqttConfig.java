// package com.nguyenanhbinh.lab306new.config;

// import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.integration.annotation.ServiceActivator;
// import org.springframework.integration.channel.DirectChannel;
// import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
// import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
// import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
// import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
// import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
// import org.springframework.messaging.MessageChannel;
// import org.springframework.messaging.MessageHandler;

// import com.nguyenanhbinh.lab306new.model.Telemetry;
// import com.nguyenanhbinh.lab306new.service.TelemetryService;
// import com.nguyenanhbinh.lab306new.service.PowerDataService;
// import com.nguyenanhbinh.lab306new.repository.DeviceRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import com.fasterxml.jackson.databind.ObjectMapper;

// import java.util.regex.Matcher;
// import java.util.regex.Pattern;
// import java.util.Map;

// @Configuration
// public class MqttConfig {

//     private final String brokerUrl = "tcp://localhost:1883";
//     private final String clientId = "spring-boot-client";
//     private static final Pattern DEVICE_TOPIC_PATTERN = Pattern.compile("^device/(\\d+)/data$");
//     private static final Logger LOGGER = LoggerFactory.getLogger(MqttConfig.class);

//     private final TelemetryService telemetryService;
//     private final DeviceRepository deviceRepository;
//     private final PowerDataService powerDataService; // ✅ THÊM MỚI
//     private final ObjectMapper objectMapper = new ObjectMapper();

//     @Autowired
//     public MqttConfig(TelemetryService telemetryService,
//             DeviceRepository deviceRepository,
//             PowerDataService powerDataService) { // ✅ INJECT
//         this.telemetryService = telemetryService;
//         this.deviceRepository = deviceRepository;
//         this.powerDataService = powerDataService;
//     }

//     @Bean
//     public MqttPahoClientFactory mqttClientFactory() {
//         DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
//         MqttConnectOptions options = new MqttConnectOptions();
//         options.setServerURIs(new String[] { brokerUrl });
//         factory.setConnectionOptions(options);
//         return factory;
//     }

//     @Bean
//     public MessageChannel mqttInputChannel() {
//         return new DirectChannel();
//     }

//     @Bean
//     public MqttPahoMessageDrivenChannelAdapter inbound() {
//         // ✅ SUBSCRIBE 2 TOPICS: device/+/data VÀ device/power
//         String[] topics = { "device/+/data", "device/power" };

//         MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
//                 clientId + "_in", mqttClientFactory(), topics);
//         adapter.setCompletionTimeout(5000);
//         adapter.setConverter(new DefaultPahoMessageConverter());
//         adapter.setQos(1);
//         adapter.setOutputChannel(mqttInputChannel());
//         return adapter;
//     }

//     @Bean
//     @ServiceActivator(inputChannel = "mqttInputChannel")
//     public MessageHandler handler() {
//         return message -> {
//             String topic = message.getHeaders().get("mqtt_receivedTopic").toString();
//             String payloadStr = message.getPayload().toString();

//             LOGGER.info("Received MQTT: topic={}, payload={}", topic, payloadStr);

//             // ✅ XỬ LÝ TOPIC MỚI: device/power
//             if ("device/power".equals(topic)) {
//                 handlePowerTopic(payloadStr);
//                 return;
//             }

//             // Xử lý topic cũ: device/{id}/data
//             Long deviceId = resolveDeviceId(topic);
//             if (deviceId == null) {
//                 LOGGER.warn("Bỏ qua telemetry vì không xác định được deviceId cho topic: {}", topic);
//                 return;
//             }

//             Telemetry telemetry = new Telemetry();
//             telemetry.setDeviceId(deviceId);
//             telemetry.setPayload(payloadStr);

//             telemetryService.saveTelemetry(telemetry);
//         };
//     }

//     /**
//      * ✅ HÀM MỚI: Xử lý topic device/power
//      */
//     private void handlePowerTopic(String jsonPayload) {
//         try {
//             // Parse JSON: {"current": 0.082, "power": 18.0, "relay": 1}
//             Map<String, Object> data = objectMapper.readValue(jsonPayload, Map.class);

//             Double current = ((Number) data.get("current")).doubleValue();
//             Double power = ((Number) data.get("power")).doubleValue();
//             Integer relay = ((Number) data.get("relay")).intValue();

//             // Lưu vào DB và cache
//             powerDataService.savePowerData(current, power, relay);

//             LOGGER.info("Power data saved: I={} A, P={} W, Relay={}", current, power, relay);

//         } catch (Exception e) {
//             LOGGER.error("Lỗi parse JSON từ topic device/power: {}", jsonPayload, e);
//         }
//     }

//     @Bean
//     @ServiceActivator(inputChannel = "mqttOutboundChannel")
//     public MessageHandler mqttOutbound() {
//         MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId + "_out", mqttClientFactory());
//         messageHandler.setAsync(true);
//         messageHandler.setDefaultTopic("/test/topic");
//         return messageHandler;
//     }

//     @Bean
//     public MessageChannel mqttOutboundChannel() {
//         return new DirectChannel();
//     }

//     private Long resolveDeviceId(String topic) {
//         Matcher matcher = DEVICE_TOPIC_PATTERN.matcher(topic);
//         if (matcher.matches()) {
//             return Long.parseLong(matcher.group(1));
//         }
//         java.util.List<com.nguyenanhbinh.lab306new.model.Device> devices = deviceRepository.findByTopic(topic);
//         if (devices.isEmpty()) {
//             return null;
//         }
//         if (devices.size() > 1) {
//             LOGGER.error("Có {} thiết bị cùng topic '{}', không biết chọn thiết bị nào.", devices.size(), topic);
//             return null;
//         }
//         return devices.get(0).getId();
//     }
// }
package com.nguyenanhbinh.lab306new.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import com.nguyenanhbinh.lab306new.service.PowerDataService;
import com.nguyenanhbinh.lab306new.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class MqttConfig {

    private final String brokerUrl = "tcp://localhost:1883";
    private final String clientId = "spring-boot-client";
    private static final Logger LOGGER = LoggerFactory.getLogger(MqttConfig.class);

    // ✅ Topics ESP32
    private static final String TOPIC_CURRENT = "device/current";
    private static final String TOPIC_POWER = "device/power";

    private final PowerDataService powerDataService;
    private final WebSocketService webSocketService;

    // ✅ Cache để ghép current + power
    private final ConcurrentHashMap<String, Double> currentCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Double> powerCache = new ConcurrentHashMap<>();
    private volatile Integer lastRelayState = 0;

    @Autowired
    public MqttConfig(PowerDataService powerDataService, WebSocketService webSocketService) {
        this.powerDataService = powerDataService;
        this.webSocketService = webSocketService;
        LOGGER.info("🚀 MqttConfig initialized with services");
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { brokerUrl });
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        factory.setConnectionOptions(options);
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter inbound() {
        String[] topics = { TOPIC_CURRENT, TOPIC_POWER };

        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                clientId + "_in", mqttClientFactory(), topics);

        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());

        LOGGER.info("✅ MQTT Adapter subscribed to: {}", String.join(", ", topics));

        return adapter;
    }

    /**
     * ✅ Handler xử lý message (LOGIC TRỰC TIẾP - KHÔNG QUA SERVICE RIÊNG)
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return message -> {
            try {
                String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
                String payload = message.getPayload().toString();

                LOGGER.info("📥 MQTT Message - Topic: {}, Payload: {}", topic, payload);

                // ✅ Xử lý CURRENT
                if (TOPIC_CURRENT.equals(topic)) {
                    handleCurrent(payload);
                }
                // ✅ Xử lý POWER
                else if (TOPIC_POWER.equals(topic)) {
                    handlePower(payload);
                } else {
                    LOGGER.warn("⚠️ Unknown topic: {}", topic);
                }

            } catch (Exception e) {
                LOGGER.error("❌ Error handling MQTT message: {}", e.getMessage(), e);
            }
        };
    }

    /**
     * ✅ Xử lý message từ device/current
     */
    private void handleCurrent(String payload) {
        try {
            Double current = Double.parseDouble(payload.trim());
            currentCache.put("latest", current);

            // Tính relay state
            int relayState = (current > 0.1) ? 1 : 0;
            lastRelayState = relayState;

            LOGGER.info("📊 CURRENT: {} A (Relay: {})", current, relayState);

            // Kiểm tra xem đã có power chưa
            saveIfComplete();

        } catch (NumberFormatException e) {
            LOGGER.error("❌ Invalid current format: {}", payload);
        }
    }

    /**
     * ✅ Xử lý message từ device/power
     */
    private void handlePower(String payload) {
        try {
            Double power = Double.parseDouble(payload.trim());
            powerCache.put("latest", power);

            LOGGER.info("⚡ POWER: {} W", power);

            // Kiểm tra xem đã có current chưa
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
            LOGGER.info("💾 Saving: I={} A, P={} W, Relay={}", current, power, lastRelayState);

            try {
                // ✅ Lưu vào DB
                var saved = powerDataService.savePowerData(current, power, lastRelayState);

                LOGGER.info("✅ SAVED TO DB - ID: {}", saved.getId());

                // ✅ Gửi WebSocket
                String jsonPayload = String.format(
                        "{\"current\":%.3f,\"power\":%.1f,\"relay\":%d,\"timestamp\":\"%s\"}",
                        current, power, lastRelayState, saved.getTimestamp());

                webSocketService.sendPowerDataUpdate(jsonPayload);

                LOGGER.info("📤 WebSocket sent");

            } catch (Exception e) {
                LOGGER.error("❌ Error saving: {}", e.getMessage(), e);
            }
        } else {
            LOGGER.debug("⏳ Waiting - Current: {}, Power: {}", current, power);
        }
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(
                clientId + "_out", mqttClientFactory());
        messageHandler.setAsync(true);
        messageHandler.setDefaultQos(1);
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }
}