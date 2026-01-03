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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nguyenanhbinh.lab306new.model.Telemetry;
import com.nguyenanhbinh.lab306new.repository.DeviceRepository;
import com.nguyenanhbinh.lab306new.service.PowerDataService;
import com.nguyenanhbinh.lab306new.service.TelemetryService;
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

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class MqttConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(MqttConfig.class);

    /* ================= ENV ================= */
    private static final String BROKER_URL = System.getenv().getOrDefault("MQTT_BROKER_URL", "tcp://localhost:1883");

    private static final String MQTT_USERNAME = System.getenv().getOrDefault("MQTT_USERNAME", "");

    private static final String MQTT_PASSWORD = System.getenv().getOrDefault("MQTT_PASSWORD", "");

    private static final String CLIENT_ID = "spring-" + System.currentTimeMillis();

    /* ================= TOPIC PATTERN ================= */
    private static final Pattern DEVICE_TOPIC_PATTERN = Pattern.compile("^device/(\\d+)/data$");

    private final TelemetryService telemetryService;
    private final DeviceRepository deviceRepository;
    private final PowerDataService powerDataService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MqttConfig(
            TelemetryService telemetryService,
            DeviceRepository deviceRepository,
            PowerDataService powerDataService) {
        this.telemetryService = telemetryService;
        this.deviceRepository = deviceRepository;
        this.powerDataService = powerDataService;
    }

    /* ================= MQTT FACTORY ================= */

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();

        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { BROKER_URL });
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(30);

        if (!MQTT_USERNAME.isBlank()) {
            options.setUserName(MQTT_USERNAME);
            options.setPassword(MQTT_PASSWORD.toCharArray());
        }

        factory.setConnectionOptions(options);

        LOGGER.info("MQTT connecting to {}", BROKER_URL);
        return factory;
    }

    /* ================= INBOUND ================= */

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MqttPahoMessageDrivenChannelAdapter inbound() {
        String[] topics = {
                "device/+/data",
                "device/power"
        };

        MqttPahoMessageDrivenChannelAdapter adapter = new MqttPahoMessageDrivenChannelAdapter(
                CLIENT_ID + "_in",
                mqttClientFactory(),
                topics);

        adapter.setQos(1);
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setOutputChannel(mqttInputChannel());

        return adapter;
    }

    /* ================= MESSAGE HANDLER ================= */

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttInboundHandler() {
        return message -> {
            String topic = (String) message.getHeaders().get("mqtt_receivedTopic");
            String payload = message.getPayload().toString();

            LOGGER.info("MQTT IN | topic={} payload={}", topic, payload);

            if ("device/power".equals(topic)) {
                handlePowerTopic(payload);
                return;
            }

            Long deviceId = resolveDeviceId(topic);
            if (deviceId == null) {
                LOGGER.warn("Không xác định được deviceId cho topic {}", topic);
                return;
            }

            Telemetry telemetry = new Telemetry();
            telemetry.setDeviceId(deviceId);
            telemetry.setPayload(payload);

            telemetryService.saveTelemetry(telemetry);
        };
    }

    /* ================= POWER ================= */

    private void handlePowerTopic(String json) {
        try {
            Map<String, Object> data = objectMapper.readValue(json, Map.class);

            Double current = ((Number) data.get("current")).doubleValue();
            Double power = ((Number) data.get("power")).doubleValue();
            Integer relay = ((Number) data.get("relay")).intValue();

            powerDataService.savePowerData(current, power, relay);

            LOGGER.info("POWER | I={}A P={}W Relay={}", current, power, relay);
        } catch (Exception e) {
            LOGGER.error("Parse device/power failed: {}", json, e);
        }
    }

    /* ================= OUTBOUND ================= */

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(CLIENT_ID + "_out", mqttClientFactory());
        handler.setAsync(true);
        handler.setDefaultTopic("device/control");
        return handler;
    }

    /* ================= DEVICE RESOLVE ================= */

    private Long resolveDeviceId(String topic) {
        Matcher matcher = DEVICE_TOPIC_PATTERN.matcher(topic);
        if (matcher.matches()) {
            return Long.parseLong(matcher.group(1));
        }

        List<com.nguyenanhbinh.lab306new.model.Device> devices = deviceRepository.findByTopic(topic);

        if (devices.size() == 1) {
            return devices.get(0).getId();
        }

        if (devices.size() > 1) {
            LOGGER.error("Trùng topic {} cho {} devices", topic, devices.size());
        }

        return null;
    }
}
