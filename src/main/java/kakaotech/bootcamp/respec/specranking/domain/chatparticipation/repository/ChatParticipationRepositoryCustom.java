package kakaotech.bootcamp.respec.specranking.domain.chatparticipation.repository;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.chatparticipation.entity.ChatParticipation;

public interface ChatParticipationRepositoryCustom {
    List<ChatParticipation> findAllByUserIdOrderByLastChatroomMessage(Long userId);
}
