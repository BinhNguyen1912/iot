package com.nguyenanhbinh.lab306new.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefix cho các message gửi từ server -> client
        config.enableSimpleBroker("/topic");

        // Prefix cho các message gửi từ client -> server
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint WebSocket, cho phép CORS
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");

    }
}