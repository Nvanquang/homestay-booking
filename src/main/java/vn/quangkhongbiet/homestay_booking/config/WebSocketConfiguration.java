package vn.quangkhongbiet.homestay_booking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Cấu hình CORS đúng cách
        registry.addEndpoint("/chat")
                .setAllowedOriginPatterns("http://localhost:3000", "http://localhost:4173", "http://localhost:5173");
                        
        // Endpoint với SockJS fallback
        registry.addEndpoint("/chat")
                .setAllowedOriginPatterns("http://localhost:3000", "http://localhost:4173", "http://localhost:5173")
                .withSockJS();
    }
}