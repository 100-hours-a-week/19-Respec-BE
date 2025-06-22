package kakaotech.bootcamp.respec.specranking.domain.chat.handler;

import static kakaotech.bootcamp.respec.specranking.domain.common.type.ChatStatus.SENT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kakaotech.bootcamp.respec.specranking.domain.chat.dto.produce.ChatProduceDto;
import kakaotech.bootcamp.respec.specranking.domain.chat.dto.request.SocketChatSendRequest;
import kakaotech.bootcamp.respec.specranking.global.util.IPService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<Long, WebSocketSession> userSessionMap = new ConcurrentHashMap<>();
    private final KafkaTemplate<String, ChatProduceDto> chatMessageKafkaTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final IPService ipService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String privateAddress = ipService.loadEC2PrivateAddress();
        log.info("Connected to EC2 with private address {}", privateAddress);

        Long userId = (Long) session.getAttributes().get("userId");

        redisTemplate.opsForValue().set("chat:user:" + userId, privateAddress);
        userSessionMap.put(userId, session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        SocketChatSendRequest incomingMessage = parseJsonMessage(payload);
        if (incomingMessage == null) {
            session.sendMessage(new TextMessage("Error: json parsing error."));
            return;
        }

        String validationError = validateRequestData(incomingMessage);
        if (validationError != null) {
            session.sendMessage(new TextMessage("Error: " + validationError));
            return;
        }

        Long senderId = validateExistsUser(session);
        if (senderId == null) {
            session.sendMessage(new TextMessage("Error: Invalid type session."));
            return;
        }

        processMessage(incomingMessage, senderId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        userSessionMap.remove(userId);
        redisTemplate.delete("chat:user:" + userId);
    }

    public Map<Long, WebSocketSession> getUserSessionMap() {
        return userSessionMap;
    }

    public WebSocketSession getSessionByUserId(Long userId) {
        return userSessionMap.get(userId);
    }

    public void removeSessionByUserId(Long userId) {
        userSessionMap.remove(userId);
    }

    private String generateKeyForSequence(Long senderId, Long receiverId) {
        if (senderId < receiverId) {
            return senderId + "_" + receiverId;
        } else {
            return receiverId + "_" + senderId;
        }
    }

    private SocketChatSendRequest parseJsonMessage(String payload) {
        try {
            return objectMapper.readValue(payload, SocketChatSendRequest.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    private String validateRequestData(SocketChatSendRequest request) {
        Set<ConstraintViolation<SocketChatSendRequest>> violations = validator.validate(request);

        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder("Validation failed: ");
            for (ConstraintViolation<SocketChatSendRequest> violation : violations) {
                sb.append(violation.getPropertyPath()).append(" ").append(violation.getMessage()).append("; ");
            }
            return sb.toString();
        }

        return null;
    }

    private Long validateExistsUser(WebSocketSession session) {
        Object senderIdObj = session.getAttributes().get("userId");

        if (!(senderIdObj instanceof Long)) {
            log.warn("invalid type userId in WebSocket session");
            return null;
        }

        return (Long) senderIdObj;
    }

    private void processMessage(SocketChatSendRequest incomingMessage, Long senderId) {
        Long receiverId = incomingMessage.getReceiverId();
        String content = incomingMessage.getContent();
        String idempotentKey = UUID.randomUUID().toString();

        ChatProduceDto chatProduceDto = new ChatProduceDto(idempotentKey, senderId, receiverId, content, SENT);
        String key = generateKeyForSequence(senderId, receiverId);

        chatMessageKafkaTemplate.send("chat", key, chatProduceDto).whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Kafka SEND FAILED topic=chat key={} payload={} error={}",
                        key, chatProduceDto, ex.getMessage(), ex);
            } else {
                log.debug("Kafka SEND OK topic={} partition={} offset={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
