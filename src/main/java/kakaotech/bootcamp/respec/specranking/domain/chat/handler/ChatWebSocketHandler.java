package kakaotech.bootcamp.respec.specranking.domain.chat.handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import kakaotech.bootcamp.respec.specranking.global.util.ServerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final StringRedisTemplate redisTemplate;
    private final Map<Long, WebSocketSession> userSessionMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String privateAddress = ServerUtils.getPrivateAddress();

        Long userId = (Long) session.getAttributes().get("userId");
        if (userId == null) {
            session.close();
            return;
        }

        redisTemplate.opsForValue().set("chat:user:" + userId, privateAddress);
        userSessionMap.put(userId, session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        if (userId != null) {
            userSessionMap.remove(userId);
            redisTemplate.delete("chat:user:" + userId);
        }
    }

    public WebSocketSession getSessionByUserId(Long userId) {
        return userSessionMap.get(userId);
    }
}
