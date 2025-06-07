package kakaotech.bootcamp.respec.specranking.domain.chatparticipation.repository;

import java.util.List;
import kakaotech.bootcamp.respec.specranking.domain.chatparticipation.entity.ChatParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipationRepository extends JpaRepository<ChatParticipation, Long>, ChatParticipationRepositoryCustom {
    List<ChatParticipation> findByChatroomId(Long chatroomId);
}
