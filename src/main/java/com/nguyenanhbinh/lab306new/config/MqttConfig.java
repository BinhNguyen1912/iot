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

    // ✅ DÙNG MQTT BROKER CỦA BẠN TRÊN RAILWAY
    private final String brokerUrl = "tcp://trolley.proxy.rlwy.net:46563";
    private final String clientId = "spring-boot-client";
    private static final Logger LOGGER = LoggerFactory.getLogger(MqttConfig.class);

    private static final String TOPIC_CURRENT = "device/current";
    private static final String TOPIC_POWER = "device/power";

    private final PowerDataService powerDataService;
    private final WebSocketService webSocketService;

    private final ConcurrentHashMap<String, Double> currentCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Double> powerCache = new ConcurrentHashMap<>();
    private volatile Integer lastRelayState = 0;

    @Autowired
    public MqttConfig(PowerDataService powerDataService, WebSocketService webSocketService) {
        this.powerDataService = powerDataService;
        this.webSocketService = webSocketService;
        LOGGER.info("🚀 MqttConfig initialized");
        LOGGER.info("📍 Broker: {}", brokerUrl);
    }

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[] { brokerUrl });
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(60);
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

        LOGGER.info("✅ Subscribed: {}", String.join(", ", topics));

        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return message -> {
            try {
                String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
                String payload = message.getPayload().toString();

                LOGGER.info("📥 Topic: {}, Payload: {}", topic, payload);

                if (TOPIC_CURRENT.equals(topic)) {
                    handleCurrent(payload);
                } else if (TOPIC_POWER.equals(topic)) {
                    handlePower(payload);
                } else {
                    LOGGER.warn("⚠️ Unknown topic: {}", topic);
                }

            } catch (Exception e) {
                LOGGER.error("❌ Handler error: {}", e.getMessage(), e);
            }
        };
    }

    private void handleCurrent(String payload) {
        try {
            Double current = Double.parseDouble(payload.trim());
            currentCache.put("latest", current);

            int relayState = (current > 0.1) ? 1 : 0;
            lastRelayState = relayState;

            LOGGER.info("📊 I={} A, Relay={}", current, relayState);
            saveIfComplete();

        } catch (NumberFormatException e) {
            LOGGER.error("❌ Invalid current: {}", payload);
        }
    }

    private void handlePower(String payload) {
        try {
            Double power = Double.parseDouble(payload.trim());
            powerCache.put("latest", power);

            LOGGER.info("⚡ P={} W", power);
            saveIfComplete();

        } catch (NumberFormatException e) {
            LOGGER.error("❌ Invalid power: {}", payload);
        }
    }

    private void saveIfComplete() {
        Double current = currentCache.get("latest");
        Double power = powerCache.get("latest");

        if (current != null && power != null) {
            LOGGER.info("💾 Saving: I={} A, P={} W, R={}", current, power, lastRelayState);

            try {
                var saved = powerDataService.savePowerData(current, power, lastRelayState);
                LOGGER.info("✅ Saved ID: {}", saved.getId());

                String json = String.format(
                        "{\"current\":%.3f,\"power\":%.1f,\"relay\":%d,\"timestamp\":\"%s\"}",
                        current, power, lastRelayState, saved.getTimestamp());

                webSocketService.sendPowerDataUpdate(json);
                LOGGER.info("📤 WebSocket sent");

            } catch (Exception e) {
                LOGGER.error("❌ Save error: {}", e.getMessage(), e);
            }
        }
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler handler = new MqttPahoMessageHandler(
                clientId + "_out", mqttClientFactory());
        handler.setAsync(true);
        handler.setDefaultQos(1);
        return handler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }
}