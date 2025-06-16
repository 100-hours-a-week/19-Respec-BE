package kakaotech.bootcamp.respec.specranking.domain.chat.config;

import kakaotech.bootcamp.respec.specranking.domain.chat.handler.ChatWebSocketHandler;
import kakaotech.bootcamp.respec.specranking.domain.chat.interceptor.WebsocketHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final WebsocketHandshakeInterceptor handshakeInterceptor;

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatWebSocketHandler, "/ws/chat")
                .addInterceptors(handshakeInterceptor)
                .setAllowedOrigins(frontendBaseUrl);
    }
}
