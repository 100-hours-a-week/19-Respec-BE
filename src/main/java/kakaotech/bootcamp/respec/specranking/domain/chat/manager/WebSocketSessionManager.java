package kakaotech.bootcamp.respec.specranking.domain.chat.manager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

@Component
public class WebSocketSessionManager {
    private final Map<Long, WebSocketSession> userSessionMap = new ConcurrentHashMap<>();

    public void addSession(Long userId, WebSocketSession session) {
        userSessionMap.put(userId, session);
    }

    public void removeSession(Long userId) {
        userSessionMap.remove(userId);
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

}
