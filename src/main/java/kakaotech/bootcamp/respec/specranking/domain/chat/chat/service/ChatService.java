package kakaotech.bootcamp.respec.specranking.domain.chat.chat.service;

import static kakaotech.bootcamp.respec.specranking.global.infrastructure.redis.constant.CacheManagerConstant.CHAT_ENTER_USER_PREFIX;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.dto.request.ChatRelayRequestDto;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.dto.response.ChatRelayResponse;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.manager.WebSocketSessionManager;
import kakaotech.bootcamp.respec.specranking.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final WebSocketSessionManager webSocketSessionManager;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final NotificationService notificationService;

    public void sendMessageToUser(ChatRelayRequestDto chatRelayDto) throws IOException {
        Long receiverId = chatRelayDto.receiverId();
        Long senderId = chatRelayDto.senderId();

        WebSocketSession session = webSocketSessionManager.getSessionByUserId(receiverId);

        if (session == null) {
            log.info("userId{}의 세션이 존재하지 않습니다.", receiverId);
            notificationService.createChatNotificationIfNotExists(receiverId);
            return;
        }

        log.info("userId{}의 세션이 존재합니다.", receiverId);

        ChatRelayResponse messageToClient = new ChatRelayResponse(chatRelayDto.senderId(), chatRelayDto.receiverId(),
                chatRelayDto.content());

        String messageJson = objectMapper.writeValueAsString(messageToClient);

        try {
            session.sendMessage(new TextMessage(messageJson));
            log.info("userId{}가 userId{}에게 세션 메시지 전송에 성공했습니다.", senderId, receiverId);
        } catch (IOException | IllegalStateException e) {
            if (session.isOpen()) {
                session.close(CloseStatus.SESSION_NOT_RELIABLE);
            }
            log.info("userId{}가 userId{}에게 세션 메시지 전송에 실패했습니다.", senderId, receiverId);
            log.error(e.getMessage());
            redisTemplate.delete(CHAT_ENTER_USER_PREFIX + receiverId);
            webSocketSessionManager.removeSessionByUserId(receiverId);
            notificationService.createChatNotificationIfNotExists(receiverId);
        }

    }
}
