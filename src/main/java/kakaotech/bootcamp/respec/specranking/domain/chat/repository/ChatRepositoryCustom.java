package kakaotech.bootcamp.respec.specranking.domain.chat.repository;

import java.util.List;
import java.util.Optional;
import kakaotech.bootcamp.respec.specranking.domain.chat.entity.Chat;

public interface ChatRepositoryCustom {
    List<Chat> findLatestChatsWithCursor(
            Long chatroomId,
            Long cursorId,
            int limitPlusOne
    );

    Optional<Chat> findLatestChatWithCursor(Long chatroomId);
}
