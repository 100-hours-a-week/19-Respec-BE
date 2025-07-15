package kakaotech.bootcamp.respec.specranking.domain.chat.chat.handler;

import static kakaotech.bootcamp.respec.specranking.global.common.type.ChatStatus.SENT;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.adapter.out.dto.ChatSessionRedisValue;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.dto.request.SocketChatSendRequest;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.manager.WebSocketSessionManager;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.kafka.dto.ChatProduceDto;
import kakaotech.bootcamp.respec.specranking.global.infrastructure.myserver.ip.service.IPService;
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

    private final WebSocketSessionManager webSocketSessionManager;
    private final KafkaTemplate<String, ChatProduceDto> chatMessageKafkaTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final IPService ipService;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String privateAddress = ipService.loadEC2PrivateAddress();

        Long userId = (Long) session.getAttributes().get("userId");

        ChatSessionRedisValue chatSessionRedisValue = new ChatSessionRedisValue(privateAddress, userId);
        redisTemplate.opsForValue().set("chat:user:" + userId, chatSessionRedisValue, Duration.ofHours(24));
        webSocketSessionManager.addSession(userId, session);
        log.info("userId{}가 초기 세션 연결에 성공했습니다.", userId);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        SocketChatSendRequest incomingMessage = parseJsonMessage(payload);
        if (incomingMessage == null) {
            session.sendMessage(new TextMessage("Error: json parsing error."));
            log.error("메시지의 json parsing이 실패했습니다.");
            return;
        }

        String validationError = validateRequestData(incomingMessage);
        if (validationError != null) {
            session.sendMessage(new TextMessage("Error: " + validationError));
            log.error("메시지의 검증에 실패했습니다.");
            return;
        }

        Long senderId = validateExistsUser(session);
        if (senderId == null) {
            session.sendMessage(new TextMessage("Error: Invalid type session."));
            log.error("세션의 타입이 유효하지 않습니다.");
            return;
        }

        processMessage(incomingMessage, senderId);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long userId = (Long) session.getAttributes().get("userId");
        webSocketSessionManager.removeSession(userId);
        redisTemplate.delete("chat:user:" + userId);
        log.info("userId{}의 세션 연결이 종료되었습니다.", userId);
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
        Long receiverId = incomingMessage.receiverId();
        String content = incomingMessage.content();
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
