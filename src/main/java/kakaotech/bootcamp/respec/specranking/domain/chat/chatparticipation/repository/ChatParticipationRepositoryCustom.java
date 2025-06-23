package kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.repository;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.chat.chatparticipation.entity.ChatParticipation;

public interface ChatParticipationRepositoryCustom {
    List<ChatParticipation> findAllByUserIdOrderByLastChatroomMessage(Long userId);
}
