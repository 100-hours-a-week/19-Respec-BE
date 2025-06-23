package kakaotech.bootcamp.respec.specranking.domain.chat.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.dto.consume.ChatRelayConsumeDto;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.dto.response.ChatRelayResponse;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.manager.WebSocketSessionManager;
import kakaotech.bootcamp.respec.specranking.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final WebSocketSessionManager webSocketSessionManager;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final NotificationService notificationService;

    public void sendMessageToUser(ChatRelayConsumeDto chatRelayDto) throws IOException {
        Long receiverId = chatRelayDto.receiverId();

        WebSocketSession session = webSocketSessionManager.getSessionByUserId(receiverId);

        if (session == null) {
            notificationService.createChatNotificationIfNotExists(receiverId);
            return;
        }

        ChatRelayResponse messageToClient = new ChatRelayResponse(chatRelayDto.senderId(), chatRelayDto.receiverId(),
                chatRelayDto.content());

        String messageJson = objectMapper.writeValueAsString(messageToClient);

        try {
            session.sendMessage(new TextMessage(messageJson));
        } catch (IOException | IllegalStateException e) {
            if (session.isOpen()) {
                session.close(CloseStatus.SESSION_NOT_RELIABLE);
            }
            redisTemplate.delete("chat:user:" + receiverId);
            webSocketSessionManager.removeSessionByUserId(receiverId);
            notificationService.createChatNotificationIfNotExists(receiverId);
        }

    }
}
