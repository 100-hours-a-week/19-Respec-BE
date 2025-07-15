package kakaotech.bootcamp.respec.specranking.domain.chat.chat.scheduler;

import static kakaotech.bootcamp.respec.specranking.domain.chat.chat.constant.ChatConstant.PING_MESSAGE;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.handler.ChatWebSocketHandler;
import kakaotech.bootcamp.respec.specranking.domain.chat.chat.manager.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.PingMessage;
import org.springframework.web.socket.WebSocketSession;

@Component
@RequiredArgsConstructor
public class PingPongScheduler {

    private final WebSocketSessionManager webSocketSessionManager;
    private final ChatWebSocketHandler chatWebSocketHandler;

    @Scheduled(fixedRate = 30_000)
    public void sendPingToAllSessions() throws Exception {
        Map<Long, WebSocketSession> userSessionMap = webSocketSessionManager.getUserSessionMap();

        for (Map.Entry<Long, WebSocketSession> entry : userSessionMap.entrySet()) {
            WebSocketSession session = entry.getValue();

            try {
                session.sendMessage(new PingMessage(ByteBuffer.wrap(PING_MESSAGE.getBytes())));
            } catch (IOException | IllegalStateException e) {
                if (session.isOpen()) {
                    session.close(CloseStatus.SESSION_NOT_RELIABLE);
                }
                chatWebSocketHandler.afterConnectionClosed(session, CloseStatus.SESSION_NOT_RELIABLE);
            }
        }
    }
}
