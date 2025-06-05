package kakaotech.bootcamp.respec.specranking.domain.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kakaotech.bootcamp.respec.specranking.domain.chat.dto.produce.ChatProduceDto;
import kakaotech.bootcamp.respec.specranking.global.util.ServerUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<Long, WebSocketSession> userSessionMap = new ConcurrentHashMap<>();
    private final KafkaTemplate<String, ChatProduceDto> chatMessageKafkaTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String privateAddress = ServerUtils.getPrivateAddress();

        Long userId = (Long) session.getAttributes().get("userId");

        redisTemplate.opsForValue().set("chat:user:" + userId, privateAddress);
        userSessionMap.put(userId, session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        Map<String, Object> incomingMessage = objectMapper.readValue(payload, Map.class);

        Object senderIdObj = session.getAttributes().get("userId");
        Object receiverIdObj = incomingMessage.get("receiverId");
        String content = (String) incomingMessage.get("content");

        String senderId = String.valueOf(senderIdObj);
        String receiverId = String.valueOf(receiverIdObj);

        String idempotentKey = UUID.randomUUID().toString();

        ChatProduceDto chatProduceDto = new ChatProduceDto(idempotentKey, senderId, receiverId, content, "SENT");

        String key = generateKeyForSequence(senderId, receiverId);

        chatMessageKafkaTemplate.send("chat", key, chatProduceDto);

    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        userSessionMap.remove(userId);
        redisTemplate.delete("chat:user:" + userId);
    }

    public WebSocketSession getSessionByUserId(Long userId) {
        return userSessionMap.get(userId);
    }


    private String generateKeyForSequence(String senderId, String receiverId) {
        long senderIdLong = Long.parseLong(senderId);
        long receiverIdLong = Long.parseLong(receiverId);

        if (senderIdLong < receiverIdLong) {
            return senderId + "_" + receiverId;
        } else {
            return receiverId + "_" + senderId;
        }
    }
}
