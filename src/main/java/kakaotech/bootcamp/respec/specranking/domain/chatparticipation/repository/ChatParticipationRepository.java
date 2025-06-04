package kakaotech.bootcamp.respec.specranking.domain.chatparticipation.repository;

import kakaotech.bootcamp.respec.specranking.domain.chatparticipation.entity.ChatParticipation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatParticipationRepository extends JpaRepository<ChatParticipation, Long> {
}
