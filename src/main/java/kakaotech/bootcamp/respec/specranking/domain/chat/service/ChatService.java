package kakaotech.bootcamp.respec.specranking.domain.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import kakaotech.bootcamp.respec.specranking.domain.chat.dto.consume.ChatRelayConsumeDto;
import kakaotech.bootcamp.respec.specranking.domain.chat.dto.response.ChatRelayResponse;
import kakaotech.bootcamp.respec.specranking.domain.chat.handler.ChatWebSocketHandler;
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

    private final ChatWebSocketHandler chatWebSocketHandler;
    private final ObjectMapper objectMapper;
    private final RedisTemplate<String, Object> redisTemplate;
    private final NotificationService notificationService;

    public void sendMessageToUser(ChatRelayConsumeDto chatRelayDto) throws IOException {
        Long receiverId = chatRelayDto.getReceiverId();

        WebSocketSession session = chatWebSocketHandler.getSessionByUserId(receiverId);

        if (session == null) {
            notificationService.createChatNotificationIfNotExists(receiverId);
            return;
        }

        ChatRelayResponse messageToClient = ChatRelayResponse.builder()
                .senderId(chatRelayDto.getSenderId())
                .receiverId(chatRelayDto.getReceiverId())
                .content(chatRelayDto.getContent())
                .build();

        String messageJson = objectMapper.writeValueAsString(messageToClient);

        try {
            session.sendMessage(new TextMessage(messageJson));
        } catch (IOException | IllegalStateException e) {
            if (session.isOpen()) {
                session.close(CloseStatus.SESSION_NOT_RELIABLE);
            }
            redisTemplate.delete("chat:user:" + receiverId);
            chatWebSocketHandler.removeSessionByUserId(receiverId);
            notificationService.createChatNotificationIfNotExists(receiverId);
        }

    }
}
